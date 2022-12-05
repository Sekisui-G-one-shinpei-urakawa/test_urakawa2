package jp.co.sekisui.batch.zcbzj002;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.batch.core.configuration.annotation.StepScope;

import jp.co.sekisui.batch.base.BatchBase;
import jp.co.sekisui.batch.base.ItemInitBase;
import jp.co.sekisui.batch.base.JobExecutionListenerBase;
import jp.co.sekisui.batch.vdm.namespaces.zfjapp38cds007.ZFJAPP38_CDS007;
import jp.co.sekisui.batch.vdm.namespaces.zs4z0001srv.PcUpload;
import lombok.extern.slf4j.Slf4j;

/**
 * バッチメインクラス
 * （業務実装）
 */
@Slf4j
@Configuration
@EnableBatchProcessing
@ComponentScan(value = { "jp.co.sekisui.common.function", "jp.co.sekisui.common.service", "jp.co.sekisui.batch.base.service", "jp.co.sekisui.batch.base"  })
public class BatchZCBZJ002 extends BatchBase {

    /**
     * 
     * （スケルトン実装）
     */
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    /**
     * 
     * （スケルトン実装）
     */
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    /**
     * パラメータ
     * （スケルトン実装）
     */
    @Autowired
    ItemParameterZCBZJ002 prm;

    /**
     * 初期処理処理
     * （スケルトン実装）
     */
    @Autowired
    ItemInitBase initBase;

    /**
     * 初期処理処理
     * （業務実装）
     */
    @Autowired
    ItemInitZCBZJ002 init;

    /**
     * 読み込み処理
     * （業務実装）
     */
    @Autowired
    ItemReaderFileZCBZJ002 reader;

    /**
     * 読み込み処理
     * （業務実装）
     */
    @Autowired
    ItemReaderFilePcUpload readerPcUpload;

    /**
     * 編集処理
     * （業務実装）
     */
    @Autowired
    ItemProcessorZCBZJ002 proc;

    /**
     * 書き出し処理
     * （業務実装）
     */
    @Autowired
    ItemWriterZCBZJ002 writer;

    /**
     * 書き出し処理
     * （業務実装）
     */
    @Autowired
    ItemWriterPcUpload writerPcUpload;
    
    /**
     * 初期処理の生成。
     * （スケルトン実装）
     * 
     * @return 初期処理
     */
    @Bean
    @StepScope
    public Tasklet initBase() {
        log.debug("create initBase");
        return initBase;
    }

    /**
     * 初期処理の生成。
     * （業務実装）
     * 
     * @return 初期処理
     */
    @Bean
    @StepScope
    public Tasklet init() {
        log.debug("create init");
        //Tasklet init = new ItemInitZCBZJ002();
        return init;
    }

    /**
     * 読み込み処理の生成。
     * （業務実装）
     * 
     * @return 読み込み処理
     * @throws Exception
     */
    @Bean
    @StepScope
    public ItemReader<List<PcUpload>> readerPcUpload() throws Exception {
        log.debug("create readerPcUpload");
        readerPcUpload.init();
        return readerPcUpload;
    }

    /**
     * 読み込み処理の生成。
     * （業務実装）
     * 
     * @return 読み込み処理
     * @throws Exception
     */
    @Bean
    @StepScope
    public ItemReader<List<ZFJAPP38_CDS007>> reader() throws Exception {
        log.debug("create reader");
        reader.init();
        return reader;
    }

    /**
     * 編集処理の生成。
     * （業務実装）
     * 
     * @return 編集処理
     */
    @Bean
    public ItemProcessorZCBZJ002 processor() {
        log.debug("create processor");
        //return new ItemProcessorZCBZJ002();
        return proc;
    }

    /**
     * 書き出し処理の生成。
     * （業務実装）
     * 
     * @return 書き出し処理
     */
    @Bean
    @StepScope
    public ItemWriter<List<ZFJAPP38_CDS007>> writer() {
        log.debug("create writer");
        //ItemWriter<List<ZFJAPP38_CDS007>> writer = new ItemWriterZCBZJ002();
        return writer;
    }

    /**
     * 書き出し処理の生成。
     * （業務実装）
     * 
     * @return 書き出し処理
     */
    @Bean
    @StepScope
    public ItemWriter<List<PcUpload>> writerPcUpload() {
        log.debug("create writerPcUpload");
        ItemWriter<List<PcUpload>> writerPcUpload = new ItemWriterPcUpload();
        return writerPcUpload;
    }

    /**
     * 初期ステップを構築する。
     * （スケルトン実装）
     * 
     * @return 初期ステップ
     */
    @Bean
    public Step initStep() {
        log.debug("create initStep");
        return stepBuilderFactory.get("initStep").tasklet(init()).build();
    }

    /**
     * 初期ステップを構築する。
     * （スケルトン実装）
     * 
     * @return 初期ステップ
     */
    @Bean
    public Step initBaseStep() {
        log.debug("create initBaseStep");
        return stepBuilderFactory.get("initBaseStep").tasklet(initBase()).build();
    }

    /**
     * 処理ステップを構築する。
     * （業務実装）
     * 
     * @return 処理ステップ
     * @throws Exception
     */
    @Bean
    public Step step1() throws Exception {
        log.debug("create step1");
        return stepBuilderFactory.get("step1").< List<ZFJAPP38_CDS007>, List<ZFJAPP38_CDS007> >chunk(1).reader(reader())
                .processor(processor()).writer(writer()).build();
    }

    /**
     * 処理ステップを構築する。
     * （業務実装）
     * 
     * @return 処理ステップ
     * @throws Exception
     */
    @Bean
    public Step step2() throws Exception {
        log.debug("create step2");
        return stepBuilderFactory.get("step2").< List<PcUpload>, List<PcUpload> >chunk(1).reader(readerPcUpload())
                .writer(writerPcUpload()).build();
    }

    /**
     * ジョブをを構築する。
     * （スケルトン実装）
     * 
     * @return ジョブ1
     * @throws Exception
     */
    @Bean
    public Job job1() throws Exception {
        log.debug("create job1");
        return jobBuilderFactory.get("job1").incrementer(new RunIdIncrementer()).listener(listener()).start(initBaseStep()).next(initStep())
                // .next(step1()).build();
                .next(step2()).build();
    }

    /**
     * ジョブ実行リスナーの生成。
     * （スケルトン実装）
     * 
     * @return ジョブ実行リスナー
     */
    @Bean
    public JobExecutionListener listener() {
        return new JobExecutionListenerBase();
    }
}
