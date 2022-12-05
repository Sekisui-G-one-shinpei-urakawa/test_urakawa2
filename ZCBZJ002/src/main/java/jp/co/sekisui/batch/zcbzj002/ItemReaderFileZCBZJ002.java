package jp.co.sekisui.batch.zcbzj002;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperExecutable;
import com.sap.cloud.sdk.datamodel.odata.helper.Order;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHeader;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import jp.co.sekisui.batch.vdm.namespaces.zfjapp38cds007.ZFJAPP38_CDS007;
import jp.co.sekisui.batch.vdm.namespaces.zfjapp38cds007.ZFJAPP38_CDS007FluentHelper;
import jp.co.sekisui.batch.vdm.services.DefaultZFJAPP38CDS007Service;
import jp.co.sekisui.common.entity.sk001.Zz0009T;
import jp.co.sekisui.common.function.FUtil;
import jp.co.sekisui.common.function.SapMessage;
import jp.co.sekisui.common.function.SapMessageList;
import jp.co.sekisui.common.service.ResultService;
import jp.co.sekisui.common.service.UploadDataService;
import lombok.extern.slf4j.Slf4j;

/**
 * 読み込み処理クラス （業務実装）
 */
@Slf4j
@Component
@StepScope
public class ItemReaderFileZCBZJ002 implements ItemReader<List<ZFJAPP38_CDS007>> {
    /**
     * メッセージ （スケルトン実装）
     */
    @Autowired
    private FUtil futil;

    /**
     * パラメータ （スケルトン実装）
     */
    @Autowired
    private ItemParameterZCBZJ002 prm;

    /**
     * 処理結果テーブル （スケルトン実装）
     */
    @Autowired
    private ResultService rsvc;

    /**
     * 処理結果テーブル （業務実装）
     */
    @Autowired
    private UploadDataService upload;

    /**
     * データ定義 （業務実装）
     */
    private int entityIndex = 0;
    private List<ZFJAPP38_CDS007> entities1;
    private List<List<ZFJAPP38_CDS007>> entities2;

    /**
     * 処理対象データの取得 （業務実装）
     * 
     * @param prm パラメータ
     */
    // @Autowired
    // public ItemReaderFile(ItemParameterZCBZJ002 prm) throws Exception {
    public void init() throws Exception {
        // this.prm = prm;
        // rsvc = new ResultService();
        log.debug("START");

        Zz0009T data = null;
        try {
            data = upload.select(prm.getUuid());
            log.debug("data.getFilename()={}", data.getFilename());
            log.debug("data.getFiledata()={}", data.getFiledata());

            entities1 = new ArrayList<>();
            String[] linedata = data.getFiledata().split("\r\n");
            for (String line : linedata) {
                ZFJAPP38_CDS007 entity = new ZFJAPP38_CDS007();
                String[] dat = line.split("\t");
                if (dat.length != 21) continue;
                entity.setCompanyCode(dat[0]);
                entity.setFiscalYear(dat[1]);
                entity.setJournalEntry(dat[2]);
                entity.setPostingViewItem(dat[3]);
                entity.setJournalEntryDate(LocalDateTime.parse(dat[4]));
                entity.setPostingDate(LocalDateTime.parse(dat[5]));
                entity.setEnteredOn(LocalDateTime.parse(dat[6]));
                entity.setCreationTime(LocalTime.parse(dat[7]));
                entity.setJournalEntryType(dat[8]);
                entity.setPostingKey(dat[9]);
                entity.setGLAccount(dat[10]);
                entity.setSupplier(dat[11]);
                entity.setAccountType(dat[12]);
                entity.setCurrency(dat[13]);
                entity.setCompanyCodeCrcy(dat[14]);
                entity.setAmountInTransCrcy(new BigDecimal(dat[15]));
                entity.setAmountInCCCrcy(new BigDecimal(dat[16]));
                entity.setText(dat[17]);
                entity.setPaymentMethod(dat[18]);
                entity.setDueCalculationBaseDate(LocalDateTime.parse(dat[19]));
                entity.setDuecalculationbasedate_Open(LocalDateTime.parse(dat[20]));
                entities1.add(entity);
            }
            
            if (entities1 == null || entities1.size() == 0) {
                entities2 = new ArrayList<>();
                log.debug("entities2.size:{}", entities2.size());
                prm.setWarnCount(prm.getWarnCount() + 1);
                log.warn("[{}] {}", prm.getUuid(), futil.getMessage("MSG0006", 0, null));
                return;
            }                
            prm.setInCount(entities1.size());
            log.debug("entities1.size:{}", entities1.size());
            Map<String, List<ZFJAPP38_CDS007>> treeMap = new TreeMap<>();
            for (ZFJAPP38_CDS007 list : entities1) {
                if (treeMap.containsKey(list.getSupplier())) {
                    treeMap.get(list.getSupplier()).add(list);
                } else {
                    List<ZFJAPP38_CDS007> sub = new ArrayList<>();
                    sub.add(list);
                    treeMap.put(list.getSupplier(), sub);
                }
            }
            entities2 = new ArrayList<>(treeMap.values());
            upload.update(prm.getUuid(), "X");
            rsvc.log(prm.getUuid(), ResultService.INFO, "", "", "file name="+ data.getFilename());
            log.debug("entities2:{}", Arrays.toString(entities2.toArray()));

        } catch (Exception e) {
            log.error("ItemReaderFile:{}", e.toString());
            throw e;
        }
        log.debug("END");
    }

    /**
     * 保存してあるエンティティの取得 （業務実装）
     * 
     * @return エンティティ(全て処理済みはnullを返す)
     */
    @Override
    public List<ZFJAPP38_CDS007> read()
            // throws Exception, UnexpectedInputException, ParseException,
            // NonTransientResourceException {
            throws Exception {
        log.debug("START");

        List<ZFJAPP38_CDS007> result = null;
        if (entityIndex < entities2.size()) {
            result = entities2.get(entityIndex);
            entityIndex++;
        }
        if (result != null) {
            log.info("[{}] read count:{}", prm.getUuid(), result.size());
        }
        log.debug("END");
        return result;
    }
}
