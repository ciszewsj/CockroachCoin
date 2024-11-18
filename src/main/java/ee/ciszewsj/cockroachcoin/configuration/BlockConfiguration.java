package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class BlockConfiguration {

	@Bean
	public BlockService blockService(List<BlockDto> blockList) {
		return new BlockService(blockList);
	}

	@Bean
	public List<BlockDto> blockList(Clock clock) {
		BlockDto dto = new BlockDto(0, new ArrayList<>(), clock.millis(), 0, "");
		List<BlockDto> a = new ArrayList<>();
		a.add(dto);
		return a;
	}
}
