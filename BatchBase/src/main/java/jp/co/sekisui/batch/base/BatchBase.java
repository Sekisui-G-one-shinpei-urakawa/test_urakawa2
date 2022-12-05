package jp.co.sekisui.batch.base;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチ構成クラス
 * （スケルトン実装）
 */
@Slf4j
public class BatchBase {

    @Autowired(required = false)
    DataSourceConfig dsConfig;

    public BatchBase() {
    }

    /**
     * SQLセッションのファクトリーを返す。
     * 
     * @return ファクトリー
     * @throws Exception ファクトリーの生成に失敗した場合
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        log.debug("create factoryBean");
        //log.debug("##### dsConfig.dataSourceProperties.getDriverClassName() {}", dsConfig.dataSourceProperties.getDriverClassName());
        factoryBean.setDataSource(dsConfig.dataSource());
        return factoryBean.getObject();
    }

}
