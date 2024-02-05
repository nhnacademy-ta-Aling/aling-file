package kr.aling.file.common.util;

import java.util.Objects;
import javax.servlet.http.Part;
import kr.aling.file.common.dto.FileInfoDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 정보를 생성 하는 Util Class.
 *
 * @author : 박경서
 * @since : 1.0
 **/
public class FileInfoUtil {

    /**
     * Util Class 기본 생성자.
     */
    private FileInfoUtil() {
    }

    /**
     * 파일의 이름, 무작위 파일 이름 생성하는 메서드.
     *
     * @param part 파일
     * @return 원본 이름, 저장 이름 Dto
     */
    public static FileInfoDto generateFileInfo(Part part) {
        String originFileName = part.getSubmittedFileName();
        String extension = originFileName.substring(originFileName.lastIndexOf("."));
        String saveFileName = RandomStringUtils.randomAlphanumeric(20) + "-"
                + RandomStringUtils.randomAlphabetic(20) + extension;

        return new FileInfoDto(originFileName, saveFileName);
    }

    /**
     * MultipartFile 가지고 파일의 이름, 무작위 파일 이름 생성 하는 메서드.
     *
     * @param multipartFile MultipartFile
     * @return 원본 이름, 저장 이름 Dto
     */
    public static FileInfoDto generateFileInfo(MultipartFile multipartFile) {
        String originFileName = multipartFile.getOriginalFilename();
        String extension = Objects.requireNonNull(originFileName).substring(originFileName.lastIndexOf("."));
        String saveFileName = RandomStringUtils.randomAlphanumeric(20) + "-"
                + RandomStringUtils.randomAlphabetic(20) + extension;

        return new FileInfoDto(originFileName, saveFileName);
    }
}
