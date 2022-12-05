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
import java.util.function.Consumer;

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

import jp.co.sekisui.batch.vdm.namespaces.zs4z0001srv.PcUpload;
import jp.co.sekisui.batch.vdm.namespaces.zs4z0001srv.PcUploadFluentHelper;
import jp.co.sekisui.batch.vdm.services.DefaultZS4Z0001SRVService;
import jp.co.sekisui.common.entity.sk001.Zz0004T;
import jp.co.sekisui.common.entity.sk001.Zz0009T;
import jp.co.sekisui.common.function.FUtil;
import jp.co.sekisui.common.function.SapMessage;
import jp.co.sekisui.common.function.SapMessageList;
import jp.co.sekisui.common.service.ResultService;
import jp.co.sekisui.common.service.ResultFieldLabelService;
import jp.co.sekisui.common.service.UploadDataService;
import lombok.extern.slf4j.Slf4j;

/**
 * 読み込み処理クラス （業務実装）
 */
@Slf4j
@Component
@StepScope
public class ItemReaderFilePcUpload implements ItemReader<List<PcUpload>> {
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
     * 処理結果テーブル(項目ラベル) （スケルトン実装）
     */
    @Autowired
    private ResultFieldLabelService resultFieldLabel;

    /**
     * 処理結果テーブル （業務実装）
     */
    @Autowired
    private UploadDataService upload;

    /**
     * データ定義 （業務実装）
     */
    private int entityIndex = 0;
    private List<PcUpload> entities1;
    private List<List<PcUpload>> entities2;

    /**
     * 処理対象データの取得 （業務実装）
     * 
     * @param prm パラメータ
     */
    // @Autowired
    public void init() throws Exception {
        log.debug("START init");

        Zz0009T data = null;
        List<Zz0004T> itemsInput = resultFieldLabel.select(prm.getProgram_id(), "", "I");   /* Input項目取得 */
        List<Zz0004T> itemsOut = resultFieldLabel.select(prm.getProgram_id(), "", "O");     /* Output項目取得 */
        int itemsInputCount = itemsInput.size();
        int itemsMsgCount = 4;  /* Message項目数は固定 */
        int itemsOutCount = itemsOut.size();
        int itemsMax = itemsInputCount + itemsMsgCount + itemsOutCount;
        log.debug("Program_id = {}, Lang={}, itemsInputCount={}, itemsMsgCount={}, itemsOutCount={}", prm.getProgram_id(), prm.getLang(), itemsInputCount, itemsMsgCount, itemsOutCount);

        if( (itemsInputCount == 0) || (itemsOutCount == 0) ) {
            /* Input/Output項目がない場合 */
            prm.setWarnCount(prm.getWarnCount() + 1);
            log.warn("[{}] {}", prm.getUuid(), futil.getMessage("MSG0006", 0, null));
            return;
        }
        
        PcUpload entity = new PcUpload();
        Consumer<String>[] setComm = new Consumer[103];     /* 関数型インターフェイス定義：各Setter */
        setComm[0] = entity::setComm001;
        setComm[1] = entity::setComm002;
        setComm[2] = entity::setComm003;
        setComm[3] = entity::setComm004;
        setComm[4] = entity::setComm005;
        setComm[5] = entity::setComm006;
        setComm[6] = entity::setComm007;
        setComm[7] = entity::setComm008;
        setComm[8] = entity::setComm009;
        setComm[9] = entity::setComm010;
        setComm[10] = entity::setComm011;
        setComm[11] = entity::setComm012;
        setComm[12] = entity::setComm013;
        setComm[13] = entity::setComm014;
        setComm[14] = entity::setComm015;
        setComm[15] = entity::setComm016;
        setComm[16] = entity::setComm017;
        setComm[17] = entity::setComm018;
        setComm[18] = entity::setComm019;
        setComm[19] = entity::setComm020;
        setComm[20] = entity::setComm021;
        setComm[21] = entity::setComm022;
        setComm[22] = entity::setComm023;
        setComm[23] = entity::setComm024;
        setComm[24] = entity::setComm025;
        setComm[25] = entity::setComm026;
        setComm[26] = entity::setComm027;
        setComm[27] = entity::setComm028;
        setComm[28] = entity::setComm029;
        setComm[29] = entity::setComm030;
        setComm[30] = entity::setComm031;
        setComm[31] = entity::setComm032;
        setComm[32] = entity::setComm033;
        setComm[33] = entity::setComm034;
        setComm[34] = entity::setComm035;
        setComm[35] = entity::setComm036;
        setComm[36] = entity::setComm037;
        setComm[37] = entity::setComm038;
        setComm[38] = entity::setComm039;
        setComm[39] = entity::setComm040;
        setComm[40] = entity::setComm041;
        setComm[41] = entity::setComm042;
        setComm[42] = entity::setComm043;
        setComm[43] = entity::setComm044;
        setComm[44] = entity::setComm045;
        setComm[45] = entity::setComm046;
        setComm[46] = entity::setComm047;
        setComm[47] = entity::setComm048;
        setComm[48] = entity::setComm049;
        setComm[49] = entity::setComm050;
        setComm[50] = entity::setComm051;
        setComm[51] = entity::setComm052;
        setComm[52] = entity::setComm053;
        setComm[53] = entity::setComm054;
        setComm[54] = entity::setComm055;
        setComm[55] = entity::setComm056;
        setComm[56] = entity::setComm057;
        setComm[57] = entity::setComm058;
        setComm[58] = entity::setComm059;
        setComm[59] = entity::setComm060;
        setComm[60] = entity::setComm061;
        setComm[61] = entity::setComm062;
        setComm[62] = entity::setComm063;
        setComm[63] = entity::setComm064;
        setComm[64] = entity::setComm065;
        setComm[65] = entity::setComm066;
        setComm[66] = entity::setComm067;
        setComm[67] = entity::setComm068;
        setComm[68] = entity::setComm069;
        setComm[69] = entity::setComm070;
        setComm[70] = entity::setComm071;
        setComm[71] = entity::setComm072;
        setComm[72] = entity::setComm073;
        setComm[73] = entity::setComm074;
        setComm[74] = entity::setComm075;
        setComm[75] = entity::setComm076;
        setComm[76] = entity::setComm077;
        setComm[77] = entity::setComm078;
        setComm[78] = entity::setComm079;
        setComm[79] = entity::setComm080;
        setComm[80] = entity::setComm081;
        setComm[81] = entity::setComm082;
        setComm[82] = entity::setComm083;
        setComm[83] = entity::setComm084;
        setComm[84] = entity::setComm085;
        setComm[85] = entity::setComm086;
        setComm[86] = entity::setComm087;
        setComm[87] = entity::setComm088;
        setComm[88] = entity::setComm089;
        setComm[89] = entity::setComm090;
        setComm[90] = entity::setComm091;
        setComm[91] = entity::setComm092;
        setComm[92] = entity::setComm093;
        setComm[93] = entity::setComm094;
        setComm[94] = entity::setComm095;
        setComm[95] = entity::setComm096;
        setComm[96] = entity::setComm097;
        setComm[97] = entity::setComm098;
        setComm[98] = entity::setComm099;
        setComm[99] = entity::setComm100;
        setComm[100] = entity::setComm101;
        setComm[101] = entity::setComm102;
        setComm[102] = entity::setComm103;
        Consumer<String>[] setMessage = new Consumer[4];
        setMessage[0] = entity::setMessageType;
        setMessage[1] = entity::setMessageID;
        setMessage[2] = entity::setMessageNumber;
        setMessage[3] = entity::setMessageText;
        Consumer<String>[] setOut = new Consumer[5];
        setOut[0] = entity::setOut001;
        setOut[1] = entity::setOut002;
        setOut[2] = entity::setOut003;
        setOut[3] = entity::setOut004;
        setOut[4] = entity::setOut005;

        try {
            data = upload.select(prm.getUuid());
            if (data != null) {
                log.debug("data.getFilename()={}", data.getFilename());
                log.debug("data.getFiledata()={}", data.getFiledata());
                
                entities1 = new ArrayList<>();
                String[] linedata = data.getFiledata().split("\r\n");
                for (int i = 0; i < linedata.length; i++) {

                   String[] dat = linedata[i].split("\t", -1);

                    /* Seq設定 */
                    entity.setSeqno(String.valueOf(i+1));

                    /* Input設定 */
                    for(int loopCount = 0; loopCount < itemsInputCount; loopCount++){
                        setComm[loopCount].accept(dat[loopCount]);
                    }
                    if(dat.length > itemsInputCount){
                        /* リラン時 */
                        String[] redat;
                        if(dat.length == itemsMax){
                            /* データサイズと項目数(最大)が一致する場合、取得データをそのまま使用 */
                            redat = dat;
                        }else {
                            /* データサイズと項目数(最大)が一致しない場合(Select時に最終空白は強制削除)、空文字を追加 */
                            redat = Arrays.copyOf(dat, dat.length + 1);
                            /* 空文字追加 */
                            redat[dat.length + 0] = "";
                        }
                        log.debug("dat.length={}, redat.length={}", dat.length, redat.length);

                        /* Message設定 */
                        for(int loopCount = 0; loopCount < itemsMsgCount; loopCount++){
                            log.debug("redat={}", redat[(itemsInputCount + loopCount)]);
                            setMessage[loopCount].accept(redat[(itemsInputCount + loopCount)]);
                        }
                        /* Output設定 */
                        for(int loopCount = 0; loopCount < itemsOutCount; loopCount++){
                            log.debug("redat={}", redat[(itemsInputCount + itemsMsgCount + loopCount)]);
                            setOut[loopCount].accept(redat[(itemsInputCount + itemsMsgCount + loopCount)]);
                        }
                    }

                    entities1.add(entity);
                    log.info("entities1={}", entities1);
                }
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
            Map<String, List<PcUpload>> treeMap = new TreeMap<>();
            for (PcUpload list : entities1) {
                if (treeMap.containsKey(list.getSeqno())) {
                    treeMap.get(list.getSeqno()).add(list);
                } else {
                    List<PcUpload> sub = new ArrayList<>();
                    sub.add(list);
                    treeMap.put(list.getSeqno(), sub);
                }
            }
            entities2 = new ArrayList<>(treeMap.values());
            upload.update(prm.getUuid(), "X");
            rsvc.log(prm.getUuid(), ResultService.INFO, "", "", "file name="+ data.getFilename());
            log.debug("entities2:{}", Arrays.toString(entities2.toArray()));

        } catch (Exception e) {
            log.error("ItemReaderFilePcUpload:{}", e.toString());
            throw e;
        }
        log.debug("END init");
    }

    /**
     * 保存してあるエンティティの取得 （業務実装）
     * 
     * @return エンティティ(全て処理済みはnullを返す)
     */
    @Override
    public List<PcUpload> read() throws Exception {
        log.debug("START read");

        List<PcUpload> result = null;
        if (entityIndex < entities2.size()) {
            result = entities2.get(entityIndex);
            entityIndex++;
        }
        if (result != null) {
            log.info("[{}] read count:{}", prm.getUuid(), result.size());
        }
        log.debug("END read");
        return result;
    }
}
