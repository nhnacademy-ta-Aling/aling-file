package kr.aling.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Aling File API 서버.
 *
 * @author 박경서
 * @since 1.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }

}
