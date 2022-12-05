package jp.co.sekisui.batch.zcbzj002;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import jp.co.sekisui.batch.base.ItemInitBase;
import jp.co.sekisui.common.entity.sk001.CmCommonParameter;
import jp.co.sekisui.common.entity.sk001.Zz0011T;
import jp.co.sekisui.common.function.CFixedValue;
import jp.co.sekisui.common.function.CVariantValue;
import jp.co.sekisui.common.function.FUtil;
import jp.co.sekisui.common.service.ResultService;
import lombok.extern.slf4j.Slf4j;

/**
 * 初期処理クラス （業務実装）
 */
@Slf4j
@Component
@StepScope
// public class ItemInitZCBZJ002 extends ItemInitBase {
public class ItemInitZCBZJ002 implements Tasklet {
    /**
     * パラメータ （スケルトン実装）
     */
    @Autowired
    ItemParameterZCBZJ002 prm;

    /**
     * 固定値管理 （スケルトン実装）
     */
    @Autowired
    CFixedValue cfixedValue;

    /**
     * バリアント変数管理 （スケルトン実装）
     */
    @Autowired
    CVariantValue cvariantdValue;

    /**
     * メッセージ （スケルトン実装）
     */
    @Autowired
    FUtil futil;

    /**
     * 処理結果テーブル出力 （スケルトン実装）
     */
    @Autowired
    ResultService rsvc;

    //S4用接続先
    private static final String DESTINATION = "s4job"; 

    /**
     * コンストラクタ （業務実装）
     */
    public ItemInitZCBZJ002() {
    }

    /**
     * 初期処理の実装 （業務実装）
     * 
     * @param contribution
     * @param chunkContext
     * @return CONTINUABLE（処理続行）、FINISHED（処理終了）
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // super.execute(contribution, chunkContext);
        log.debug("START");
        // if (true) throw new Exception("test");
        try {
            // from 変更
            // 共通情報：固定値テーブルの取得。
            List<CmCommonParameter> fvalue = null;

            // 勘定タイプ
            fvalue = this.getFixedValue(prm.getProgram_id(), "KOART", null, null);
            if (fvalue == null || fvalue.size() == 0) {
                String[] p = new String[] { prm.getProgram_id(), "KOART", null, null };
                log.error("[{}] {}/{}", prm.getUuid(), prm.getProgram_id(), p);
                throw new IllegalArgumentException("Financialaccount_type:" + fvalue);
            }
            prm.setFinancialaccount_type(fvalue.get(0).getValue1());
            log.debug("getFinancialaccount_type={}", prm.getFinancialaccount_type());

            // カレンダーID
            fvalue = this.getFixedValue(prm.getProgram_id(), "FCALID", null, null);
            if (fvalue == null || fvalue.size() == 0) {
                String[] p = new String[] { prm.getProgram_id(), "FCALID", null, null };
                log.error("[{}] {}/{}", prm.getUuid(), prm.getProgram_id(), p);
                throw new IllegalArgumentException("Calendar_id:" + fvalue);
            }
            prm.setCalendar_id(fvalue.get(0).getValue1());
            log.debug("getCalendar_id={}", prm.getCalendar_id());

            // 支払方法判定額
            fvalue = this.getFixedValue(prm.getProgram_id(), "DMBTR", "BODER", null);
            if (fvalue == null || fvalue.size() == 0) {
                String[] p = new String[] { prm.getProgram_id(), "DMBTR", "BODER", null };
                log.error("[{}] {}/{}", prm.getUuid(), prm.getProgram_id(), p);
                throw new IllegalArgumentException("Check_amount:" + fvalue);
            }
            prm.setCheck_amount(fvalue.get(0).getValue1());
            log.debug("getCheck_amount={}", prm.getCheck_amount());

            // 支払方法種別 50万円以上＝手形：DAT1の値に変更。50万円未満＝銀行振込：DAT2の値に変更。
            fvalue = this.getFixedValue(prm.getProgram_id(), "ZLSCH", "AFTER", null);
            if (fvalue == null || fvalue.size() == 0) {
                String[] p = new String[] { prm.getProgram_id(), "ZLSCH", "AFTER", null };
                log.error("{}/{}", prm.getProgram_id(), p);
                throw new IllegalArgumentException("Bills:" + fvalue);
            }
            prm.setBills(fvalue.get(0).getValue1());
            log.debug("getBills={}", prm.getBills());
            prm.setBank_transfer(fvalue.get(0).getValue2());
            log.debug("getBank_transfer={}", prm.getBank_transfer());

            // 支払方法判定種別
            fvalue = this.getFixedValue(prm.getProgram_id(), "ZLSCH", "TRANSFER", null);
            if (fvalue == null || fvalue.size() == 0) {
                String[] p = new String[] { prm.getProgram_id(), "ZLSCH", "TRANSFER", null };
                log.error("[{}] {}/{}", prm.getUuid(), prm.getProgram_id(), p);
                throw new IllegalArgumentException("Payment_method:" + fvalue);
            }
            prm.setPayment_method(fvalue.get(0).getValue1());
            log.debug("getPayment_method={}", prm.getPayment_method());
/*
            // 共通情報：バリアントテーブルの取得。
            // 会社コード
            String prmuuid = "0";
            if (prm.getOnbatch() != null) {
                prmuuid = prm.getUuid();
            }
            List<Zz0011T> vvalue = cvariantdValue.getVariantValue(prmuuid, prm.getProgram_id(), prm.getVariant_id(),
                    "BUKRS");
            if (vvalue == null || vvalue.size() == 0) {
                String[] p = new String[] { prm.getProgram_id(), prm.getVariant_id(), "BUKRS" };
                log.error("[{}] {}/{}", prm.getUuid(), prm.getProgram_id(), p);
                throw new IllegalArgumentException("Company_code:" + vvalue);
            }
            prm.setCompany_code(vvalue.get(0).getLow());
            log.debug("getCompany_code={}", prm.getCompany_code());

            // 伝票タイプ
            vvalue = cvariantdValue.getVariantValue(prmuuid, prm.getProgram_id(), prm.getVariant_id(), "BLART");
            if (vvalue == null || vvalue.size() == 0) {
                String[] p = new String[] { prm.getProgram_id(), "BLART" };
                log.error("[{}] {}/{}", prm.getUuid(), prm.getProgram_id(), p);
                throw new IllegalArgumentException("Document_type:" + vvalue);
            }
            String str[] = new String[vvalue.size()];
            for (int i = 0; i < vvalue.size(); ++i) {
                str[i] = vvalue.get(i).getLow();
            }
            prm.setDocument_type(str);
            log.debug("getDocument_type={}", Arrays.toString(prm.getDocument_type()));
 */
        } catch (Exception e) {
            log.error("[{}] ItemInitZCBZJ002:{}", prm.getUuid(), e.toString());
            throw e;
        }
        log.debug("END");
        return RepeatStatus.FINISHED;
    }

    /**
    * 固定値取得
    * <br>
    * S4の共通パラメータサービスから、条件(ProgramId, ParamId, key1, key2)に一致したレコードを取得します。
     * @param ProgramId プログラムID
     * @param ParamId   パラメータID
     * @param key1 キー1
     * @param key2 キー2
     * @return 固定値情報
    */
    private List<CmCommonParameter> getFixedValue(String ProgramId, String ParamId, String key1, String key2) {
        // 固定値情報取得
        return cfixedValue.getFixedValue(DESTINATION, ProgramId, ParamId, "", "JA", key1, key2, "", "");
    }

}
