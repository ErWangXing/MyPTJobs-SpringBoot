package com.MyPTJobs.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduledTask {

    @Autowired
    private RatingService ratingService;

    @Scheduled(cron = "0 * * * * ?")
    public void printCurrentTime() {
        ratingService.calcTotalJobSeekerAverageRating();
        ratingService.calcTotalEmployerAverageRating();
        System.out.println("Current time: " + LocalDateTime.now());

    }
}
