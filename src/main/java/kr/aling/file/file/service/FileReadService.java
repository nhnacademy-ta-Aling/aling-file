package kr.aling.file.file.service;

import java.util.List;
import kr.aling.file.file.dto.request.ReadPostFileRequestDto;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import kr.aling.file.file.dto.response.ReadPostFileResponseDto;

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

    /**
     * 여러개의 게시물에 대한 파일 경로 조회
     *
     * @param requests 게시물 번호와 대응되는 파일 번호가 포함된 요청 객체
     * @return 게시물 번호와 대응되는 파일 경로가 포함된 응답 객체
     * @author : 이성준
     * @since : 1.0
     */
    List<ReadPostFileResponseDto> getPostsFiles(List<ReadPostFileRequestDto> requests);
}
