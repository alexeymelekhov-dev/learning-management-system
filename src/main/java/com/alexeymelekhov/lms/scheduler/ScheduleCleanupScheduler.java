package com.alexeymelekhov.lms.scheduler;

import com.alexeymelekhov.lms.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduleCleanupScheduler {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.delete-old-schedules.cron}")
    public void deleteOldSchedules() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        scheduleRepository.deleteSchedulesOlderThan(oneYearAgo);
    }
}
