package com.data.processing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration

public class BatchTaskExecutorPool {

	@Bean(name = "taskExecutor")
	public ThreadPoolTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Runtime.getRuntime().availableProcessors()*8);
		executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors()*8);
		executor.setQueueCapacity(1000000);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.afterPropertiesSet();
		executor.setThreadNamePrefix("batch-exec-");
		executor.initialize();
		return executor;
	}
}
