package jp.co.sekisui.batch.base;

import javax.sql.DataSource;
import java.lang.String;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.core.env.Environment;
import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.jdbc.CfJdbcEnv;

import lombok.extern.slf4j.Slf4j;

/**
 * データソース構成クラス
 * （スケルトン実装）
 */
@Slf4j
@Component
@Configuration
public class DataSourceConfig {
    @Autowired
    DataSourceProperties dataSourceProperties;

    @Autowired
    private Environment environment;

    HikariDataSource dataSource = null;

    /**
     * データソースを返す。
     * 
     * @return データソース
     */
    public DataSource dataSource() {

        DataSourceProperties properties;
        String profile = String.join("", environment.getActiveProfiles());
        
        if( profile.equals("cloud") ){
            log.debug("Read CF Environment Variables");
            properties = dataSourceProperties();
        }else if( (profile.equals("default")) || (profile.equals("")) ){
            log.debug("Read apprication.yml");
            properties = dataSourceProperties;
        }else{
            log.error("Undefined Profile:{}. Run with Profile:default", profile);
            log.debug("Read apprication.yml");
            properties = dataSourceProperties;
        }
        
        dataSource = new HikariDataSource();
        log.debug("properties {}", properties);
        log.debug("properties.getDriverClassName() {}", properties.getDriverClassName());
        log.debug("properties.getUsername() {}", properties.getUsername());
        System.out.println("#####" + properties.getDriverClassName());
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());

        return dataSource;
    }

	public DataSourceProperties dataSourceProperties() {
        log.debug("dataSourceProperties()");
		CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
		DataSourceProperties properties = new DataSourceProperties();
		CfCredentials hanaCredentials = cfJdbcEnv.findCredentialsByTag("hana");

		if (hanaCredentials != null) {
			String uri = hanaCredentials.getUri("hana");
			properties.setUrl(uri);
			properties.setUsername(hanaCredentials.getUsername());
			properties.setPassword(hanaCredentials.getPassword());
			properties.setDriverClassName(hanaCredentials.getString("driver"));
		}
        return properties;
	}

    // https://docs.spring.io/spring-batch/docs/4.1.x/reference/html/job.html#javaConfig
    @Bean
    public BatchConfigurer batchConfigurer() {
        return new DefaultBatchConfigurer() {
                @Override
                public PlatformTransactionManager getTransactionManager() {
                        //return new MyTransactionManager();
                        return new DataSourceTransactionManager(dataSource);
                    }
        };
    }
    //@Bean
    //public DataSourceTransactionManager transactionManager() {
    //    return new DataSourceTransactionManager(dataSource);
    //}
}
