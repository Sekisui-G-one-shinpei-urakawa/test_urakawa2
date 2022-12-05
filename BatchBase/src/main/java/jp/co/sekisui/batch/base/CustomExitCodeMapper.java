package jp.co.sekisui.batch.base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * ExitCodeMapperクラス （スケルトン実装）
 */
@Slf4j
public class CustomExitCodeMapper extends SimpleJvmExitCodeMapper {
    Map<String, Integer> mapping = new HashMap<String, Integer>();
    @Override
    public int intValue(String exitCode) {        
        log.debug("intValue()={}", exitCode);
        mapping.put(ExitStatus.COMPLETED.getExitCode(), new Integer(100));
        mapping.put(ExitStatus.EXECUTING.getExitCode(), new Integer(101));
        mapping.put(ExitStatus.FAILED.getExitCode(), new Integer(102));
        mapping.put(ExitStatus.NOOP.getExitCode(), new Integer(103));
        mapping.put(ExitStatus.STOPPED.getExitCode(), new Integer(104));
        mapping.put(ExitStatus.UNKNOWN.getExitCode(), new Integer(105));
        setMapping(mapping);
        return mapping.get(exitCode);
    }
}
