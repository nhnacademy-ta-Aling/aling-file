package kr.aling.file.file.exception;

/**
 * 사용 하지 않는 메서드 사용 시 발생 하는 Exception .
 *
 * @author 박경서
 * @since 1.0
 **/
public class DeprecatedException extends RuntimeException {

    public static final String MESSAGE = "Deprecated Method";

    public DeprecatedException() {
        super(MESSAGE);
    }
}
