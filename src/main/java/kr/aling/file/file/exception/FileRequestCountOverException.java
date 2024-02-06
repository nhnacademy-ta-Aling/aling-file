package kr.aling.file.file.exception;

/**
 * 하나의 요청에 파일 개수가 넘어 가는 경우 발생 하는 Exception.
 *
 * @author : 박경서
 * @since : 1.0
 **/
public class FileRequestCountOverException extends RuntimeException {

    /**
     * FileRequestCountOverException.
     */
    public FileRequestCountOverException() {
        super();
    }
}
