package kr.aling.file.file.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Object Storage 토큰 생성 Request Dto.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Getter
@AllArgsConstructor
public class StorageTokenRequestDto {

    private Auth auth;

    /**
     * StorageTokenRequestDto Auth Dto.
     */
    @Getter
    @AllArgsConstructor
    public static class Auth {
        private String tenantId;
        private PasswordCredentials passwordCredentials;
    }

    /**
     * StorageTokenRequestDto PasswordCredentials Dto.
     */
    @Getter
    @AllArgsConstructor
    public static class PasswordCredentials {
        private String username;
        private String password;
    }

    /**
     * 요청 Dto 만드는 메서드.
     *
     * @param tenantId storage tenantId
     * @param username storage 계정
     * @param password storage 비밀 번호
     * @return StorageTokenRequestDto
     */
    public static StorageTokenRequestDto makeDto(String tenantId, String username, String password) {
        return new StorageTokenRequestDto(new Auth(tenantId, new PasswordCredentials(username, password)));
    }

}
