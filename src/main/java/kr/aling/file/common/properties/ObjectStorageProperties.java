package kr.aling.file.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Object Storage 정보 Custom Properties.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "aling.storage")
public class ObjectStorageProperties {

    private String tenantId;
    private String username;
    private String password;

    private String tokenIssueUrl;
    private String storeUrl;

    private String containerName;
}
