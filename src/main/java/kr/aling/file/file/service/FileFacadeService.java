package kr.aling.file.file.service;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * File 파사드 서비스.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Service
@RequiredArgsConstructor
public class FileFacadeService {

    private final FileServiceResolver fileServiceResolver;

    /**
     * 파일 업로드.
     *
     * @param request          HttpServletRequest
     * @param fileCategoryNo   파일 Category 번호
     * @param fileSaveLocation 파일 저장 위치
     */
    public void uploadFiles(HttpServletRequest request, Integer fileCategoryNo, String fileSaveLocation) {
        fileServiceResolver.chooseFileService(fileSaveLocation).saveFile(request, fileCategoryNo);
    }
}
