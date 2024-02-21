package kr.aling.file.file.service;

import java.util.List;
import kr.aling.file.file.dto.response.GetFileResponseDto;

/**
 * 파일 Read Service interface.
 *
 * @author 박경서
 * @since 1.0
 **/
public interface FileReadService {

    /**
     * 여러 파일 번호로 파일 정보 조회 메서드.
     *
     * @param fileNoList 파일 번호 리스트
     * @return 파일 정보 리스트
     */
    List<GetFileResponseDto> getFilesInfo(List<Long> fileNoList);

    /**
     * 파일 번호로 파일 정보 단건 조회 메서드.
     *
     * @param fileNo 파일 번호
     * @return 파일 정보
     */
    GetFileResponseDto getFileInfo(Long fileNo);
}
