package jp.co.sekisui.batch.zcbzj002;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import jp.co.sekisui.batch.vdm.namespaces.zfjapp38cds007.ZFJAPP38_CDS007;
import jp.co.sekisui.batch.vdm.namespaces.zfjapp38cds007.ZFJAPP38_CDS007CreateFluentHelper;
import jp.co.sekisui.batch.vdm.namespaces.zfjapp38cds007.ZFJAPP38_CDS007UpdateFluentHelper;
import jp.co.sekisui.batch.vdm.namespaces.zfjapp38cds007.batch.ZFJAPP38CDS007ServiceBatchChangeSet;
import jp.co.sekisui.batch.vdm.services.DefaultZFJAPP38CDS007Service;
import jp.co.sekisui.common.function.FUtil;
import jp.co.sekisui.common.function.SapMessage;
import jp.co.sekisui.common.function.SapMessageList;
import jp.co.sekisui.common.service.ResultService;
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
public class ItemWriterZCBZJ002 implements ItemWriter<List<ZFJAPP38_CDS007>> {
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
    ResultService rsvc;

    /**
     * 登録処理 （業務実装）
     * 
     * @param items 入力データ
     */
    @Override
    public void write(List<? extends List<ZFJAPP38_CDS007>> items) throws Exception {
        log.debug("START");
        log.debug("items.size():{}", items.size());
        for (int j = 0; j < items.size(); j++) {
            List<ZFJAPP38_CDS007> itemlist = items.get(j);
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

    // 汎用ODataクライアント
    /**
     * 伝票登録処理 （業務実装）
     * 
     * @param itemlist 入力データ
     */
    public void executeRequest(List<ZFJAPP38_CDS007> itemlist) throws Throwable {
        log.debug("START executeRequest_execute");

        //TODO:DB Layer未実装のためコメントアウト
/*         // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();

        try {
            HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

            String servicePath = DefaultZFJAPP38CDS007Service.DEFAULT_SERVICE_PATH;
            String entityName = "ZFJAPP38_CDS007";
            // instantiate custom OData V2 batch request
            ODataRequestBatch requestBatch = new ODataRequestBatch(servicePath, ODataProtocol.V2);
            Changeset changeset = requestBatch.beginChangeset();
            List<ODataRequestCreate> requestCreate = new ArrayList<>();
            log.debug("itemlist.size():{}", itemlist.size());
            for (int j = 0; j < itemlist.size(); j++) {
                ZFJAPP38_CDS007 item = itemlist.get(j);
                GsonBuilder gsonBuilder = ODataGsonBuilder.newGsonBuilder();
                Gson gson = gsonBuilder.registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter()).create();
                String encodedQuery = gson.toJson(item);
                log.debug("encodedQuery:{}", encodedQuery);
                ODataRequestCreate create = new ODataRequestCreate(servicePath, entityName, encodedQuery,
                        ODataProtocol.V2);
                requestCreate.add(create);
                changeset = changeset.addCreate(create);
                log.debug("changeset.addCreate():{}", changeset.toString());
            }
            requestBatch = changeset.endChangeset();

            // execute the batch request
            ODataRequestResultMultipartGeneric batchResult = requestBatch.execute(httpClient);

            // extract information from batch response, by referring to the individual OData
            // request reference
            List<List<HttpResponse>> results = batchResult.getBatchedResponses();
            log.debug("results:{}", results.size());
            log.debug("results:{}", results);

            HttpResponse bhs = batchResult.getHttpResponse();
            log.debug("bhs:{}", bhs);
            int bsc = bhs.getStatusLine().getStatusCode();
            // httpステータス400/500の場合は例外となるが、念のためチェックは行うこととする
            if ((bsc < 200) && (bsc >= 300)) {
                Header[] Sap_Message = bhs.getHeaders("Sap-Message");
                SapMessageList sapmsglist = new SapMessageList(Sap_Message);
                throw new IOException(sapmsglist.toString());
            }

            for (int j = 0; j < requestCreate.size(); j++) {
                ODataRequestResultGeneric result = batchResult.getResult(requestCreate.get(j));

                HttpEntity entity = result.getHttpResponse().getEntity();
                log.debug("entity:{}", entity);

                HttpResponse hs = result.getHttpResponse();
                log.debug("hs:{}", hs);
                int sc = hs.getStatusLine().getStatusCode();
                Header[] Sap_Message = hs.getHeaders("Sap-Message");
                if (Sap_Message.length == 0) {
                    prm.setOutCount(prm.getOutCount() + 1);
                    String msg = futil.getMessage("MSG0004", 0, null);
                    rsvc.log(prm.getUuid(), resultData(itemlist.get(j)), ResultService.INFO, "MSG0004", "0", msg);
                    log.info("[{}] {} {} {}", prm.getUuid(), "MSG0004", "0", msg);
                    continue;
                }
                SapMessageList sapmsglist = new SapMessageList(Sap_Message);
                for (SapMessage sapmsg : sapmsglist) {
                    String Sap_Message_id = sapmsg.getId();
                    String Sap_Message_code = sapmsg.getCode();
                    String Sap_Message_severity = sapmsg.getSeverity();
                    String Sap_Message_message = sapmsg.getMessage();

                    if (Sap_Message_severity.equals("info") || Sap_Message_severity.equals("success")) {
                        prm.setOutCount(prm.getOutCount() + 1);
                        log.info("[{}] {} {} {}", prm.getUuid(), Sap_Message_code, Sap_Message_severity,
                                Sap_Message_message);
                        rsvc.log(prm.getUuid(), resultData(itemlist.get(j)), ResultService.INFO, Sap_Message_id,
                                Sap_Message_code, Sap_Message_message);
                    } else if (Sap_Message_severity.equals("warning")) {
                        prm.setWarnCount(prm.getWarnCount() + 1);
                        log.warn("[{}] {} {} {}", prm.getUuid(), Sap_Message_code, Sap_Message_severity,
                                Sap_Message_message);
                        rsvc.log(prm.getUuid(), resultData(itemlist.get(j)), ResultService.WARN, Sap_Message_id,
                                Sap_Message_code, Sap_Message_message);
                    } else if (Sap_Message_severity.equals("error")) {
                        prm.setOutErrorCount(prm.getOutErrorCount() + 1);
                        log.error("[{}] {} {} {}", prm.getUuid(), Sap_Message_code, Sap_Message_severity,
                                Sap_Message_message);
                        rsvc.log(prm.getUuid(), resultData(itemlist.get(j)), ResultService.ERROR, Sap_Message_id,
                                Sap_Message_code, Sap_Message_message);
                        //throw new IOException(sapmsg.toString());
                    }
                }
                // httpステータス400/500の場合は例外となるが、念のためチェックは行うこととする
                log.debug("getStatusCode:{}", sc);
                if ((sc < 200) && (sc >= 300)) {
                    throw new IOException(sapmsglist.toString());
                }
            }
        } catch (Exception e) {
            log.error("executeRequest_execute:{}", e.toString());
            throw e;
        }
 */    }

    List<String> resultData(ZFJAPP38_CDS007 item) {
        List<String> rec = new ArrayList<String>();
        rec.add(item.getSupplier());
        rec.add(item.getCompanyCode());
        rec.add(item.getFiscalYear());
        rec.add(item.getJournalEntry());
        rec.add(item.getPostingViewItem());
        rec.add(item.getPaymentMethod());
        return rec;
    }


    // 汎用ODataクライアント(GET)
    @Deprecated
    public void executeRequest_get(List<ZFJAPP38_CDS007> itemlist) throws Throwable {
        log.debug("START executeRequest");

        //TODO:DB Layer未実装のためコメントアウト
/*         // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();

        try {
            HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

            String servicePath = DefaultZFJAPP38CDS007Service.DEFAULT_SERVICE_PATH;
            String entityName = "ZFJAPP38_CDS007";
            // instantiate custom OData V2 batch request
            ODataRequestBatch requestBatch = new ODataRequestBatch(servicePath, ODataProtocol.V2);

            // instantiate custom OData V2 read request
            StructuredQuery structuredQuery = StructuredQuery.onEntity(entityName, ODataProtocol.V2);
            structuredQuery.select("Companycode", "Fiscalyear", "Accountingdocumentitem");
            structuredQuery.filter(FieldReference.of("Companycode").equalTo(prm.getCompany_code()));
            String encodedQuery = structuredQuery.getEncodedQueryString();
            ODataRequestRead requestRead = new ODataRequestRead(servicePath, entityName, encodedQuery,
                    ODataProtocol.V2);
            log.debug("encodedQuery:{}", encodedQuery);

            // add read request to batch
            requestBatch.addRead(requestRead);
            // requestBatch =
            // requestBatch.beginChangeset().addCreate(requestCreate).endChangeset();

            // execute the batch request
            ODataRequestResultMultipartGeneric batchResult = requestBatch.execute(httpClient);

            // extract information from batch response, by referring to the individual OData
            // request reference
            ODataRequestResultGeneric result = batchResult.getResult(requestRead);
            List<List<HttpResponse>> results = batchResult.getBatchedResponses();
            log.debug("results:{}", results.size());
            log.debug("results:{}", results);

            HttpResponse bhs = batchResult.getHttpResponse();
            log.debug("bhs:{}", bhs);

            List<Map<String, Object>> listOfEntityFields = result.asListOfMaps();
            log.debug("listOfEntityFields:{}", listOfEntityFields.size());
            log.debug("listOfEntityFields:{}", listOfEntityFields);

            HttpResponse hs = result.getHttpResponse();
            log.debug("hs:{}", hs);
            int sc = hs.getStatusLine().getStatusCode();
            Header[] Sap_Message = hs.getHeaders("Sap-Message");
            if (Sap_Message.length == 0) {
                throw new IOException("Sap-Message 0");
            }
            String Sap_Message_name = Sap_Message[0].getName();
            String Sap_Message_value = Sap_Message[0].getValue();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> Sap_Message_map = null;
            String Sap_Message_code = "";
            String Sap_Message_severity = "";
            String Sap_Message_message = "";
            try {
                Sap_Message_map = objectMapper.readValue(Sap_Message_value, new TypeReference<Map<String, Object>>() {
                });
                log.debug("Sap_Message:{}/{}", Sap_Message_name, Sap_Message_map);
                Sap_Message_code = (String) Sap_Message_map.get("code");
                Sap_Message_severity = (String) Sap_Message_map.get("severity");
                Sap_Message_message = (String) Sap_Message_map.get("message");
            } catch (JsonMappingException e) {
                e.printStackTrace();
                log.error("Sap_Message:{}/{}", Sap_Message_name, Sap_Message_map);
                throw e;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.error("Sap_Message:{}/{}", Sap_Message_name, Sap_Message_map);
                throw e;
            }
            if (Sap_Message_severity.equals("info") || Sap_Message_severity.equals("success")) {
                log.info("{} {} {}", Sap_Message_code, Sap_Message_severity, Sap_Message_message);
            } else if (Sap_Message_severity.equals("warning")) {
                log.warn("{} {} {}", Sap_Message_code, Sap_Message_severity, Sap_Message_message);
            } else if (Sap_Message_severity.equals("error")) {
                log.error("{} {} {}", Sap_Message_code, Sap_Message_severity, Sap_Message_message);
                throw new IOException(Sap_Message_map.toString());
            }
            log.debug("getStatusCode:{}", sc);
            if ((sc >= 200) && (sc < 300)) {
                // entities1 = result.asList(ZFJAPP38_CDS007.class);
            } else {
                throw new IOException(Sap_Message_map.toString());
            }

        } catch (Exception e) {
            throw e;
        }
 */    }

    // ODatav2タイプセーフクライアントAPI
    @Deprecated
    public void executeRequest_typesafe(List<ZFJAPP38_CDS007> itemlist) throws Throwable {
        log.debug("START executeRequest");

        //TODO:DB Layer未実装のためコメントアウト
/*         // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();
        DefaultZFJAPP38CDS007Service service = new DefaultZFJAPP38CDS007Service();

        try {
            List<ZFJAPP38_CDS007CreateFluentHelper> createFluentHelper = new ArrayList<ZFJAPP38_CDS007CreateFluentHelper>();
            log.debug("itemlist.size():{}", itemlist.size());
            ZFJAPP38CDS007ServiceBatchChangeSet changeset = service.batch().beginChangeSet();
            for (int j = 0; j < itemlist.size(); j++) {
                ZFJAPP38_CDS007 item = itemlist.get(j);
                changeset = changeset.createZFJAPP38_CDS007(item);
                createFluentHelper.add(service.createZFJAPP38_CDS007(item));
                rsvc.log(prm.getUuid(), resultData(item), "E", "FI", "000", "");
            }
            FluentHelperCreate<?, ?>[] flcr = createFluentHelper
                    .toArray(new FluentHelperCreate[createFluentHelper.size()]);
            log.debug("flcr.length:{}", flcr.length);

            // BatchResponse result =
            // service.batch().addChangeSet(flcr).executeRequest(destination);
            BatchResponse result = changeset.endChangeSet().executeRequest(destination);

            // DefaultBatchResponseResult dr = (DefaultBatchResponseResult) result;
            Try<BatchResponseChangeSet> changeSetTry = result.get(0);
            if (changeSetTry.isSuccess()) {
                BatchResponseChangeSet responseChangeSet = changeSetTry.get();
                List createdEntities = responseChangeSet.getCreatedEntities().stream()
                        .map(entity -> (ZFJAPP38_CDS007) entity).collect(Collectors.toList());

                List<VdmEntity<?>> s = responseChangeSet.getCreatedEntities();
                Object[] a = s.toArray();

                log.debug("createdEntities:{}", createdEntities);
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
 */        log.debug("END executeRequest");
    }

    // 更新のみ(updateZFJAPP38_CDS007_CDS007) httpヘッダ取得できない
    @Deprecated
    public void executeRequest_beginChangeSet_updateZFJAPP38_CDS007_CDS007_endChangeSet(List<ZFJAPP38_CDS007> itemlist)
            throws Throwable {
        log.debug("START executeRequest");

        //TODO:DB Layer未実装のためコメントアウト
/*         // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();
        DefaultZFJAPP38CDS007Service service = new DefaultZFJAPP38CDS007Service();

        try {
            log.debug("itemlist.size():{}", itemlist.size());
            ZFJAPP38CDS007ServiceBatchChangeSet changeset = service.batch().beginChangeSet();
            for (int j = 0; j < itemlist.size(); j++) {
                ZFJAPP38_CDS007 item = itemlist.get(j);
                changeset = changeset.updateZFJAPP38_CDS007(item);
                rsvc.log(prm.getUuid(), resultData(item), "E", "FI", "000", "");
            }

            BatchResponse result = changeset.endChangeSet().executeRequest(destination);

            Try<BatchResponseChangeSet> changeSetTry = result.get(0);
            if (changeSetTry.isSuccess()) {
                BatchResponseChangeSet responseChangeSet = changeSetTry.get();
                List createdEntities = responseChangeSet.getCreatedEntities().stream()
                        .map(entity -> (ZFJAPP38_CDS007) entity).collect(Collectors.toList());
                log.debug("createdEntities:{}", createdEntities);
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
 */    }

    // 更新のみ(addChangeSet) httpヘッダ取得できない
    @Deprecated
    public void executeRequest_updateFluentHelper(List<ZFJAPP38_CDS007> itemlist) throws Throwable {
        log.debug("START executeRequest");

        //TODO:DB Layer未実装のためコメントアウト
/*         // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();
        DefaultZFJAPP38CDS007Service service = new DefaultZFJAPP38CDS007Service();

        try {
            List<ZFJAPP38_CDS007UpdateFluentHelper> updateFluentHelper = new ArrayList<ZFJAPP38_CDS007UpdateFluentHelper>();
            log.debug("itemlist.size():{}", itemlist.size());
            for (int j = 0; j < itemlist.size(); j++) {
                ZFJAPP38_CDS007 item = itemlist.get(j);
                updateFluentHelper.add(service.updateZFJAPP38_CDS007(item).modifyingEntity());
                rsvc.log(prm.getUuid(), resultData(item), "E", "FI", "000", "");
            }
            FluentHelperUpdate<?, ?>[] flup = updateFluentHelper
                    .toArray(new FluentHelperUpdate[updateFluentHelper.size()]);
            log.debug("flup.length:{}", flup.length);

            BatchResponse result = service.batch().addChangeSet(flup).executeRequest(destination);

            Try<BatchResponseChangeSet> changeSetTry = result.get(0);
            if (changeSetTry.isSuccess()) {
                BatchResponseChangeSet responseChangeSet = changeSetTry.get();
                List createdEntities = responseChangeSet.getCreatedEntities().stream()
                        .map(entity -> (ZFJAPP38_CDS007) entity).collect(Collectors.toList());
                log.debug("createdEntities:{}", createdEntities);
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
 */    }

    // 登録・更新複合パターン httpヘッダ取得できない
    @Deprecated
    public void executeRequest_beginChangeSet_createZFJAPP38_updateZFJAPP38_CDS007_CDS007_endChangeSet(
            List<ZFJAPP38_CDS007> itemlist) throws Throwable {
        log.debug("START executeRequest");

        //TODO:DB Layer未実装のためコメントアウト
/*         // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();
        DefaultZFJAPP38CDS007Service service = new DefaultZFJAPP38CDS007Service();

        try {
            log.debug("itemlist.size():{}", itemlist.size());
            ZFJAPP38CDS007ServiceBatchChangeSet changeset = service.batch().beginChangeSet();
            for (int j = 0; j < itemlist.size(); j++) {
                ZFJAPP38_CDS007 item = itemlist.get(j);
                if (item.getPostingViewItem().compareTo("1") == 0) {
                    changeset = changeset.updateZFJAPP38_CDS007(item);
                } else if (item.getPostingViewItem().compareTo("2") == 0) {
                    changeset = changeset.createZFJAPP38_CDS007(item);
                }
                rsvc.log(prm.getUuid(), resultData(item), "E", "FI", "000", "");
            }

            BatchResponse result = changeset.endChangeSet().executeRequest(destination);

            Try<BatchResponseChangeSet> changeSetTry = result.get(0);
            if (changeSetTry.isSuccess()) {
                BatchResponseChangeSet responseChangeSet = changeSetTry.get();
                List createdEntities = responseChangeSet.getCreatedEntities().stream()
                        .map(entity -> (ZFJAPP38_CDS007) entity).collect(Collectors.toList());
                log.debug("createdEntities:{}", createdEntities);
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
 */    }

    // 登録・更新複合パターン -> NGパターン（$batchが2回に分かれる)
    @Deprecated
    public void executeRequest_createFluentHelper_updateFluentHelper(List<ZFJAPP38_CDS007> itemlist) throws Throwable {
        log.debug("START executeRequest");

        //TODO:DB Layer未実装のためコメントアウト
/*         // HTTP 宛先
        HttpDestination destination = DestinationAccessor.getDestination(prm.getDestination()).asHttp();
        DefaultZFJAPP38CDS007Service service = new DefaultZFJAPP38CDS007Service();

        try {
            List<ZFJAPP38_CDS007UpdateFluentHelper> updateFluentHelper = new ArrayList<ZFJAPP38_CDS007UpdateFluentHelper>();
            List<ZFJAPP38_CDS007CreateFluentHelper> createFluentHelper = new ArrayList<ZFJAPP38_CDS007CreateFluentHelper>();
            log.debug("itemlist.size():{}", itemlist.size());
            for (int j = 0; j < itemlist.size(); j++) {
                ZFJAPP38_CDS007 item = itemlist.get(j);
                if (item.getPostingViewItem().compareTo("1") == 0) {
                    updateFluentHelper.add(service.updateZFJAPP38_CDS007(item).modifyingEntity());
                } else if (item.getPostingViewItem().compareTo("2") == 0) {
                    createFluentHelper.add(service.createZFJAPP38_CDS007(item));
                }
                rsvc.log(prm.getUuid(), resultData(item), "E", "FI", "000", "");
            }
            FluentHelperCreate<?, ?>[] flcr = createFluentHelper
                    .toArray(new FluentHelperCreate[createFluentHelper.size()]);
            FluentHelperUpdate<?, ?>[] flup = updateFluentHelper
                    .toArray(new FluentHelperUpdate[updateFluentHelper.size()]);
            log.debug("flcr.length:{}", flcr.length);
            log.debug("flup.length:{}", flup.length);

            BatchResponse result = service.batch().addChangeSet(flcr).addChangeSet(flup).executeRequest(destination);

            Try<BatchResponseChangeSet> changeSetTry = result.get(0);
            if (changeSetTry.isSuccess()) {
                BatchResponseChangeSet responseChangeSet = changeSetTry.get();
                List createdEntities = responseChangeSet.getCreatedEntities().stream()
                        .map(entity -> (ZFJAPP38_CDS007) entity).collect(Collectors.toList());
                log.debug("createdEntities:{}", createdEntities);
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
 */    }

}
