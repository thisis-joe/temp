package com.ssafy.home.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.List;

@Service
@Slf4j
public class OperationLogService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_LOGS = 300;

    private final ArrayDeque<OperationLog> logs = new ArrayDeque<>();

    public synchronized void record(String category, String action, String message) {
        OperationLog operationLog = new OperationLog(
                LocalDateTime.now().format(FORMATTER),
                category,
                action,
                message
        );
        logs.addFirst(operationLog);
        while (logs.size() > MAX_LOGS) {
            logs.removeLast();
        }
        log.info("OPERATION_LOG category={} action={} message={} retainedInMemory={} maxInMemory={}",
                category, action, message, logs.size(), MAX_LOGS);
    }

    public synchronized List<OperationLog> find(String category) {
        List<OperationLog> result = logs.stream()
                .filter(log -> category == null || category.isBlank() || category.equalsIgnoreCase(log.category()))
                .limit(100)
                .toList();
        log.info("OPERATION_LOG_QUERY category={} resultCount={}", category, result.size());
        return result;
    }
}
