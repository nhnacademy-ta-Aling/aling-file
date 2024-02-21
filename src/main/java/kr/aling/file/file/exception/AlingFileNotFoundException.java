package kr.aling.file.file.exception;

/**
 * Aling File 찾지 못한 경우 Exception.
 *
 * @author 박경서
 * @since 1.0
 **/
public class AlingFileNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Aling File Not Found";

    public AlingFileNotFoundException() {
        super(MESSAGE);
    }
}
