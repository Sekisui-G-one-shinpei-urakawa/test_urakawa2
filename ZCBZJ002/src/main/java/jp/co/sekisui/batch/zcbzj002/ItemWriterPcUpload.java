package jp.co.sekisui.batch.zcbzj002;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.io.IOException;
import java.math.BigDecimal;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.adapter.BigDecimalAdapter;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchResponse;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchResponseChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataGsonBuilder;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch.Changeset;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import io.vavr.control.Try;
import jp.co.sekisui.batch.vdm.namespaces.zs4z0001srv.PcUpload;
import jp.co.sekisui.batch.vdm.namespaces.zs4z0001srv.PcUploadFluentHelper;
import jp.co.sekisui.batch.vdm.namespaces.zs4z0001srv.PcUploadCreateFluentHelper;
import jp.co.sekisui.batch.vdm.namespaces.zs4z0001srv.batch.ZS4Z0001SRVServiceBatchChangeSet;
import jp.co.sekisui.batch.vdm.services.DefaultZS4Z0001SRVService;
import jp.co.sekisui.common.entity.sk001.Zz0007T;
import jp.co.sekisui.common.entity.sk001.Zz0009T;
import jp.co.sekisui.common.entity.sk001.Zz0011T;
import jp.co.sekisui.common.function.CVariantValue;
import jp.co.sekisui.common.function.FUtil;
import jp.co.sekisui.common.function.SapMessage;
import jp.co.sekisui.common.function.SapMessageList;
import jp.co.sekisui.common.service.ResultService;
import jp.co.sekisui.common.service.UploadDataService;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 書き出し処理クラス （業務実装）
 */
@Slf4j
@SpringBootApplication
//@ComponentScan("jp.co.sekisui.batch.batch000.service")
@Component
@StepScope
public class ItemWriterPcUpload implements ItemWriter<List<PcUpload>> {
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
     * バリアント変数管理 （スケルトン実装）
     */
    @Autowired
    private CVariantValue cvariantValue;

    @Autowired
    private UploadDataService uploadDataService;

    private static final String DELIMITER = ";";

    /**
     * 登録処理 （業務実装）
     * 
     * @param items 入力データ
     */
    @Override
    public void write(List<? extends List<PcUpload>> items) throws Exception {
        log.debug("START");
        log.debug("items.size():{}", items.size());
        for (int j = 0; j < items.size(); j++) {
            List<PcUpload> itemlist = items.get(j);
            log.info("[{}] write count:{}", prm.getUuid(), itemlist.size());
            try {
                executeRequest(itemlist);
            } catch (Throwable e) {
                log.error("{}", e.toString());
                rsvc.log(prm.getUuid(), "E", "ERR", "000", e.toString());
                throw new Exception(e);
            }
        }
        log.debug("END");
    }

    // ODatav2タイプセーフクライアントAPI
    @Deprecated
    public void executeRequest(List<PcUpload> itemlist) throws Throwable {
        log.debug("START executeRequest");

        // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();
        log.debug("destination:{}", prm.getDestination());
        DefaultZS4Z0001SRVService service = new DefaultZS4Z0001SRVService();

        try {
            List<PcUploadCreateFluentHelper> fluentHelperList = new ArrayList<PcUploadCreateFluentHelper>();
            log.debug("itemlist.size():{}", itemlist.size());
            //ZS4Z0001SRVServiceBatchChangeSet changeset = service.batch().beginChangeSet();
            for (int j = 0; j < itemlist.size(); j++) {
                PcUpload item = itemlist.get(j);
                //changeset = changeset.createPcUpload(item);
                PcUploadCreateFluentHelper fluentHelper = service.createPcUpload(item);
                fluentHelper = fluentHelper.withHeaders(getHeaderInfo());
                fluentHelperList.add(fluentHelper);
                //rsvc.log(prm.getUuid(), resultDataPcUpload(item), "E", "FI", "000", "");
            }
            FluentHelperCreate<?, ?>[] flcr = fluentHelperList
                    .toArray(new FluentHelperCreate[fluentHelperList.size()]);
            log.debug("flcr.length:{}", flcr.length);

            BatchResponse result =
            service.batch().addChangeSet(flcr).executeRequest(destination);
            // BatchResponse result = changeset.endChangeSet().executeRequest(destination);

            Try<BatchResponseChangeSet> changeSetTry = result.get(0);
            if (changeSetTry.isSuccess()) {
                log.debug("SUCCESS executeRequest");
                BatchResponseChangeSet responseChangeSet = changeSetTry.get();
                List createdEntities = responseChangeSet.getCreatedEntities().stream()
                        .map(entity -> (PcUpload) entity).collect(Collectors.toList());
                log.debug("createdEntities:{}", createdEntities);

                List<VdmEntity<?>> entities = responseChangeSet.getCreatedEntities();
                // Object[] a = s.toArray();
                List<Zz0007T> logList = new ArrayList<>();
                for (VdmEntity<?> entity : entities) {
                    logList.add(resultDataPcUpload((PcUpload)entity));
                }
                rsvc.log(logList);

                log.debug("chageSetSuccess :{}", changeSetTry);
                log.debug("chageSetSuccess :{}", changeSetTry.stringPrefix());
                log.debug("chageSetSuccess :{}", changeSetTry.failed());
            } else {
                log.debug("chageSetError :{}", changeSetTry.failed().get());
                log.debug("chageSetError :{}", changeSetTry.stringPrefix());
                log.debug("chageSetError :{}", changeSetTry.failed());
                throw changeSetTry.getCause();
            }
        } catch (Exception e) {
            throw e;
        }
        log.debug("END executeRequest");
    }

    Zz0007T resultDataPcUpload(PcUpload item) { 
        Zz0007T logtbl = new Zz0007T();
        logtbl.setUuid(prm.getUuid());
        logtbl.setSeqno(Integer.valueOf(item.getSeqno()));
        logtbl.setInp001(item.getComm001());
        logtbl.setInp002(item.getComm002());
        logtbl.setInp003(item.getComm003());
        logtbl.setInp004(item.getComm004());
        logtbl.setInp005(item.getComm005());
        logtbl.setInp006(item.getComm006());
        logtbl.setInp007(item.getComm007());
        logtbl.setInp008(item.getComm008());
        logtbl.setInp009(item.getComm009());
        logtbl.setInp010(item.getComm010());
        logtbl.setInp011(item.getComm011());
        logtbl.setInp012(item.getComm012());
        logtbl.setInp013(item.getComm013());
        logtbl.setInp014(item.getComm014());
        logtbl.setInp015(item.getComm015());
        logtbl.setInp016(item.getComm016());
        logtbl.setInp017(item.getComm017());
        logtbl.setInp018(item.getComm018());
        logtbl.setInp019(item.getComm019());
        logtbl.setInp020(item.getComm020());
        logtbl.setInp021(item.getComm021());
        logtbl.setInp022(item.getComm022());
        logtbl.setInp023(item.getComm023());
        logtbl.setInp024(item.getComm024());
        logtbl.setInp025(item.getComm025());
        logtbl.setInp026(item.getComm026());
        logtbl.setInp027(item.getComm027());
        logtbl.setInp028(item.getComm028());
        logtbl.setInp029(item.getComm029());
        logtbl.setInp030(item.getComm030());
        logtbl.setInp031(item.getComm031());
        logtbl.setInp032(item.getComm032());
        logtbl.setInp033(item.getComm033());
        logtbl.setInp034(item.getComm034());
        logtbl.setInp035(item.getComm035());
        logtbl.setInp036(item.getComm036());
        logtbl.setInp037(item.getComm037());
        logtbl.setInp038(item.getComm038());
        logtbl.setInp039(item.getComm039());
        logtbl.setInp040(item.getComm040());
        logtbl.setInp041(item.getComm041());
        logtbl.setInp042(item.getComm042());
        logtbl.setInp043(item.getComm043());
        logtbl.setInp044(item.getComm044());
        logtbl.setInp045(item.getComm045());
        logtbl.setInp046(item.getComm046());
        logtbl.setInp047(item.getComm047());
        logtbl.setInp048(item.getComm048());
        logtbl.setInp049(item.getComm049());
        logtbl.setInp050(item.getComm050());
        logtbl.setInp051(item.getComm051());
        logtbl.setInp052(item.getComm052());
        logtbl.setInp053(item.getComm053());
        logtbl.setInp054(item.getComm054());
        logtbl.setInp055(item.getComm055());
        logtbl.setInp056(item.getComm056());
        logtbl.setInp057(item.getComm057());
        logtbl.setInp058(item.getComm058());
        logtbl.setInp059(item.getComm059());
        logtbl.setInp060(item.getComm060());
        logtbl.setInp061(item.getComm061());
        logtbl.setInp062(item.getComm062());
        logtbl.setInp063(item.getComm063());
        logtbl.setInp064(item.getComm064());
        logtbl.setInp065(item.getComm065());
        logtbl.setInp066(item.getComm066());
        logtbl.setInp067(item.getComm067());
        logtbl.setInp068(item.getComm068());
        logtbl.setInp069(item.getComm069());
        logtbl.setInp070(item.getComm070());
        logtbl.setInp071(item.getComm071());
        logtbl.setInp072(item.getComm072());
        logtbl.setInp073(item.getComm073());
        logtbl.setInp074(item.getComm074());
        logtbl.setInp075(item.getComm075());
        logtbl.setInp076(item.getComm076());
        logtbl.setInp077(item.getComm077());
        logtbl.setInp078(item.getComm078());
        logtbl.setInp079(item.getComm079());
        logtbl.setInp080(item.getComm080());
        logtbl.setInp081(item.getComm081());
        logtbl.setInp082(item.getComm082());
        logtbl.setInp083(item.getComm083());
        logtbl.setInp084(item.getComm084());
        logtbl.setInp085(item.getComm085());
        logtbl.setInp086(item.getComm086());
        logtbl.setInp087(item.getComm087());
        logtbl.setInp088(item.getComm088());
        logtbl.setInp089(item.getComm089());
        logtbl.setInp090(item.getComm090());
        logtbl.setInp091(item.getComm091());
        logtbl.setInp092(item.getComm092());
        logtbl.setInp093(item.getComm093());
        logtbl.setInp094(item.getComm094());
        logtbl.setInp095(item.getComm095());
        logtbl.setInp096(item.getComm096());
        logtbl.setInp097(item.getComm097());
        logtbl.setInp098(item.getComm098());
        logtbl.setInp099(item.getComm099());
        logtbl.setInp100(item.getComm100());
        logtbl.setInp101(item.getComm101());
        logtbl.setInp102(item.getComm102());
        logtbl.setInp103(item.getComm103());
        logtbl.setMessageType(item.getMessageType());
        logtbl.setMessageId(item.getMessageID());
        logtbl.setMessageNo(item.getMessageNumber());
        logtbl.setMessageText(item.getMessageText());
        logtbl.setOut001(item.getOut001());
        logtbl.setOut002(item.getOut002());
        logtbl.setOut003(item.getOut003());
        logtbl.setOut004(item.getOut004());
        logtbl.setOut005(item.getOut005());
        logtbl.setFlgskip(item.getFlgSkip());
        return logtbl;
    }

    private Map<String, String> getHeaderInfo() {
        log.debug("getHeaderInfo");

        // バリアント値データ取得
        List<Zz0011T> variantList = cvariantValue.getVariantValue(prm.getUuid(), prm.getProgram_id(), prm.getVariant_id(), null);
        if (variantList == null || variantList.size() == 0) {
            String[] p = new String[] { prm.getProgram_id(), prm.getVariant_id(), "" };
            log.error("[{}] {}/{}", prm.getUuid(), prm.getProgram_id(), prm.getVariant_id());
            throw new IllegalArgumentException("VariantValue is NULL");
        }

        // PCアップロード対象データ取得
        Zz0009T pcUploadTarget = uploadDataService.select(prm.getUuid());
        if (pcUploadTarget == null) {
            String[] p = new String[] { prm.getUuid() };
            log.error("[{}] {}", prm.getUuid(), "select");
            throw new IllegalArgumentException("UploadData is NULL");
        }

        // バリアント値情報設定
        Map<String, String> resultMap = new HashMap<String, String>();
        for (int i = 0; i < variantList.size(); i++) {
            Zz0011T variant = variantList.get(i);
            StringBuilder key = new StringBuilder();
            key.append("p_fld_");
            key.append(variant.getFieldId().toLowerCase());
            key.append("_");
            key.append(String.format("%02d", variant.getSeqNo()));

            StringBuilder value = new StringBuilder();
            value.append("I");
            value.append(DELIMITER);
            value.append("EQ");
            value.append(DELIMITER);
            value.append(variant.getLow());
            value.append(DELIMITER);
            value.append(variant.getHigh());
            
            resultMap.put(key.toString(), value.toString());
            log.debug("{}:{}", key.toString(), value.toString());
        }

        // アップロードID設定
        resultMap.put("p_upload_id", pcUploadTarget.getUploadId());
        log.debug("p_upload_id:{}", pcUploadTarget.getUploadId());
        // UUID設定
        resultMap.put("p_uuid", prm.getUuid());
        log.debug("p_uuid:{}", prm.getUuid());
        // ユーザID設定
        // TODO
        // resultMap.put("p_online_user_id", prm.getUser_id());
        resultMap.put("p_online_user_id", "FJT171");
        log.debug("p_online_user_id:{}", prm.getUser_id());

        return resultMap;
    }

}
