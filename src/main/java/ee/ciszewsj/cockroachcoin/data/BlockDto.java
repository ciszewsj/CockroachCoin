package ee.ciszewsj.cockroachcoin.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.util.List;

public record BlockDto(
		int index,
		List<Transaction> transactions,
		long timestamp,
		long previousNonce,
		String previousHash
) {
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
