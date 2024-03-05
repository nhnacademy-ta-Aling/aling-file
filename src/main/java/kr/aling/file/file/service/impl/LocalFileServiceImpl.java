package kr.aling.file.file.service.impl;

import static kr.aling.file.common.enums.FileSaveLocation.LOCAL;
import static kr.aling.file.common.util.FileSizeUtil.calculateFileSize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import kr.aling.file.common.dto.FileInfoDto;
import kr.aling.file.common.util.FileInfoUtil;
import kr.aling.file.file.dto.response.FileDownResponseDto;
import kr.aling.file.file.dto.response.FileUploadResponseDto;
import kr.aling.file.file.dto.response.HookResponseDto;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.file.exception.DeprecatedException;
import kr.aling.file.file.exception.FileSaveException;
import kr.aling.file.file.repository.AlingFileRepository;
import kr.aling.file.file.service.FileService;
import kr.aling.file.filecategory.entity.FileCategory;
import kr.aling.file.filecategory.exception.FileCategoryNotFoundException;
import kr.aling.file.filecategory.repository.FileCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
     * @param files          MultipartFile 파일들
     * @param fileCategoryNo 파일 Category 번호
     * @return 파일 번호 List
     */
    @Override
    public List<FileUploadResponseDto> saveFile(List<MultipartFile> files, Integer fileCategoryNo) {
        FileCategory fileCategory = fileCategoryRepository.findById(fileCategoryNo)
                .orElseThrow(FileCategoryNotFoundException::new);

        List<FileUploadResponseDto> fileUploadResponseDtoList = new ArrayList<>();

        for (MultipartFile file : files) {
            FileInfoDto fileInfoDto = FileInfoUtil.generateFileInfo(file);
            String path = ROOT_PATH + fileInfoDto.getSaveFileName();

            try (InputStream is = file.getInputStream(); OutputStream os = new FileOutputStream(path)) {

                int len;
                byte[] buf = new byte[4096];

                while ((len = is.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
            } catch (IOException e) {
                throw new FileSaveException();
            }

            AlingFile alingFile = AlingFile.builder()
                    .fileCategory(fileCategory)
                    .path(path)
                    .originName(fileInfoDto.getOriginFileName())
                    .saveName(fileInfoDto.getSaveFileName())
                    .size(calculateFileSize(file.getSize()))
                    .createAt(LocalDateTime.now())
                    .build();

            fileUploadResponseDtoList.add(new FileUploadResponseDto(fileRepository.save(alingFile).getFileNo()));
        }

        return fileUploadResponseDtoList;
    }

    /**
     * 사용 하지 않는 메서드. (Local 파일 저장)
     *
     * @param multipartFile  MultipartFile
     * @param fileCategoryNo 파일 Category 번호
     * @return null
     */
    @Override
    public HookResponseDto saveOnlyHookImageFile(MultipartFile multipartFile, Integer fileCategoryNo) {
        throw new DeprecatedException();
    }

    /**
     * 사용 하지 않는 메서드.
     *
     * @param fileNoList     파일 번호 리스트
     * @param fileList       파일 리스트
     * @param fileCategoryNo 파일 카테고리 번호
     * @return
     */
    @Override
    public List<FileUploadResponseDto> modifyFile(List<Long> fileNoList, List<MultipartFile> fileList,
                                                  Integer fileCategoryNo) {
        throw new DeprecatedException();
    }

    /**
     * 사용 하지 않는 메서드.
     *
     * @param fileNo 파일 번호
     */
    @Override
    public void deleteFile(Long fileNo) {
        throw new DeprecatedException();
    }

    /**
     * 사용 하지 않는 메서드.
     *
     * @param fileNo 파일 번호
     */
    @Override
    public FileDownResponseDto downloadFile(Long fileNo) {
        throw new DeprecatedException();
    }
}
