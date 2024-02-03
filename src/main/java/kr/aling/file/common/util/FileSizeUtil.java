package kr.aling.file.common.util;

/**
 * 파일 사이즈 계산 하는 Util Class.
 *
 * @author : 박경서
 * @since : 1.0
 **/
public class FileSizeUtil {

    private static final Long KILO_BYTE = 1024L;
    private static final Long MEGA_BYTE = KILO_BYTE * 1024L;
    private static final Long GIGA_BYTE = MEGA_BYTE * 1024L;
    private static final Long TERA_BYTE = GIGA_BYTE * 1024L;
    private static final String BYTE = "B";

    private static final String KB = "KB";
    private static final String MB = "MB";
    private static final String GB = "GB";
    private static final String TB = "TB";

    /**
     * Util Class 기본 생성자.
     */
    private FileSizeUtil() {
    }

    /**
     * 파일의 크기 (byte size)를 단위에 맞게 계산 하는 메서드. <br>
     * ex) 1024 byte -> 1KB (참고: {@link  FileSizeUtilTest#fileSizeCalculatorTest(Long, String) 파일 사이즈 변환 테스트})
     *
     * @param fileByteSize  파일 사이즈 (단위 byte)
     * @return 변환된 사이즈 (문자열)
     */
    public static String calculateFileSize(Long fileByteSize) {
        if (fileByteSize < KILO_BYTE) {
            return fileByteSize + BYTE;
        } else if (fileByteSize < MEGA_BYTE) {
            return fileByteSize / KILO_BYTE + KB;
        } else if (fileByteSize < GIGA_BYTE) {
            return fileByteSize / MEGA_BYTE + MB;
        } else if (fileByteSize < TERA_BYTE) {
            return fileByteSize / GIGA_BYTE + GB;
        } else {
            return fileByteSize / TERA_BYTE + TB;
        }
    }
}
