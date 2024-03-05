package kr.aling.file.file.controller;

import static kr.aling.file.common.util.ConstantUtil.X_FILE_CATEGORY;
import static kr.aling.file.common.util.ConstantUtil.X_FILE_SAVE_LOCATION;

import java.nio.charset.StandardCharsets;
import java.util.List;
import kr.aling.file.file.dto.response.FileDownResponseDto;
import kr.aling.file.file.dto.response.FileUploadResponseDto;
import kr.aling.file.file.dto.response.HookResponseDto;
import kr.aling.file.file.service.facade.FileFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class FileManageController {

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

    /**
     * 파일 수정 API.
     *
     * @param fileNoList       수정할 파일 번호 리스트
     * @param fileList         새로운 사진 리스트
     * @param fileSaveLocation 파일 저장 위치
     * @param fileCategoryNo   파일 Category 번호
     * @return Status 201, File 번호 리스트
     */
    @PostMapping(value = "/files", params = "no")
    public ResponseEntity<List<FileUploadResponseDto>> modifyFiles(@RequestParam("no") List<Long> fileNoList,
                                                                   @RequestPart("files") List<MultipartFile> fileList,
                                                                   @RequestHeader(X_FILE_SAVE_LOCATION)
                                                                   String fileSaveLocation,
                                                                   @RequestHeader(X_FILE_CATEGORY)
                                                                   Integer fileCategoryNo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileFacadeService.modifyFiles(fileNoList, fileList, fileCategoryNo, fileSaveLocation));
    }

    /**
     * 파일 삭제 API.
     *
     * @param fileNo           파일 번호
     * @param fileSaveLocation 파일 저장 위치
     * @return Status 204
     */
    @DeleteMapping("/files/{fileNo}")
    public ResponseEntity<Void> deleteFile(@PathVariable("fileNo") Long fileNo,
                                           @RequestHeader(X_FILE_SAVE_LOCATION) String fileSaveLocation) {
        fileFacadeService.deleteFile(fileNo, fileSaveLocation);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * 파일 다운 API.
     *
     * @param fileNo           파일 번호
     * @param fileSaveLocation 파일 저장 위치
     * @return Status 200, 파일 다운
     */
    @GetMapping("/files/{fileNo}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileNo") Long fileNo,
                                                 @RequestHeader(X_FILE_SAVE_LOCATION) String fileSaveLocation) {
        FileDownResponseDto fileDownResponseDto = fileFacadeService.downloadFile(fileNo, fileSaveLocation);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(fileDownResponseDto.getOriginName(), StandardCharsets.UTF_8)
                        .build().toString())
                .body(fileDownResponseDto.getByteArrayResource());
    }
}
