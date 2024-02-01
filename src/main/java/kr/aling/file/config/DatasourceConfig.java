package kr.aling.file.config;

import javax.sql.DataSource;
import kr.aling.file.common.properties.DatabaseProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datasource Config class.
 *
 * @author 박경서
 * @since 1.0
 **/
@Configuration
@RequiredArgsConstructor
public class DatasourceConfig {

    private final DatabaseProperties databaseProperties;


    /**
     * DBCP2 Bean 설정.
     *
     * @return DBCP2
     */
    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(databaseProperties.getDriver());
        dataSource.setUrl(databaseProperties.getUrl());
        dataSource.setUsername(databaseProperties.getUsername());
        dataSource.setPassword(databaseProperties.getPassword());

        dataSource.setInitialSize(databaseProperties.getInitialSize());
        dataSource.setMaxTotal(databaseProperties.getMaxTotal());
        dataSource.setMinIdle(databaseProperties.getMinIdle());
        dataSource.setMaxIdle(databaseProperties.getMaxIdle());
        dataSource.setMaxWaitMillis(databaseProperties.getMaxWait());

        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setValidationQuery(databaseProperties.getQuery());

        return dataSource;
    }
}
