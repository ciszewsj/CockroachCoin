package ee.ciszewsj.cockroachcoin.configuration.properites;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("certificates")
public record CertificatesFileStoreProperties(String myName,
                                              String privateKey,
                                              String publicKey,
                                              String connectUrl,
                                              String myUrl,
                                              String path) {
}
