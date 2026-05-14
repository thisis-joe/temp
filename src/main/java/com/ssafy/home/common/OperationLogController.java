package com.ssafy.home.common;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class OperationLogController {
    private final OperationLogService operationLogService;

    @GetMapping
    ApiResponse<List<OperationLog>> logs(@RequestParam(required = false) String category) {
        return ApiResponse.ok(operationLogService.find(category));
    }
}
