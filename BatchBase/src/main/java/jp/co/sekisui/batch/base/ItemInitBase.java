package jp.co.sekisui.batch.base;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import jp.co.sekisui.common.entity.sk001.Zz0013T;
import jp.co.sekisui.common.function.CFixedValue;
import jp.co.sekisui.common.function.CJobDate;
import jp.co.sekisui.common.function.CQueuing;
import jp.co.sekisui.common.function.CVariantValue;
import jp.co.sekisui.common.function.FUtil;
import jp.co.sekisui.common.service.ResultService;
import jp.co.sekisui.common.service.UploadDataService;
import jp.co.sekisui.common.entity.sk001.Zz0009T;
import lombok.extern.slf4j.Slf4j;

/**
 * 初期処理クラス （スケルトン実装）
 */
@Slf4j
@Component
@StepScope
public class ItemInitBase implements Tasklet {
    /**
     * パラメータ
     */
    @Autowired
    ItemParameterBase prm;

    /**
     * 固定値取得
     */
    @Autowired
    CFixedValue cfixedValue;

    /**
     * バリアント取得
     */
    @Autowired
    CVariantValue cvariantdValue;

    /**
     * JOB日付取得
     */
    @Autowired
    CJobDate cjobdate;

    /**
     * メッセージ
     */
    @Autowired
    FUtil futil;

    /**
     * 処理結果テーブル出力
     */
    @Autowired
    ResultService rsvc;

    /**
     * 起動パラメータ（プログラムID）
     */
    @Value("#{jobParameters[pid]}")
    String pid;

    /**
     * 起動パラメータ（バリアントID）
     */
    @Value("#{jobParameters[vid]}")
    String vid;

    /**
     * 起動パラメータ（オンバッチ起動）
     */
    @Value("#{jobParameters[onbatch]}")
    String onbatch;

    /**
     * 処理結果テーブル出力
     */
    @Autowired
    CQueuing cque;

    /**
     * プロパティ
     */
    // @Autowired
    // SampleProperty sampleProperty;

    /**
     * PCアップロード処理対象テーブル （業務実装）
     */
    @Autowired
    private UploadDataService upload;

    //S4用接続先
    private static final String DESTINATION = "s4job"; 

    /**
     * コンストラクタ
     */
    public ItemInitBase() {
    }

    /**
     * 初期処理の実装
     * 
     * @param contribution
     * @param chunkContext
     * @return CONTINUABLE（処理続行）、FINISHED（処理終了）
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.debug("START");
        
        Zz0009T data = null;
        
        try {
            // 起動パラメータの取得
            prm.setProgram_id(pid);
            prm.setVariant_id(vid);
            prm.setOnbatch(onbatch);

            // オンバッチ起動の場合はキュー取得
            if (prm.getOnbatch() != null) {
                Zz0013T que = cque.getQueuing(DESTINATION);
                log.debug("que:{}", que);
                if (que == null) {
                    throw new IOException("getQueuing():null");
                }
                prm.setUuid(que.getUuid());
                prm.setUser_id(que.getQueuingUserId());
                prm.setQueuingstatus(que.getQueuingStatus());
            } else {
                // UUID取得
                UUID uuid = UUID.randomUUID();
                prm.setUuid(uuid.toString());
            }
            // テスト用
            // prm.setUuid("76eb84ef-1b7b-4fa5-8377-f3f41f6b9955");

            data = upload.select(prm.getUuid());

            //rsvc.start(prm.getUuid(), "jobid", prm.getProgram_id(), prm.getVariant_id(), new Date(), prm.getUser_id());
            rsvc.start(prm.getUuid(), "jobid", prm.getProgram_id(), prm.getVariant_id(), new Date(), prm.getUser_id(), data.getFilename());
            //rsvc.start(prm.getUuid(), "jobid", prm.getProgram_id(), prm.getVariant_id(), new Date(), prm.getUser_id(), null);

            log.info("[{}] {}", prm.getUuid(), "getUuid");
        } catch (Exception e) {
            log.error("[{}] ItemInit000:{}", prm.getUuid(), e.toString());
            throw e;
        }

        try {
            // 言語の取得
            prm.setLang(Locale.getDefault().getLanguage());
            log.info("[{}] LANG:{}", prm.getUuid(), prm.getLang());

            log.info("[{}] getProgram_id()={} getVariant_id()={} getOnbatch()={}", prm.getUuid(), prm.getProgram_id(),
                    prm.getVariant_id(), prm.getOnbatch());

            // コミット間隔
            // prm.setRecCount(Integer.parseInt(sampleProperty.getRecCount()));
            // log.debug("getRecCount:{}", sampleProperty.getRecCount());

            //業務日付取得
            String jobdate = cjobdate.getJobDate(DESTINATION, "YTEST_TVARVC_DATE");
            // String jobdate = "20221116";
            DateTimeFormatter dtFt = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
            prm.setJob_date(LocalDateTime.parse(jobdate + " 00:00:00", dtFt));
            log.info("[{}] getJob_date={}", prm.getUuid(), prm.getJob_date());
 
            // Destination
             prm.setDestination(DESTINATION);
        } catch (Exception e) {
            log.error("[{}] ItemInit000:{}", prm.getUuid(), e.toString());
            throw e;
        }
        log.debug("END");
        return RepeatStatus.FINISHED;
    }

}
