package kr.aling.file.file.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Object Storage 토큰 생성 Response Dto.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Getter
@NoArgsConstructor
public class StorageTokenResponseDto {

    private Access access;

    /**
     * StorageTokenResponseDto Access Dto.
     */
    @Getter
    @NoArgsConstructor
    public static class Access {
        private Token token;
    }

    /**
     * StorageTokenResponseDto Token Dto.
     */
    @Getter
    @NoArgsConstructor
    public static class Token {
        private String id;
        private LocalDateTime expires;
    }
}
