package ee.ciszewsj.cockroachcoin.configuration.properites;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("certificates")
public record CertificatesFileStoreProperties(String myName,
                                              String connectUrl,
                                              String myUrl,
                                              String minerKey,
                                              boolean impostor,
                                              boolean mining) {
}
