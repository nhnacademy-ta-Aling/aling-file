package kr.aling.file.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 파일 정보를 담는 Dto.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Getter
@AllArgsConstructor
public class FileInfoDto {

    private String originFileName;
    private String saveFileName;
}
