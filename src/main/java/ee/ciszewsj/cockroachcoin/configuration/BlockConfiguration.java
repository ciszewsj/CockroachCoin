package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
		BlockDto dto = new BlockDto("", new ArrayList<>(), clock.millis(), "");
		List<BlockDto> a = new ArrayList<>();
		a.add(dto);
		return a;
	}

	@Bean
	public String miner(BlockService blockService) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(10);
		executor.setThreadNamePrefix("Miner-");
		executor.initialize();
		for (int i = 0; i < 10; i++) {
			executor.execute(blockService::mine);
		}
		return "";
	}
}
