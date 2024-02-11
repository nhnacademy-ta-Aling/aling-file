package kr.aling.file.file.service.impl;

import static kr.aling.file.common.enums.FileSaveLocation.OBJECT_STORAGE;
import static kr.aling.file.common.util.ConstantUtil.X_AUTH_TOKEN;
import static kr.aling.file.common.util.FileSizeUtil.calculateFileSize;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kr.aling.file.common.dto.FileInfoDto;
import kr.aling.file.common.properties.ObjectStorageProperties;
import kr.aling.file.common.util.FileInfoUtil;
import kr.aling.file.file.dto.request.StorageTokenRequestDto;
import kr.aling.file.file.dto.response.FileUploadResponseDto;
import kr.aling.file.file.dto.response.HookResponseDto;
import kr.aling.file.file.dto.response.StorageTokenResponseDto;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.file.exception.FileSaveException;
import kr.aling.file.file.repository.AlingFileRepository;
import kr.aling.file.file.service.FileService;
import kr.aling.file.filecategory.entity.FileCategory;
import kr.aling.file.filecategory.exception.FileCategoryNotFoundException;
import kr.aling.file.filecategory.repository.FileCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일을 Object Storage 장소에 저장 하는 FileServcie 구현체.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ObjectStorageFileServiceImpl implements FileService {

    private final ObjectStorageProperties objectStorageProperties;
    private final RestTemplate restTemplate;
    private final AlingFileRepository fileRepository;
    private final FileCategoryRepository fileCategoryRepository;

    private String tokenId;
    private LocalDateTime tokenExpires;
    private static final String DIRECTORY = "/";

    /**
     * {@inheritDoc}
     *
     * @return NHN Cloud Object Storage
     */
    @Override
    public String getSaveLocation() {
        return OBJECT_STORAGE.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @param files             MultipartFile 파일들
     * @param fileCategoryNo    파일 Category 번호
     * @return 파일 번호 List
     */
    @Override
    public List<FileUploadResponseDto> saveFile(List<MultipartFile> files, Integer fileCategoryNo) {
        if (hasToIssuedToken()) {
            requestStorageToken();
        }

        FileCategory fileCategory = fileCategoryRepository.findById(fileCategoryNo)
                .orElseThrow(FileCategoryNotFoundException::new);

        List<FileUploadResponseDto> fileUploadResponseDtoList = new ArrayList<>();

        for (MultipartFile file : files) {
            FileInfoDto fileInfoDto = FileInfoUtil.generateFileInfo(file);
            String pathUrl = objectStorageProperties.getStoreUrl() + DIRECTORY
                    + objectStorageProperties.getContainerName() + DIRECTORY
                    + fileCategory.getName() + DIRECTORY + fileInfoDto.getSaveFileName();

            final RequestCallback requestCallback = req -> {
                req.getHeaders().add(X_AUTH_TOKEN, tokenId);

                try (InputStream is = file.getInputStream()) {
                    int len;
                    byte[] buf = new byte[4096];

                    while ((len = is.read(buf)) != -1) {
                        req.getBody().write(buf, 0, len);
                    }
                } catch (IOException e) {
                    throw new FileSaveException();
                }
            };

            HttpMessageConverterExtractor<String> responseExtractor
                    = new HttpMessageConverterExtractor<>(String.class, restTemplate.getMessageConverters());

            restTemplate.execute(pathUrl, HttpMethod.PUT, requestCallback, responseExtractor);

            AlingFile alingFile = AlingFile.builder()
                    .fileCategory(fileCategory)
                    .path(pathUrl)
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
     * {@inheritDoc}
     *
     * @param multipartFile     MultipartFile
     * @param fileCategoryNo    파일 Category 번호
     * @return 이미지 주소
     */
    @Override
    public HookResponseDto saveOnlyHookImageFile(MultipartFile multipartFile, Integer fileCategoryNo) {
        if (hasToIssuedToken()) {
            requestStorageToken();
        }

        FileCategory fileCategory = fileCategoryRepository.findById(fileCategoryNo)
                .orElseThrow(FileCategoryNotFoundException::new);

        FileInfoDto fileInfoDto = FileInfoUtil.generateFileInfo(multipartFile);
        String pathUrl = objectStorageProperties.getStoreUrl() + DIRECTORY
                + objectStorageProperties.getContainerName() + DIRECTORY
                + fileCategory.getName() + DIRECTORY + fileInfoDto.getSaveFileName();

        final RequestCallback requestCallback = req -> {
            req.getHeaders().add(X_AUTH_TOKEN, tokenId);

            try (InputStream is = multipartFile.getInputStream()) {
                int len;
                byte[] buf = new byte[4096];

                while ((len = is.read(buf)) != -1) {
                    req.getBody().write(buf, 0, len);
                }
            }
        };

        HttpMessageConverterExtractor<String> responseExtractor
                = new HttpMessageConverterExtractor<>(String.class, restTemplate.getMessageConverters());

        restTemplate.execute(pathUrl, HttpMethod.PUT, requestCallback, responseExtractor);

        return new HookResponseDto(pathUrl);
    }


    /**
     * 토큰을 요청해 발급 받는 메서드.
     */
    private void requestStorageToken() {
        StorageTokenRequestDto storageTokenRequestDto = StorageTokenRequestDto.makeDto(
                objectStorageProperties.getTenantId(),
                objectStorageProperties.getUsername(),
                objectStorageProperties.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StorageTokenResponseDto tokenResponseDto = restTemplate.exchange(
                objectStorageProperties.getTokenIssueUrl(),
                HttpMethod.POST,
                new HttpEntity<>(storageTokenRequestDto, headers),
                StorageTokenResponseDto.class
        ).getBody();

        this.tokenId = Objects.requireNonNull(tokenResponseDto).getAccess().getToken().getId();
        this.tokenExpires = Objects.requireNonNull(tokenResponseDto).getAccess().getToken().getExpires();
    }

    /**
     * 토큰을 재발급 필요한 여부 체크 하는 메서드. <br>
     * <ol>
     *     <li>토큰이 없는 경우</li>
     *     <li>토큰 유효 시간이 30초 보다 적은 경우</li>
     * </ol>
     *
     * @return boolean
     */
    private boolean hasToIssuedToken() {
        return ((Objects.isNull(tokenId)) || isTokenExpiresValid());
    }

    /**
     * 토큰의 유효 시간이 남아 있는지 체크 하는 메서드. <Br>
     * 현재 시간 기준 30초 보다 적으면 토큰 재발급 필요.
     *
     * @return boolean
     */
    private boolean isTokenExpiresValid() {
        return tokenExpires.minusSeconds(30).isBefore(LocalDateTime.now());
    }
}
