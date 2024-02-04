package kr.aling.file.file.service.impl;

import static kr.aling.file.common.enums.FileSaveLocation.LOCAL;
import static kr.aling.file.common.util.FileSizeUtil.calculateFileSize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.file.exception.FileSaveException;
import kr.aling.file.file.repository.AlingFileRepository;
import kr.aling.file.file.service.FileService;
import kr.aling.file.filecategory.entity.FileCategory;
import kr.aling.file.filecategory.exception.FileCategoryNotFoundException;
import kr.aling.file.filecategory.repository.FileCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 파일을 Local 환경에 저장 하는 Service 구현체.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Service
@RequiredArgsConstructor
@Transactional
public class LocalFileServiceImpl implements FileService {

    private final AlingFileRepository fileRepository;
    private final FileCategoryRepository fileCategoryRepository;

    private static final String ROOT_PATH = System.getProperty("user.dir") + "/local/";

    /**
     * {@inheritDoc}
     *
     * @return LOCAL 저장소
     */
    @Override
    public String getSaveLocation() {
        return LOCAL.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @param request        HttpServletRequest
     * @param fileCategoryNo 파일 Category 번호
     */
    @Override
    public void saveFile(HttpServletRequest request, Integer fileCategoryNo) {
        FileCategory fileCategory = fileCategoryRepository.findById(fileCategoryNo)
                .orElseThrow(FileCategoryNotFoundException::new);

        try {
            for (Part part : request.getParts()) {
                String originFileName = part.getSubmittedFileName();
                String extension = originFileName.substring(originFileName.lastIndexOf("."));
                String saveFileName = UUID.randomUUID() + extension;
                String path = ROOT_PATH + saveFileName;

                try (InputStream is = part.getInputStream(); OutputStream os = new FileOutputStream(path)) {

                    int len;
                    byte[] buf = new byte[4096];

                    while ((len = is.read(buf)) != -1) {
                        os.write(buf, 0, len);
                    }
                }

                AlingFile file = AlingFile.builder()
                        .fileCategory(fileCategory)
                        .path(path)
                        .originName(originFileName)
                        .saveName(saveFileName)
                        .size(calculateFileSize(part.getSize()))
                        .createAt(LocalDateTime.now())
                        .build();

                fileRepository.save(file);
            }
        } catch (ServletException | IOException e) {
            throw new FileSaveException();
        }

    }
}