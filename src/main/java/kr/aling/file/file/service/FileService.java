package kr.aling.file.file.service;

import java.util.List;
import kr.aling.file.file.dto.response.HookResponseDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 서비스 interface.
 *
 * @author : 박경서
 * @since : 1.0
 **/
public interface FileService {

    /**
     * 파일 저장 하는 공간 return.
     *
     * @return 파일 저장 위치
     */
    String getSaveLocation();

    /**
     * 파일을 Local, Object Storage 등등에 저장.
     *
     * @param files             MultipartFile 파일들
     * @param fileCategoryNo    파일 Category 번호
     */
    void saveFile(List<MultipartFile> files, Integer fileCategoryNo);

    /**
     * Toast UI Hook 통해 이미지 저장.
     *
     * @param multipartFile     MultipartFile
     * @param fileCategoryNo    파일 Category 번호
     * @return Hook 응답 Dto
     */
    HookResponseDto saveOnlyHookImageFile(MultipartFile multipartFile, Integer fileCategoryNo);
}
