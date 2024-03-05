package kr.aling.file.file.service.facade;

import java.util.List;
import kr.aling.file.file.dto.response.FileDownResponseDto;
import kr.aling.file.file.dto.response.FileUploadResponseDto;
import kr.aling.file.file.dto.response.HookResponseDto;
import kr.aling.file.file.service.FileServiceResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * File 파사드 서비스.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class FileFacadeService {

    private final FileServiceResolver fileServiceResolver;

    /**
     * 파일 업로드.
     *
     * @param files            MultipartFile 파일들
     * @param fileCategoryNo   파일 Category 번호
     * @param fileSaveLocation 파일 저장 위치
     * @return 파일 번호 List.
     */
    public List<FileUploadResponseDto> uploadFiles(List<MultipartFile> files, Integer fileCategoryNo,
                                                   String fileSaveLocation) {
        return fileServiceResolver.chooseFileService(fileSaveLocation).saveFile(files, fileCategoryNo);
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

    /**
     * 파일 수정.
     *
     * @param fileNoList       수정 하려는 파일 번호 리스트
     * @param fileList         새로운 파일 리스트
     * @param fileCategoryNo   파일 Category 번호
     * @param fileSaveLocation 파일 저장 위치
     * @return 파일 번호 List
     */
    public List<FileUploadResponseDto> modifyFiles(List<Long> fileNoList, List<MultipartFile> fileList,
                                                   Integer fileCategoryNo, String fileSaveLocation) {
        return fileServiceResolver.chooseFileService(fileSaveLocation).modifyFile(fileNoList, fileList, fileCategoryNo);
    }

    /**
     * 파일 삭제.
     *
     * @param fileNo           파일 번호
     * @param fileSaveLocation 파일 저장 위치
     */
    public void deleteFile(Long fileNo, String fileSaveLocation) {
        fileServiceResolver.chooseFileService(fileSaveLocation).deleteFile(fileNo);
    }

    /**
     * 파일 다운 로드.
     *
     * @param fileNo           파일 번호
     * @param fileSaveLocation 파일 저장 위치
     * @return 파일 다운 응답 Dto
     */
    public FileDownResponseDto downloadFile(Long fileNo, String fileSaveLocation) {
        return fileServiceResolver.chooseFileService(fileSaveLocation).downloadFile(fileNo);
    }
}
