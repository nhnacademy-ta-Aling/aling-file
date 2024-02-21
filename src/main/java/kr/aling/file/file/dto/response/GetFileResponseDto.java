package kr.aling.file.file.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 파일 단건 조회 응답 Dto.
 *
 * @author 박경서
 * @since 1.0
 **/
@Getter
@AllArgsConstructor
public class GetFileResponseDto {

    private Long fileNo;

    private Integer categoryNo;
    private String categoryName;

    private String path;
    private String originName;
    private String fileSize;
}
