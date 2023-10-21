package com.moyeou.moyeoubackend.support.cleanup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("demo")
@RestController
@RequiredArgsConstructor
@Tag(name = "DataCleanup", description = "데이터 삭제 API(demo 서버에서만)")
public class DataCleanupController {
    private final DataCleanupService dataCleanupService;

    @Value("#{environment.ADMIN_KEY}")
    private String adminKey;

    @Operation(summary = "모든 데이터 삭제")
    @DeleteMapping("/all-data")
    public String clean(@RequestParam String key) {
        if (adminKey.equals(key)) {
            dataCleanupService.execute();
            return "success";
        }
        return "fail";
    }
}
