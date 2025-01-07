package ee.ciszewsj.cockroachcoin.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.util.List;

@Slf4j
public record BlockDto(
		int index,
		List<Transaction> transactions,
		long timestamp,
		long previousNonce,
		String previousHash
) {
	public BlockDto impostorHash() {
		return new BlockDto(index, transactions, timestamp, previousNonce, "0000000000000000");
	}

	public String calculateHash(long nonce) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();

			String data = index + previousHash + timestamp + objectMapper.writeValueAsString(transactions) + nonce;

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(data.getBytes());

			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException("Błąd przy generowaniu hasha", e);
		}
	}

	public boolean validateHash(String hash, long nonce) {
		return hash.equals(calculateHash(nonce));
	}
}
