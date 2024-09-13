package kr.gooroom.gpms.health;

import jakarta.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.gooroom.gpms.health.service.HealthService;

@Component
public class HealthScheduler {
    @Resource(name = "healthService")
    private HealthService healthService;

    @Scheduled(cron = "0/5 * * * * *")
    void updateLastActivatedTime() {
        try {
            healthService.updateHealth();
        } catch (Exception ex) {

        }
    }
}
