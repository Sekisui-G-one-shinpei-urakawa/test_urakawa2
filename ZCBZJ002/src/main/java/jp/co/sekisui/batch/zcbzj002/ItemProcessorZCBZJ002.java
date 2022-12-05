package jp.co.sekisui.batch.zcbzj002;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.sekisui.batch.vdm.namespaces.zfjapp38cds007.ZFJAPP38_CDS007;
import jp.co.sekisui.common.function.FUtil;
import jp.co.sekisui.common.service.ResultService;
import lombok.extern.slf4j.Slf4j;

/**
 * 編集処理クラス （業務実装）
 */
@Slf4j
@Component
@StepScope
public class ItemProcessorZCBZJ002 implements ItemProcessor<List<ZFJAPP38_CDS007>, List<ZFJAPP38_CDS007>> {
    /**
     * メッセージ （スケルトン実装）
     */
    @Autowired
    private FUtil futil;

    /**
     * パラメータ （スケルトン実装）
     */
    @Autowired
    ItemParameterZCBZJ002 prm;

    /**
     * 処理結果テーブル （スケルトン実装）
     */
    @Autowired
    ResultService rsvc;

    /**
     * 読み込んだデータを編集し、書き出すデータを作成する （業務実装）
     * 
     * @param entity 入力データ
     * @return 出力データ
     */
    @Override
    public List<ZFJAPP38_CDS007> process(final List<ZFJAPP38_CDS007> entity) throws Exception {
        log.debug("START process()");
        log.info("[{}] process count:{}", prm.getUuid(), entity.size());
        log.debug("entity:{}", Arrays.toString(entity.toArray()));

        List<ZFJAPP38_CDS007> list4 = null;
        try {
            // 支払方法を読み替える
            List<ZFJAPP38_CDS007> list1 = replacePaymentMethod(entity);
            // 処理対象がなければ終了
            if ((list1 == null) || (list1.size() == 0)) {
                log.debug("END process():list1={}", list1);
                return null;
            }
            log.info("[{}] process1 count:{}", prm.getUuid(), list1.size());
            log.debug("list1:{}", Arrays.toString(list1.toArray()));

            // 支払基準日を読み替える
            List<ZFJAPP38_CDS007> list2 = replacePaymentDate(list1);
            if ((list2 == null) || (list2.size() == 0)) {
                log.debug("END process():list2={}", list2);
                return null;
            }
            log.info("[{}] process2 count:{}", prm.getUuid(), list2.size());
            log.debug("list2:{}", Arrays.toString(list1.toArray()));

            // 明細テキストに値があるデータを除外する。
            List<ZFJAPP38_CDS007> list3 = checkDetail(list2);
            if ((list3 == null) || (list3.size() == 0)) {
                log.debug("END process():list3={}", list3);
                return null;
            }
            log.info("[{}] process3 count:{}", prm.getUuid(), list3.size());
            log.debug("list3:{}", Arrays.toString(list1.toArray()));

            // 元の明細を相殺して、新たな明細への振替伝票を編集する。
            list4 = createDetail(list3);
            log.info("[{}] process4 count:{}", prm.getUuid(), list4.size());
            log.debug("list4:{}", Arrays.toString(list1.toArray()));
        } catch (Exception e) {
            log.error("process():{}", e.toString());
            throw e;
        }

        return list4;
    }

    /**
     * 債務明細の仕入先合計が支払方法判定額を超えるかどうかで、支払方法を読み替える （業務実装）
     * 
     * @param entity 入力データ
     * @return 読み替え後のデータ
     */
    public List<ZFJAPP38_CDS007> replacePaymentMethod(List<ZFJAPP38_CDS007> entity) {
        log.debug("START replacePaymentMethod()");

        // 仕入先合計を計算
        BigDecimal amount = new BigDecimal("0.0");
        for (ZFJAPP38_CDS007 value : entity) {
            amount = amount.add(value.getAmountInTransCrcy());
            log.debug("amount:{}/{}/{}/{}/{}/{}", value.getJournalEntry(), value.getPostingViewItem(),
                    value.getSupplier(), value.getAmountInTransCrcy(), value.getJournalEntryType(), amount);
            // オーバーフローチェック
            if (amount.compareTo(new BigDecimal("9999999999999")) > 0) {
                prm.setWarnCount(prm.getWarnCount() + 1);
                String msg = futil.getMessage("MSG0003", 0, null);
                rsvc.log(prm.getUuid(), ResultService.ERROR, "MSG0003", "0", msg + ":" + amount);
                log.error("[{}] {}:{}", prm.getUuid(), msg, amount);
                return null;
            }
        }
        BigDecimal comp = new BigDecimal(prm.getCheck_amount());
        // 読み替え後の支払方法を設定
        String aftPayment = "";
        if (amount.compareTo(comp) < 0) {
            aftPayment = prm.getBills();
        } else {
            aftPayment = prm.getBank_transfer();
        }
        List<ZFJAPP38_CDS007> nlist = new ArrayList<>();
        for (ZFJAPP38_CDS007 value : entity) {
            log.debug("Paymentmethod:{}/{}/{}/{}:{}", value.getJournalEntry(), value.getPostingViewItem(),
                    value.getSupplier(), value.getPaymentMethod(), aftPayment);
            // 読み替えないレコードは処理対象外
            if (value.getPaymentMethod().compareTo(aftPayment) != 0) {
                value.setPaymentMethod(aftPayment);
                nlist.add(value);
                log.debug("             :{}/{}/{}/{} Replace", value.getJournalEntry(), value.getPostingViewItem(),
                        value.getSupplier(), value.getPaymentMethod());
            }
        }
        return nlist;
    }

    /**
     * 支払方法が銀行振込の場合、支払基準日が銀行営業日となるように読み替える。（銀行非営業日対応） （業務実装）
     * 
     * @param entity 入力データ
     * @return 読み替え後のデータ
     */
    public List<ZFJAPP38_CDS007> replacePaymentDate(List<ZFJAPP38_CDS007> entity) {
        log.debug("START replacePaymentDate()");
        List<ZFJAPP38_CDS007> nlist = new ArrayList<>();
        for (ZFJAPP38_CDS007 value : entity) {
            log.debug("replacePaymentDate:{}:{}", value.getPaymentMethod(), prm.getPayment_method());
            if (value.getPaymentMethod().compareTo(prm.getPayment_method()) == 0) {
                log.debug("replacePaymentDate bf :{}/{}", value.getSupplier(), value.getDuecalculationbasedate_Open());
                // value.setDuecalculationbasedate_Open(value.getDueCalculationBaseDate());
                value.setDueCalculationBaseDate(value.getDuecalculationbasedate_Open());
                log.debug("replacePaymentDate af :{}/{}", value.getSupplier(), value.getDuecalculationbasedate_Open());
            }
            nlist.add(value);
        }
        return nlist;
    }

    /**
     * 明細テキストに値がある明細を処理対象外とする （業務実装）
     * 
     * @param entity 入力データ
     * @return 読み替え後のデータ
     */
    public List<ZFJAPP38_CDS007> checkDetail(List<ZFJAPP38_CDS007> entity) {
        log.debug("START checkDetail()");
        List<ZFJAPP38_CDS007> nlist = new ArrayList<>();
        for (ZFJAPP38_CDS007 value : entity) {
            if (value.getText().length() == 0) {
                nlist.add(value);
                log.debug("getText ADD:{}/{}/{}/{}", value.getJournalEntry(), value.getPostingViewItem(),
                        value.getSupplier(), value.getText());
            } else {
                log.debug("getText DEL:{}/{}/{}/{}", value.getJournalEntry(), value.getPostingViewItem(),
                        value.getSupplier(), value.getText());
            }
        }
        return nlist;
    }

    /**
     * 元の明細を相殺して、新たな明細への振替伝票を編集する （業務実装）
     * 
     * @param entity 入力データ
     * @return 登録用のデータ
     */
    public List<ZFJAPP38_CDS007> createDetail(List<ZFJAPP38_CDS007> entity) {
        log.debug("START createDetail()");
        // 伝票番号 Accountingdocument
        // 明細番号 Accountingdocumentitem
        List<ZFJAPP38_CDS007> nlist = new ArrayList<>();
        int cnt = 1;
        for (ZFJAPP38_CDS007 value : entity) {
            String newacc = String.format("#%05d", cnt);
            // ④新しい値の明細
            ZFJAPP38_CDS007 entry = new ZFJAPP38_CDS007();
            entry.setSupplier(value.getSupplier());
            entry.setJournalEntry(newacc);
            entry.setPostingViewItem("002");
            // entry.setCurrency(value.getAccountType());
            entry.setCurrency("JPY");
            entry.setAmountInTransCrcy(value.getAmountInTransCrcy());
            entry.setPaymentMethod(value.getPaymentMethod());
            // entry.setDueCalculationBaseDate(value.getDuecalculationbasedate_Open());
            entry.setDueCalculationBaseDate(value.getDueCalculationBaseDate());
            entry.setCompanyCode(value.getCompanyCode());
            entry.setFiscalYear(value.getFiscalYear());

            // ③相殺する明細
            value.setText(value.getJournalEntry());
            value.setJournalEntry(newacc);
            value.setPostingViewItem("001");
            // value.setCurrency(value.getAccountType());
            value.setCurrency("JPY");
            value.setAmountInTransCrcy(value.getAmountInTransCrcy().multiply(new BigDecimal("-1")));

            // test用
            // if (cnt > 0) {
            // value.setText("");
            // value.setAmountInTransCrcy(value.getAmountInTransCrcy().multiply(new
            // BigDecimal("-2")));
            // }

            nlist.add(value);
            log.debug("value:{}/{}/{}/{}/{}/{}/{}", value.getSupplier(), value.getJournalEntry(),
                    value.getPostingViewItem(), value.getAmountInTransCrcy(), value.getPaymentMethod(),
                    value.getDueCalculationBaseDate(), value.getText());
            nlist.add(entry);
            log.debug("entry:{}/{}/{}/{}/{}/{}/{}", entry.getSupplier(), entry.getJournalEntry(),
                    entry.getPostingViewItem(), entry.getAmountInTransCrcy(), entry.getPaymentMethod(),
                    entry.getDueCalculationBaseDate(), entry.getText());
            cnt++;
        }
        return nlist;
    }
}
