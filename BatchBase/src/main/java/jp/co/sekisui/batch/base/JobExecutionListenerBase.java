package jp.co.sekisui.batch.base;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceError;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorDetails;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

import org.apache.http.Header;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;

import io.vavr.control.Option;
import jp.co.sekisui.common.function.FUtil;
import jp.co.sekisui.common.function.SapMessage;
import jp.co.sekisui.common.service.ResultService;
import lombok.extern.slf4j.Slf4j;

/**
 * ジョブ実行リスナークラス （スケルトン実装）
 */
@Slf4j
public class JobExecutionListenerBase extends JobExecutionListenerSupport implements ExitCodeGenerator {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int WARNING = 2;

    /**
     * メッセージ
     */
    @Autowired
    private FUtil futil;

    /**
     * パラメータ
     */
    @Autowired
    private ItemParameterBase prm;

    /**
     * 処理結果テーブル
     */
    @Autowired
    ResultService rsvc;

    private int exitCode = SUCCESS;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // ジョブの開始前
        super.beforeJob(jobExecution);
        log.info("Start Job");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // ジョブの終了後
        super.afterJob(jobExecution);
        List<Throwable> exceptions = jobExecution.getAllFailureExceptions();

        if (prm.getWarnCount() > 0) {
            exitCode = WARNING;
        }
        if (prm.getOutErrorCount() > 0) {
            exitCode = ERROR;
        }

        if (exceptions != null && !exceptions.isEmpty()) {
            log.error("This job has occurred some exceptions as follow. " + "[job-name:{}] [size:{}]",
                    jobExecution.getJobInstance().getJobName(), exceptions.size());
            exceptions.forEach(th -> {
                // log.error("exception has occurred in job.", th);
                exceptionHandler(th);
            });
            exitCode = ERROR;
            /*
             * jobExecution.getStepExecutions().forEach(stepExecution -> { Object errorItem
             * = stepExecution.getExecutionContext().get("ERROR_ITEM"); // (10) if
             * (errorItem != null) { log.error("detected error on this item processing. " +
             * "[step:{}] [item:{}]", stepExecution.getStepName(), errorItem); } });
             */
        }
        // jobExecution.setExitStatus(new ExitStatus("CUSTOM SUCCESS STATUS"));
        // jobExecution.setExitStatus(new ExitStatus("FAILED"));
        // jobExecution.setStatus(BatchStatus.ABANDONED);
        // jobExecution.setStatus(BatchStatus.COMPLETED);
        // jobExecution.setStatus(BatchStatus.FAILED);
        // jobExecution.setStatus(BatchStatus.STARTED);
        // jobExecution.setStatus(BatchStatus.STARTING);
        // jobExecution.setStatus(BatchStatus.STOPPED);
        // jobExecution.setStatus(BatchStatus.STOPPING);
        // jobExecution.setStatus(BatchStatus.UNKNOWN);

        String status = null;
        if(exitCode == SUCCESS) {
            status = "S";
        } else if(exitCode == WARNING) {
            status = "W";
        } else if(exitCode == ERROR) {
            status = "E";
        }
        rsvc.end(prm.getUuid(), new Date(), prm.getInCount(), prm.getOutCount(), prm.getWarnCount(), prm.getOutErrorCount(), status);

        log.info("uuid:[{}] End Job:inCount={}", prm.getUuid(), prm.getInCount());
        log.info("uuid:[{}] End Job:outCount={}", prm.getUuid(), prm.getOutCount());
        log.info("uuid:[{}] End Job:warnCount={}", prm.getUuid(), prm.getWarnCount());
        log.info("uuid:[{}] End Job:inErrorCount={}", prm.getUuid(), prm.getInErrorCount());
        log.info("uuid:[{}] End Job:OutErrorCount={}", prm.getUuid(), prm.getOutErrorCount());
        log.info("uuid:[{}] End Job:JobId={}", prm.getUuid(), jobExecution.getJobId());
        log.info("uuid:[{}] End Job:StartTime={}", prm.getUuid(), jobExecution.getStartTime());
        log.info("uuid:[{}] End Job:EndTime={}", prm.getUuid(), jobExecution.getEndTime());
        log.info("uuid:[{}] End Job:BatchStatus={}", prm.getUuid(), jobExecution.getStatus());
        log.info("uuid:[{}] End Job:ExitStatus={}", prm.getUuid(), jobExecution.getExitStatus());
        log.info("uuid:[{}] End Job:exitCode={}", prm.getUuid(), getExitCode());
    }

    @Override
    public int getExitCode() {
        // 終了コードを返す
        return exitCode;
    }

    public Throwable containsException(Throwable ex) {
        Throwable cause = ex.getCause();
        if (cause == null) {
            return ex;
        } else {
            return containsException(cause);
        }
    }

    public void exceptionHandler(Throwable ex) {
        ex = containsException(ex);
        if (ex instanceof ODataServiceErrorException) {
            ODataServiceErrorException e = (ODataServiceErrorException) ex;
            log.error("ODataServiceErrorException:{}", e.toString());
            log.error("ODataServiceErrorException:{}", e.getOdataError());
            log.error("ODataServiceErrorException:{}", e.getLocalizedMessage());
            ODataServiceError odataError = e.getOdataError();
            // Map<String, Object> innn = odataError.getInnerError();
            // String code = odataError.getODataCode();
            // String message = odataError.getODataMessage();
            // rsvc.log(prm.getUuid(), "E", "MSG9001", "000", e.toString());
            // List<ODataServiceErrorDetails> details = odataError.getDetails();
            // for (ODataServiceErrorDetails detail: details ) {
            // code = detail.getODataCode();
            // message = detail.getODataMessage();
            // }
            log.error("ODataServiceError:{}", odataError);
            log.error("ODataServiceError:{}", odataError.getODataCode());
            log.error("ODataServiceError:{}", odataError.getODataMessage());
            log.error("ODataServiceError:{}", odataError.getDetails());
            log.error("ODataServiceError:{}", odataError.getInnerError());
            int httpCode = e.getHttpCode();
            Collection<Header> httpHeader = e.getHttpHeaders();
            Option<String> httpBody = e.getHttpBody();
            log.error("httpCode:{}", httpCode);
            log.error("httpHeader:{}", httpHeader);
            log.error("httpBody:{}", httpBody);

            String sap_Message_value = httpBody.get();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> sap_Message_map = null;
            SapMessage sapMessage = null;
            String[] str = null;
            try {
                sap_Message_map = objectMapper.readValue(sap_Message_value, new TypeReference<Map<String, Object>>() {
                });
                log.debug("httpBody:{}", sap_Message_map);
                Map<String, Object> error = (Map<String, Object>) sap_Message_map.get("error");
                log.debug("error:{}", error);
                String code = (String) error.get(SapMessage.CODE);
                Map<String, Object> map = (Map) error.get(SapMessage.MESSAGE);
                String message = (String) map.get("value");
                // 1.severityが設定されていないメッセージをBIZに処理結果、ログに設定したい
                // →例外の時にSEVERITYがない場合、msgtypeを「E」で設定する
                String severity = SapMessage.ERROR;
                if (error.containsKey(SapMessage.SEVERITY)) {
                    severity = (String) error.get(SapMessage.SEVERITY);
                }
                sapMessage = new SapMessage(code, severity, message);
                if (severity.equals(SapMessage.INFO) || severity.equals(SapMessage.SUCCESS)) {
                    log.info("[{}] {} {} {}", prm.getUuid(), code, severity, message);
                    rsvc.log(prm.getUuid(), ResultService.INFO, sapMessage.getId(), sapMessage.getCode(), message);
                } else if (severity.equals(SapMessage.WARNING)) {
                    log.warn("[{}] {} {} {}", prm.getUuid(), code, severity, message);
                    rsvc.log(prm.getUuid(), ResultService.WARN, sapMessage.getId(), sapMessage.getCode(), message);
                } else if (severity.equals(SapMessage.ERROR)) {
                    log.error("[{}] {} {} {}", prm.getUuid(), code, severity, message);
                    rsvc.log(prm.getUuid(), ResultService.ERROR, sapMessage.getId(), sapMessage.getCode(), message);
                }

                Map<String, Object> innererror = (Map<String, Object>) error.get("innererror");
                log.debug("innererror:{}", innererror);
                List<Map> errordetails = (ArrayList) innererror.get("errordetails");
                log.debug("errordetails:{}", errordetails);
                for (Map detail : errordetails) {
                    code = (String) detail.get(SapMessage.CODE);
                    message = (String) detail.get(SapMessage.MESSAGE);
                    severity = SapMessage.ERROR;
                    if (detail.containsKey(SapMessage.SEVERITY)) {
                        severity = (String) detail.get(SapMessage.SEVERITY);
                    }
                    sapMessage = new SapMessage(code, severity, message);
                    if (severity.equals(SapMessage.INFO) || severity.equals(SapMessage.SUCCESS)) {
                        log.info("[{}] {} {} {}", prm.getUuid(), code, severity, message);
                        rsvc.log(prm.getUuid(), ResultService.INFO, sapMessage.getId(), sapMessage.getCode(), message);
                    } else if (severity.equals(SapMessage.WARNING)) {
                        log.warn("[{}] {} {} {}", prm.getUuid(), code, severity, message);
                        rsvc.log(prm.getUuid(), ResultService.WARN, sapMessage.getId(), sapMessage.getCode(), message);
                    } else if (severity.equals(SapMessage.ERROR)) {
                        log.error("[{}] {} {} {}", prm.getUuid(), code, severity, message);
                        rsvc.log(prm.getUuid(), ResultService.ERROR, sapMessage.getId(), sapMessage.getCode(), message);
                    }
                }
            } catch (JsonMappingException exp) {
                exp.printStackTrace();
                log.error("httpBody:{}/{}", sap_Message_map);
            } catch (JsonProcessingException exp) {
                exp.printStackTrace();
                log.error("httpBody:{}/{}", sap_Message_map);
            }
            // rsvc.log(prm.getUuid(), "E", "MSG9001", "000", e.toString());
        } else if (ex instanceof ODataDeserializationException) {
            ODataDeserializationException e = (ODataDeserializationException) ex;
            log.error("ODataDeserializationException:{}", e.toString());
            log.error("ODataDeserializationException:{}", e.getLocalizedMessage());
            int httpCode = e.getHttpCode();
            Collection<Header> httpHeader = e.getHttpHeaders();
            Option<String> httpBody = e.getHttpBody();
            log.error("httpCode:{}", httpCode);
            log.error("httpHeader:{}", httpHeader);
            log.error("httpBody:{}", httpBody);
            // rsvc.log(prm.getUuid(), "E", "MSG9002", "000", e.toString());
        } else if (ex instanceof ODataResponseException) {
            ODataResponseException e = (ODataResponseException) ex;
            log.error("ODataResponseException:{}", e.toString());
            int httpCode = e.getHttpCode();
            Collection<Header> httpHeader = e.getHttpHeaders();
            Option<String> httpBody = e.getHttpBody();
            log.error("httpCode:{}", httpCode);
            log.error("httpHeader:{}", httpHeader);
            log.error("httpBody:{}", httpBody);
            // rsvc.log(prm.getUuid(), "E", "MSG9003", "000", e.toString());
        } else if (ex instanceof ODataException) {
            ODataException e = (ODataException) ex;
            log.error("ODataException:{}", e.toString());
            log.error("ODataException:{}", e.getLocalizedMessage());
            // rsvc.log(prm.getUuid(), "E", "MSG9004", "000", e.toString());
        } else if (ex instanceof Exception) {
            Exception e = (Exception) ex;
            log.error("Exception:{}", e.toString());
            log.error("Exception:{}", e.getLocalizedMessage());
            // rsvc.log(prm.getUuid(), "E", "MSG9005", "000", e.toString());
        }
    }

}
