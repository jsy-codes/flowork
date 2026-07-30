package com.flowork.flowork.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    //기본 스레드 풀 사용 - 필요시 ThreadPoolTaskExecutor로 커스텀 가능.

}
