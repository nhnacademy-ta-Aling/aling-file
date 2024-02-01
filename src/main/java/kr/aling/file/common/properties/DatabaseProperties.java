package kr.aling.file.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MySQL 정보 Custom Properties.
 *
 * @author 박경서
 * @since 1.0
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "aling.mysql")
public class DatabaseProperties {

    private String driver;
    private String url;
    private String username;
    private String password;
    private Integer initialSize;
    private Integer maxTotal;
    private Integer minIdle;
    private Integer maxIdle;
    private Integer maxWait;
    private String query;
}
