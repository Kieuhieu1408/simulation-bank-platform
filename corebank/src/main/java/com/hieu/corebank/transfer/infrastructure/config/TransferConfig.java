package com.hieu.corebank.transfer.infrastructure.config;

import com.hieu.corebank.shared.constant.ProfileConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Configuration
public class TransferConfig {

    /**
     * Virtual-thread executor phục vụ điều phối Saga chuyển tiền.
     * Tự động đóng (graceful close) khi Spring ApplicationContext shutdown.
     */
    @Bean(name = "transferSagaExecutor", destroyMethod = "close")
    public ExecutorService transferSagaExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
