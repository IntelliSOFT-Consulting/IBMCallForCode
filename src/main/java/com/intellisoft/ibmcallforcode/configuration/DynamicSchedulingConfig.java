package com.intellisoft.ibmcallforcode.configuration;

import com.intellisoft.ibmcallforcode.service.IamTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledFuture;

@Slf4j
@Configuration

public class DynamicSchedulingConfig implements SchedulingConfigurer {
	
	@Autowired
	private IamTokenService iamTokenService;
	
	private ScheduledFuture<?> scheduledFuture;
	
	@Bean
	public TaskScheduler poolScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		scheduler.setPoolSize(1);
		scheduler.initialize();
		return scheduler;
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(poolScheduler());
		
		// Initial scheduling with a default rate
		taskRegistrar.addFixedRateTask(() -> {
			log.info("Refreshing IAM token...");
			String token = iamTokenService.refreshIamToken();
			if (token != null) {
				log.info("New Token: " + token);
//                int expiresIn = iamTokenService.getTokenExpiry();
                int expiresIn = 10;
				long delay = expiresIn * 1000L - 120 * 1000L; // 2 minutes before expiry
				rescheduleTask(delay);
			}
		}, 58 * 60 * 1000);
	}
	
	private void rescheduleTask(Long delay) {
		if (delay == null) {
			log.error("Delay value is null. Task cannot be scheduled.");
			return;
		}
		
		if (delay < 0) {
			log.error("Delay value is negative: {}. Task cannot be scheduled.", delay);
			return;
		}
  
		if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
			scheduledFuture.cancel(false);
		}
		
		// Schedule the new task
		scheduledFuture = poolScheduler().scheduleWithFixedDelay(() -> {
			log.info("Refreshing IAM token...");
			iamTokenService.refreshIamToken();
		}, delay);
	}
}
