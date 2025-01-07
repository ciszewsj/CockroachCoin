package ee.ciszewsj.cockroachcoin.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.util.List;

@Slf4j
public record Transaction(
		int index,
		List<FromTransactionField> senders,
		List<ToTransactionField> receivers,
		String prev_hash,
		long timestamp,
		TYPE type
) {
	public enum TYPE {
		GENESIS,
		DEPOSIT,
		TRANSFER,
	}

	public Transaction recalculate(int index, String prev_hash) {
		return new Transaction(index, senders, receivers, prev_hash, timestamp, type);
	}

	public String calculateHash() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();

			String data = index + objectMapper.writeValueAsString(senders) + objectMapper.writeValueAsString(receivers) + timestamp + prev_hash;

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
}
