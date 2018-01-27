package com.chlorocode.tendertracker;

import com.chlorocode.tendertracker.api.LongProcess;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by Kyaw Min Thu on 5/1/2017.
 * This class is used to control the long background process of the application.
 */
@Configuration
@EnableAsync
public class LongProcessConfiguration implements AsyncConfigurer {

    /**
     * This method is used to generate the LongProcess bean.
     *
     * @return LongProcess
     * @see LongProcess
     */
    @Bean
    public LongProcess longProcessBean() {
        return new LongProcess();
    }

    /**
     * This method is used to get the executor of async tasks.
     *
     * @return Executor
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setThreadNamePrefix("LULExecutor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * This method is used to create the AsyncUncaughtExceptionHandler.
     *
     * @return AsyncUncaughtExceptionHandler
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

}