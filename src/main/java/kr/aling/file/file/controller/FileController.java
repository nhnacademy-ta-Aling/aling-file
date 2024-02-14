package kr.aling.file.file.controller;

import static kr.aling.file.common.util.ConstantUtil.X_FILE_CATEGORY;
import static kr.aling.file.common.util.ConstantUtil.X_FILE_SAVE_LOCATION;

import java.util.List;
import kr.aling.file.file.dto.response.FileUploadResponseDto;
import kr.aling.file.file.dto.response.HookResponseDto;
import kr.aling.file.file.service.FileFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * File API Controller.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    private final FileFacadeService fileFacadeService;

    /**
     * File 업로드 API.
     *
     * @param files            MultipartFiles Files
     * @param fileSaveLocation 파일 저장 위치
     * @param fileCategoryNo   파일 Category 번호
     * @return Status 201, File 번호 List
     */
    @PostMapping("/files")
    public ResponseEntity<List<FileUploadResponseDto>> uploadFiles(@RequestPart("files") List<MultipartFile> files,
                                                                   @RequestHeader(X_FILE_SAVE_LOCATION)
                                                                   String fileSaveLocation,
                                                                   @RequestHeader(X_FILE_CATEGORY)
                                                                   Integer fileCategoryNo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileFacadeService.uploadFiles(files, fileCategoryNo, fileSaveLocation));
    }

    /**
     * Toast UI Hook 이미지 업로드 API.
     *
     * @param multipartFile    MultipartFile
     * @param fileSaveLocation 파일 저장 위치
     * @param fileCategoryNo   파일 Category 번호
     * @return Status 201, Hook 응답 Dto
     */
    @PostMapping("/hook-files")
    public ResponseEntity<HookResponseDto> uploadHookFiles(@RequestPart("image") MultipartFile multipartFile,
                                                           @RequestHeader(X_FILE_SAVE_LOCATION) String fileSaveLocation,
                                                           @RequestHeader(X_FILE_CATEGORY) Integer fileCategoryNo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileFacadeService.uploadHookImage(multipartFile, fileCategoryNo, fileSaveLocation));
    }

}
