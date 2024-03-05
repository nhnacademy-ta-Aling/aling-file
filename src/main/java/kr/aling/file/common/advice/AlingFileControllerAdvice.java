package kr.aling.file.common.advice;

import kr.aling.file.file.exception.DeprecatedException;
import kr.aling.file.file.exception.FileSaveException;
import kr.aling.file.file.exception.FileSaveLocationException;
import kr.aling.file.filecategory.exception.FileCategoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Aling File API 서버 공통 에외 처리 RestControllerAdvice.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@RestControllerAdvice
public class AlingFileControllerAdvice {

    /**
     * 잘못된 Enum 같은 값이 있는 경우의 예외 처리.
     *
     * @return Http Status 400
     */
    @ExceptionHandler({FileCategoryNotFoundException.class, FileSaveLocationException.class, DeprecatedException.class})
    public ResponseEntity<Void> badRequestException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    /**
     * 파일 저장 실패의 예외 처리.
     *
     * @return Http Status 500
     */
    @ExceptionHandler({FileSaveException.class})
    public ResponseEntity<Void> serverException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
