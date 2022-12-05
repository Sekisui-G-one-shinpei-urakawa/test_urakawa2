package jp.co.sekisui.batch.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.batch.JobExecutionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * ExitCodeGeneratorクラス （スケルトン実装）
 */
@Slf4j
@Component
public class ExitCodeGenerator {

    List<String> events = new ArrayList<>();

    @EventListener
    void receive(JobExecutionEvent jobExecutionEvent) {
        String exitCode = jobExecutionEvent.getJobExecution().getExitStatus().getExitCode();
        events.add(exitCode);
        log.debug("receive():exitCode={}", exitCode);
    }

    // String getExitCode() {
    // ExitStatus status = events.find { it != AppExitStatus.SUCCESS } ?:
    // AppExitStatus.SUCCESS
    // return status.getExitCode();
    // }
}