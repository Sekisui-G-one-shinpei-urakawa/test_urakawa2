package jp.co.sekisui.batch.zcbzj002;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチメイン
 * （スケルトン実装）
 *
 */
@Slf4j
@SpringBootApplication()
//@Component
//@ComponentScan({"jp.co.sekisui.common.service", "jp.co.sekisui.common.function"})
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@MapperScan(value = { "jp.co.sekisui.batch.base.repository.sk001", "jp.co.sekisui.common.repository.sk001" })
public class App {
    public static void main(String[] args) {
        log.info("start");
        // バッチの実行
        ApplicationContext context = null;
        try {
            //System.exit(SpringApplication.exit(SpringApplication.run(App.class, args)));
            context = SpringApplication.run(App.class, args);
        } catch (Exception e) {
            log.error("App.main() Exception:{}", e.toString());
            log.error("App.main() Exception:{}", e.getLocalizedMessage());
            throw e;
        }

        // 終了コードの出力
        int exitCode = SpringApplication.exit(context);
        log.info("end [System.exit: {}]", exitCode);
        System.exit(exitCode);
    }
}
