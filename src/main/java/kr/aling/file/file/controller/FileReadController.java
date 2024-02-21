package kr.aling.file.file.controller;

import java.util.List;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import kr.aling.file.file.service.FileReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 파일 Read API Controller.
 *
 * @author 박경서
 * @since 1.0
 **/
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileReadController {

    private final FileReadService fileReadService;

    /**
     * 파일 번호 리스트 가지고 파일 정보 조회 API.
     *
     * @param fileNoList 파일 번호 리스트
     * @return Status 200 & 파일 정보 리스트
     */
    @GetMapping
    public ResponseEntity<List<GetFileResponseDto>> getFilesInfo(@RequestParam("no") List<Long> fileNoList) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fileReadService.getFilesInfo(fileNoList));
    }

    /**
     * 파일 번호 가지고 파일 정보 단건 조회 API.
     *
     * @param fileNo 파일 번호
     * @return Status 200 & 파일 정보
     */
    @GetMapping("/{fileNo}")
    public ResponseEntity<GetFileResponseDto> getFileInfo(@PathVariable("fileNo") Long fileNo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fileReadService.getFileInfo(fileNo));
    }

}
