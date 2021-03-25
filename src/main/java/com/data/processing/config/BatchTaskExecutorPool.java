package com.data.processing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration

public class BatchTaskExecutorPool {
	
	@Value("${custom.taskExecutor.corePoolSize}")
	int corePoolSize;
	@Value("${custom.taskExecutor.maxPoolSize}")
	int maxPoolSize;
	@Value("${custom.taskExecutor.queueCapacity}")
	int queueCapacity;

	@Bean(name = "taskExecutor")
	public ThreadPoolTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.afterPropertiesSet();
		executor.setThreadNamePrefix("batch-exec-");
		executor.initialize();
		return executor;
	}
}
