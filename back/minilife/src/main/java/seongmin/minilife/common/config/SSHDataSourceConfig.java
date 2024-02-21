package seongmin.minilife.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SSHDataSourceConfig {
    private final SSHTunnelConfig sshTunnelConfig;

    @Value("${DB_URL}")
    private String url;
    @Value("${DB_USER}")
    private String username;
    @Value("${DB_PASSWORD}")
    private String password;

    @Bean(name="dataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        Integer forwardedPort = sshTunnelConfig.connectSSH();  // ssh 연결 및 터널링 설정
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("com.mysql.cj.jdbc.Driver") // todo - env
                .build();
    }
}
