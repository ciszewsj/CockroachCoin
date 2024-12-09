package ee.ciszewsj.cockroachcoin.configuration;

import ee.ciszewsj.cockroachcoin.configuration.properites.CertificatesFileStoreProperties;
import ee.ciszewsj.cockroachcoin.service.AccountService;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import ee.ciszewsj.cockroachcoin.service.MinerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class MinerConfiguration {

	@Bean
	public MinerService miner(BlockService service, AccountService accountService, Clock clock, CertificatesFileStoreProperties properties) {
		return new MinerService(clock, accountService, service, properties);
	}
}
