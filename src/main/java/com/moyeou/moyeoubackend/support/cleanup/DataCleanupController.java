package com.moyeou.moyeoubackend.support.cleanup;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("demo")
@RestController
@RequiredArgsConstructor
public class DataCleanupController {
    private final DataCleanupService dataCleanupService;

    @DeleteMapping("/all-data")
    public void clean() {
        dataCleanupService.execute();
    }
}
