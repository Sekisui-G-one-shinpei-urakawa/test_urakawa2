package jp.co.sekisui.batch.zcbzj002;

import java.io.IOException;
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
public class ItemReaderZCBZJ002 implements ItemReader<List<ZFJAPP38_CDS007>> {
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
    // public ItemReaderZCBZJ002(ItemParameterZCBZJ002 prm) throws Exception {
    public void init() throws Exception {
        // this.prm = prm;
        // rsvc = new ResultService();
        log.debug("START");

        // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();

        // 処理対象データの取得
        ZFJAPP38_CDS007FluentHelper flHelper;

        try {
            log.debug("destination={}", destination);

            // フィルタ条件作成
            String docs[] = prm.getDocument_type();
            ExpressionFluentHelper<ZFJAPP38_CDS007> filter = null;
            for (String doc : docs) {
                ExpressionFluentHelper<ZFJAPP38_CDS007> condition = ZFJAPP38_CDS007.JOURNAL_ENTRY_TYPE.eq(doc);
                if (filter != null) {
                    filter = filter.or(condition);
                } else {
                    filter = condition;
                }
            }

            flHelper = new DefaultZFJAPP38CDS007Service().getAllZFJAPP38_CDS007();
            HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

            flHelper.filter(ZFJAPP38_CDS007.COMPANY_CODE.eq(prm.getCompany_code()))
                    .filter(ZFJAPP38_CDS007.ACCOUNT_TYPE.eq(prm.getFinancialaccount_type()))
                    .filter(ZFJAPP38_CDS007.ENTERED_ON.le(prm.getJob_date())).filter(filter)
                    .withHeader("para_calendar_id", prm.getCalendar_id())
                    .orderBy(ZFJAPP38_CDS007.COMPANY_CODE, Order.ASC).orderBy(ZFJAPP38_CDS007.ACCOUNT_TYPE, Order.ASC)
                    .orderBy(ZFJAPP38_CDS007.JOURNAL_ENTRY_TYPE, Order.ASC)
                    .orderBy(ZFJAPP38_CDS007.CREATION_TIME, Order.ASC);

            ODataRequestResultGeneric result = flHelper.toRequest().execute(httpClient);
            HttpResponse hs = result.getHttpResponse();
            log.debug("hs:{}", hs);
            int sc = hs.getStatusLine().getStatusCode();

            Header[] Sap_Message = hs.getHeaders("Sap-Message");
            // Sap_Message[0] = new BasicHeader("Sap-Message",
            // "{\"code\":\"RW/033\",\"message\":\"Accounting
            // QQQQQ\",\"severity\":\"info\",\"details\":[{\"code\":\"RW/609\",\"message\":\"Error
            // 123\",\"severity\":\"error\"},{\"code\":\"RW/111\",\"message\":\"Error
            // AAA\",\"severity\":\"error\"}]}]");
            SapMessageList sapmsglist = new SapMessageList(Sap_Message);
            for (SapMessage sapmsg : sapmsglist) {
                String Sap_Message_id = sapmsg.getId();
                String Sap_Message_code = sapmsg.getCode();
                String Sap_Message_severity = sapmsg.getSeverity();
                String Sap_Message_message = sapmsg.getMessage();
                // for debug: Sap_Message_severity = SapMessage.ERROR;
                if (Sap_Message_severity.equals(SapMessage.INFO) || Sap_Message_severity.equals(SapMessage.SUCCESS)) {
                    log.info("[{}] {} {} {}", prm.getUuid(), Sap_Message_code, Sap_Message_severity,
                            Sap_Message_message);
                    rsvc.log(prm.getUuid(), ResultService.INFO, Sap_Message_id, Sap_Message_code, Sap_Message_message);
                } else if (Sap_Message_severity.equals(SapMessage.WARNING)) {
                    prm.setWarnCount(prm.getWarnCount() + 1);
                    log.warn("[{}] {} {} {}", null, prm.getUuid(), Sap_Message_code, Sap_Message_severity,
                            Sap_Message_message);
                    rsvc.log(prm.getUuid(), ResultService.WARN, Sap_Message_id, Sap_Message_code, Sap_Message_message);
                } else if (Sap_Message_severity.equals(SapMessage.ERROR)) {
                    prm.setInErrorCount(prm.getInErrorCount() + 1);
                    log.error("[{}] {} {} {}", prm.getUuid(), Sap_Message_code, Sap_Message_severity,
                            Sap_Message_message);
                    rsvc.log(prm.getUuid(), ResultService.ERROR, Sap_Message_id, Sap_Message_code, Sap_Message_message);
                }
            }
            if (prm.getInErrorCount() > 0) {
                throw new IOException(futil.getMessage("MSG0005", 0, null));
            }

            log.debug("getStatusCode:{}", sc);
            if ((sc >= 200) && (sc < 300)) {
                entities1 = result.asList(ZFJAPP38_CDS007.class);
                log.info("entities1={}", entities1);
            } else {
                throw new IOException("sc:" + sc);
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
            log.debug("entities2:{}", Arrays.toString(entities2.toArray()));

        } catch (Exception e) {
            log.error("ItemReaderZCBZJ002:{}", e.toString());
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
