package ee.ciszewsj.cockroachcoin.service;

import ee.ciszewsj.cockroachcoin.configuration.properties.CertificatesFileStoreProperties;
import io.swagger.v3.core.util.Json;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.INTERNAL_SERVER_EXCEPTION;
import static ee.ciszewsj.cockroachcoin.configuration.GlobalExceptionHandler.UNAUTHORIZED_EXCEPTION;

@Slf4j
@Service
@EnableConfigurationProperties(CertificatesFileStoreProperties.class)
public class CertificatesService {
	private final CertificatesFileStoreProperties properties;

	public CertificatesService(CertificatesFileStoreProperties properties) {
		Security.addProvider(new BouncyCastleProvider());
		this.properties = properties;
	}

	public void verifyObjectWithSignature(String owner, Object object, String encodedSignature) {
		try {
			String signedObject;
			if (object.getClass().equals(String.class)) {
				signedObject = (String) object;
			} else {
				signedObject = Json.mapper().writeValueAsString(object);
			}
			PublicKey publicKey = readPublicKey(owner);

			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(publicKey);
			signature.update(signedObject.getBytes(StandardCharsets.UTF_8));

			byte[] signatureBytes = Base64.getDecoder().decode(encodedSignature);

			boolean isVerified;
			try {
				isVerified = signature.verify(signatureBytes);
			} catch (SignatureException e) {
				log.warn("Signature is not valid [owner={}, signedObject={}, encodedSignature={}]", owner, signedObject, encodedSignature, e);
				return;
			}
			if (isVerified) {
				log.info("Signature is correct [owner={}]", owner);
			} else {
				log.warn("Signature is not valid [owner={}, signedObject={}, encodedSignature={}]", owner, signedObject, encodedSignature);
				throw UNAUTHORIZED_EXCEPTION;
			}
		} catch (HttpStatusCodeException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error during verifying object [owner={}, object={}, signature={}]", owner, object, encodedSignature, e);
			throw INTERNAL_SERVER_EXCEPTION;
		}
	}

	public void savePublicKey(String owner, String publicKey) throws IOException {
		String path = properties.path() + "/" + owner + TYPE.PUBLIC.suffix;

		Files.writeString(Path.of(path), publicKey);
	}

	public String readPublicKeyString(String owner) throws IOException {
		String path = properties.path() + "/" + owner + TYPE.PUBLIC.suffix;

		return Files.readString(Path.of(path));
	}

	private PublicKey readPublicKey(String owner) throws Exception {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		String path = properties.path() + "/" + owner + TYPE.PUBLIC.suffix;

		try (FileReader keyReader = new FileReader(path);
		     PemReader pemReader = new PemReader(keyReader)) {

			PemObject pemObject = pemReader.readPemObject();
			byte[] content = pemObject.getContent();
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
			return factory.generatePublic(pubKeySpec);
		}
	}

	@Getter
	enum TYPE {
		PRIVATE("_priv.pem"),
		PUBLIC("_pub.pem");
		private final String suffix;

		TYPE(String suffix) {
			this.suffix = suffix;
		}
	}
}
