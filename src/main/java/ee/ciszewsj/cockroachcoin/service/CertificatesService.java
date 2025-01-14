package ee.ciszewsj.cockroachcoin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.data.FromTransactionField;
import ee.ciszewsj.cockroachcoin.data.Transaction;
import ee.ciszewsj.cockroachcoin.data.request.TransactionRequest;
import io.swagger.v3.core.util.Json;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.INTERNAL_SERVER_EXCEPTION;
import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.UNAUTHORIZED_EXCEPTION;

@Slf4j
@Service
@EnableConfigurationProperties(CertificatesFileStoreProperties.class)
public class CertificatesService {

	public CertificatesService() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@SneakyThrows
	public void verifyObjectWithSignature(TransactionRequest request) {

		for (int i = 0; i < request.senders().size(); i++) {
			TransactionRequest.FromTransactionRequest transactionRequest = request.senders().get(i);
			String ver = new ObjectMapper().writeValueAsString(transactionRequest.decode(i, request.timestamp(), request.receivers()));
			log.warn("OBJ_TO_VERIFY [obj={}] ", ver);
			verifyObjectWithSignature(transactionRequest.senderKey(), ver, transactionRequest.signature());
		}

		log.info("Successful verify transaction with keys");
	}

	@SneakyThrows
	public void verifyObjectWithSignature(Transaction request) {

		for (int i = 0; i < request.senders().size(); i++) {
			FromTransactionField transactionRequest = request.senders().get(i);
			String ver = new ObjectMapper().writeValueAsString(transactionRequest.decode(i, request.timestamp(), request.receivers()));
			log.warn("OBJ_TO_VERIFY [obj={}] ", ver);
			verifyObjectWithSignature(transactionRequest.senderKey(), ver, transactionRequest.signature());
		}

		log.info("Successful verify transaction with keys");
	}

	public void verifyObjectWithSignature(String ownerKey, Object object, String encodedSignature) {
		try {
			String signedObject;
			if (object.getClass().equals(String.class)) {
				signedObject = (String) object;
			} else {
				signedObject = Json.mapper().writeValueAsString(object);
			}
			PublicKey publicKey = readPublicKey(ownerKey);

			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(publicKey);
			signature.update(signedObject.getBytes(StandardCharsets.UTF_8));

			byte[] signatureBytes = Base64.getDecoder().decode(encodedSignature);

			boolean isVerified;
			try {
				isVerified = signature.verify(signatureBytes);
			} catch (SignatureException e) {
				log.warn("Signature is not valid [owner={}, signedObject={}, encodedSignature={}]", ownerKey, signedObject, encodedSignature, e);
				throw UNAUTHORIZED_EXCEPTION;
			}
			if (isVerified) {
				log.info("Signature is correct [owner={}]", ownerKey);
			} else {
				log.warn("Signature is not valid [owner={}, signedObject={}, encodedSignature={}]", ownerKey, signedObject, encodedSignature);
				throw UNAUTHORIZED_EXCEPTION;
			}
		} catch (HttpStatusCodeException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error during verifying object [owner={}, object={}, signature={}]", ownerKey, object, encodedSignature, e);
			throw INTERNAL_SERVER_EXCEPTION;
		}
	}

	private PublicKey readPublicKey(String ownerKey) throws Exception {
		String publicKeyPEM = ownerKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");

		byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
		return keyFactory.generatePublic(keySpec);
	}
}
