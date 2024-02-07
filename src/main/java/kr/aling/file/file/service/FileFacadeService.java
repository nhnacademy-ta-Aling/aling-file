package kr.aling.file.file.service;

import static kr.aling.file.common.util.ConstantUtil.FILES_REQUEST_LIMIT_COUNT;

import java.util.List;
import kr.aling.file.file.dto.response.HookResponseDto;
import kr.aling.file.file.exception.FileRequestCountOverException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
     * @param files            MultipartFile 파일들
     * @param fileCategoryNo   파일 Category 번호
     * @param fileSaveLocation 파일 저장 위치
     */
    public void uploadFiles(List<MultipartFile> files, Integer fileCategoryNo, String fileSaveLocation) {
        if (files.size() > FILES_REQUEST_LIMIT_COUNT) {
            throw new FileRequestCountOverException();
        }

        fileServiceResolver.chooseFileService(fileSaveLocation).saveFile(files, fileCategoryNo);
    }

    /**
     * Hook Image 업로드.
     *
     * @param multipartFile    MultipartFile
     * @param fileCategoryNo   파일 Category 번호
     * @param fileSaveLocation 파일 저장 위치
     * @return Hook 응답 Dto
     */
    public HookResponseDto uploadHookImage(MultipartFile multipartFile, Integer fileCategoryNo,
                                           String fileSaveLocation) {
        return fileServiceResolver.chooseFileService(fileSaveLocation)
                .saveOnlyHookImageFile(multipartFile, fileCategoryNo);
    }
}
