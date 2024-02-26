package kr.aling.file.config;

import kr.aling.file.common.feignclient.FeignClientBase;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Eureka 설정 class.
 *
 * @author 박경서
 * @since 1.0
 */
@EnableFeignClients(basePackageClasses = FeignClientBase.class)
@EnableEurekaClient
@Configuration
public class EurekaConfig {
}