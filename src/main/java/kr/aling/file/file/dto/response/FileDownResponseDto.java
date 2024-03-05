package kr.aling.file.file.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;

/**
 * 파일 다운 로드 응답 Dto.
 *
 * @author 박경서
 * @since 1.0
 **/
@Getter
@AllArgsConstructor
public class FileDownResponseDto {

    private String originName;
    private ByteArrayResource byteArrayResource;
}
