package kr.aling.file.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.aling.file.common.enums.FileSaveLocation;
import kr.aling.file.common.properties.ObjectStorageProperties;
import kr.aling.file.file.dto.response.FileDownResponseDto;
import kr.aling.file.file.dto.response.FileUploadResponseDto;
import kr.aling.file.file.dto.response.StorageTokenResponseDto;
import kr.aling.file.file.dummy.AlingFileDummy;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.file.exception.AlingFileNotFoundException;
import kr.aling.file.file.repository.AlingFileRepository;
import kr.aling.file.file.service.impl.ObjectStorageFileServiceImpl;
import kr.aling.file.filecategory.dummy.FileCategoryDummy;
import kr.aling.file.filecategory.entity.FileCategory;
import kr.aling.file.filecategory.exception.FileCategoryNotFoundException;
import kr.aling.file.filecategory.repository.FileCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
class ObjectStorageFileServiceTest {

    @InjectMocks
    ObjectStorageFileServiceImpl objectStorageFileService;

    @Mock
    ObjectStorageProperties objectStorageProperties;

    @Mock
    RestTemplate restTemplate;

    @Mock
    AlingFileRepository fileRepository;

    @Mock
    FileCategoryRepository fileCategoryRepository;

    MockMultipartFile multipartFile;
    AlingFile alingFile;

    @BeforeEach
    void setup() {
        multipartFile = new MockMultipartFile("files", "test.png", "image.png", "test".getBytes());
        alingFile = AlingFileDummy.fileDummy(FileCategoryDummy.fileCategoryDummy());
    }

    @Test
    @DisplayName("object storage, saveLocation 테스트")
    void fileSaveLocation_test() {
        // given

        // when

        // then
        String actualSaveLocation = objectStorageFileService.getSaveLocation();

        assertThat(FileSaveLocation.OBJECT_STORAGE.getName()).isEqualTo(actualSaveLocation);
    }

    @Test
    @DisplayName("파일 저장 성공 테스트 (토큰이 있어서 토큰 발급이 필요 없는 테스트)")
    void object_storage_file_save_test_with_no_issue_token() {
        // given
        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));

        AlingFile alingFile = AlingFile.builder().build();
        ReflectionTestUtils.setField(alingFile, "fileNo", 1L);

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(objectStorageProperties.getStoreUrl()).thenReturn("storage url");
        when(objectStorageProperties.getContainerName()).thenReturn("container");

        when(restTemplate.getMessageConverters()).thenReturn(List.of(new MappingJackson2HttpMessageConverter()));
        when(restTemplate.execute(anyString(), any(), any(), any())).thenReturn("200");
        when(fileRepository.save(any(AlingFile.class))).thenReturn(alingFile);

        // then
        List<FileUploadResponseDto> fileUploadResponseDtoList =
                objectStorageFileService.saveFile(List.of(multipartFile), 1);

        assertThat(fileUploadResponseDtoList).isNotNull();
        assertThat(fileUploadResponseDtoList.get(0).getFileNo()).isEqualTo(alingFile.getFileNo());

        verify(objectStorageProperties, times(0)).getTokenIssueUrl();
        verify(objectStorageProperties, times(0)).getUsername();
        verify(objectStorageProperties, times(0)).getPassword();

        verify(fileCategoryRepository, times(1)).findById(anyInt());
        verify(objectStorageProperties, times(1)).getStoreUrl();
        verify(objectStorageProperties, times(1)).getContainerName();
        verify(fileRepository, times(1)).save(any(AlingFile.class));
    }

    @Test
    @DisplayName("파일 저장 성공 테스트 (토큰이 없는 경우)")
    void object_storage_file_save_test_with_no_token() {
        // given
        StorageTokenResponseDto tokenResponseDto = new StorageTokenResponseDto();
        StorageTokenResponseDto.Access access = new StorageTokenResponseDto.Access();
        StorageTokenResponseDto.Token token = new StorageTokenResponseDto.Token();

        ReflectionTestUtils.setField(tokenResponseDto, "access", access);
        ReflectionTestUtils.setField(access, "token", token);
        ReflectionTestUtils.setField(token, "id", "token id");
        ReflectionTestUtils.setField(token, "expires", LocalDateTime.now().plusHours(1));

        ResponseEntity<StorageTokenResponseDto> response = new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);

        AlingFile alingFile = AlingFile.builder().build();
        ReflectionTestUtils.setField(alingFile, "fileNo", 1L);

        // when
        when(objectStorageProperties.getTenantId()).thenReturn("tenantId");
        when(objectStorageProperties.getUsername()).thenReturn("username");
        when(objectStorageProperties.getPassword()).thenReturn("password");

        when(objectStorageProperties.getTokenIssueUrl()).thenReturn("token url");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(StorageTokenResponseDto.class))).thenReturn(response);

        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));

        when(objectStorageProperties.getStoreUrl()).thenReturn("storage url");
        when(objectStorageProperties.getContainerName()).thenReturn("container");

        when(restTemplate.getMessageConverters()).thenReturn(List.of(new MappingJackson2HttpMessageConverter()));
        when(restTemplate.execute(anyString(), any(), any(), any())).thenReturn("200");

        when(fileRepository.save(any(AlingFile.class))).thenReturn(alingFile);

        // then
        objectStorageFileService.saveFile(List.of(multipartFile), 1);

        verify(objectStorageProperties, times(1)).getTokenIssueUrl();
        verify(objectStorageProperties, times(1)).getUsername();
        verify(objectStorageProperties, times(1)).getPassword();

        verify(fileCategoryRepository, times(1)).findById(anyInt());
        verify(objectStorageProperties, times(1)).getStoreUrl();
        verify(objectStorageProperties, times(1)).getContainerName();
        verify(fileRepository, times(1)).save(any(AlingFile.class));
    }

    @Test
    @DisplayName("object storage 파일 저장 실패 테스트 - FileCateogry 없음")
    void object_storage_file_save_fail_test_no_fileCategory() {
        // given
        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));



        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> objectStorageFileService.saveFile(List.of(multipartFile), 1))
                .isInstanceOf(FileCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("hook Image 저장 정상 성공 테스트")
    void hookImageFile_save_success_test() {
        // given
        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));

        MockMultipartFile multipartFile = new MockMultipartFile("test", "test.png", "image/png", "test".getBytes());

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));

        when(objectStorageProperties.getStoreUrl()).thenReturn("url");
        when(objectStorageProperties.getContainerName()).thenReturn("container");

        when(restTemplate.getMessageConverters()).thenReturn(List.of(new MappingJackson2HttpMessageConverter()));
        when(restTemplate.execute(anyString(), any(), any(RequestCallback.class), any())).thenReturn(any(String.class));

        // then
        objectStorageFileService.saveOnlyHookImageFile(multipartFile, 1);

        verify(fileCategoryRepository, times(1)).findById(anyInt());

        verify(objectStorageProperties, times(1)).getStoreUrl();
        verify(objectStorageProperties, times(1)).getContainerName();
    }

    @Test
    @DisplayName("hook Image 저장 성공 테스트 (토큰 발급 버전)")
    void hookImageFile_save_success_with_token_issue_test() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test", "test.png", "image/png", "test".getBytes());

        StorageTokenResponseDto tokenResponseDto = new StorageTokenResponseDto();
        StorageTokenResponseDto.Access access = new StorageTokenResponseDto.Access();
        StorageTokenResponseDto.Token token = new StorageTokenResponseDto.Token();

        ReflectionTestUtils.setField(tokenResponseDto, "access", access);
        ReflectionTestUtils.setField(access, "token", token);
        ReflectionTestUtils.setField(token, "id", "token id");
        ReflectionTestUtils.setField(token, "expires", LocalDateTime.now().plusHours(1));

        ResponseEntity<StorageTokenResponseDto> response = new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);

        // when
        when(objectStorageProperties.getTenantId()).thenReturn("tenantId");
        when(objectStorageProperties.getUsername()).thenReturn("username");
        when(objectStorageProperties.getPassword()).thenReturn("password");

        when(objectStorageProperties.getTokenIssueUrl()).thenReturn("token url");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(),
                eq(StorageTokenResponseDto.class))).thenReturn(response);

        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));

        when(objectStorageProperties.getStoreUrl()).thenReturn("storage url");
        when(objectStorageProperties.getContainerName()).thenReturn("container");

        when(restTemplate.getMessageConverters()).thenReturn(List.of(new MappingJackson2HttpMessageConverter()));
        when(restTemplate.execute(anyString(), any(), any(), any())).thenReturn("200");

        // then
        objectStorageFileService.saveOnlyHookImageFile(multipartFile, 1);

        verify(objectStorageProperties, times(1)).getTokenIssueUrl();
        verify(objectStorageProperties, times(1)).getUsername();
        verify(objectStorageProperties, times(1)).getPassword();

        verify(fileCategoryRepository, times(1)).findById(anyInt());
        verify(objectStorageProperties, times(1)).getStoreUrl();
        verify(objectStorageProperties, times(1)).getContainerName();
    }

    @Test
    @DisplayName("object storage 파일 삭제 성공 테스트 - 토큰이 있는 상황 (토큰 발급 없음)")
    void objectStorage_deleteFile_success_test() {
        // given
        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));

        // when
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(alingFile));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(),
                eq(Void.class))).thenReturn(null);

        // then
        objectStorageFileService.deleteFile(1L);

        verify(objectStorageProperties, times(0)).getTokenIssueUrl();
        verify(objectStorageProperties, times(0)).getUsername();
        verify(objectStorageProperties, times(0)).getPassword();

        verify(fileRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.DELETE), any(),
                eq(Void.class));
    }

    @Test
    @DisplayName("object storage 파일 삭제 성공 테스트 (토큰 발급 버전)")
    void objectStorage_deleteFile_success_with_token_issue_test() {
        // given
        Long fileNo = 1L;

        StorageTokenResponseDto tokenResponseDto = new StorageTokenResponseDto();
        StorageTokenResponseDto.Access access = new StorageTokenResponseDto.Access();
        StorageTokenResponseDto.Token token = new StorageTokenResponseDto.Token();

        ReflectionTestUtils.setField(tokenResponseDto, "access", access);
        ReflectionTestUtils.setField(access, "token", token);
        ReflectionTestUtils.setField(token, "id", "token id");
        ReflectionTestUtils.setField(token, "expires", LocalDateTime.now().plusHours(1));

        ResponseEntity<StorageTokenResponseDto> response = new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);

        // when
        when(objectStorageProperties.getTenantId()).thenReturn("tenantId");
        when(objectStorageProperties.getUsername()).thenReturn("username");
        when(objectStorageProperties.getPassword()).thenReturn("password");

        when(objectStorageProperties.getTokenIssueUrl()).thenReturn("token url");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(),
                eq(StorageTokenResponseDto.class))).thenReturn(response);

        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(alingFile));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(),
                eq(Void.class))).thenReturn(null);


        // then
        objectStorageFileService.deleteFile(fileNo);

        verify(objectStorageProperties, times(1)).getTokenIssueUrl();
        verify(objectStorageProperties, times(1)).getUsername();
        verify(objectStorageProperties, times(1)).getPassword();

        verify(fileRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.DELETE), any(),
                eq(Void.class));
    }

    @Test
    @DisplayName("object storage 파일 수정 성공 테스트 - 토큰이 있는 상황 (토큰 발급 없음)")
    void objectStorage_modifyFile_success_test() {
        // given
        Long fileNo = 1L;
        MockMultipartFile multipartFile = new MockMultipartFile("test", "test.png", "image/png", "test".getBytes());
        Integer fileCategoryNo = 1;

        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));

        // when
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(alingFile));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(),
                eq(Void.class))).thenReturn(null);

        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(objectStorageProperties.getStoreUrl()).thenReturn("storage url");
        when(objectStorageProperties.getContainerName()).thenReturn("container");

        when(restTemplate.getMessageConverters()).thenReturn(List.of(new MappingJackson2HttpMessageConverter()));
        when(restTemplate.execute(anyString(), any(), any(), any())).thenReturn("200");
        when(fileRepository.save(any(AlingFile.class))).thenReturn(alingFile);

        // then
        objectStorageFileService.modifyFile(List.of(fileNo), List.of(multipartFile), fileCategoryNo);

        verify(fileRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.DELETE), any(),
                eq(Void.class));

        verify(objectStorageProperties, times(0)).getTokenIssueUrl();
        verify(objectStorageProperties, times(0)).getUsername();
        verify(objectStorageProperties, times(0)).getPassword();

        verify(fileCategoryRepository, times(1)).findById(anyInt());
        verify(objectStorageProperties, times(1)).getStoreUrl();
        verify(objectStorageProperties, times(1)).getContainerName();
        verify(fileRepository, times(1)).save(any(AlingFile.class));
    }

    @Test
    @DisplayName("object storage 파일 수정 성공 테스트 (토큰 발급 버전))")
    void objectStorage_modifyFile_success_test_with_token_issue_test() {
        // given
        Long fileNo = 1L;
        MockMultipartFile multipartFile = new MockMultipartFile("test", "test.png", "image/png", "test".getBytes());
        Integer fileCategoryNo = 1;

        StorageTokenResponseDto tokenResponseDto = new StorageTokenResponseDto();
        StorageTokenResponseDto.Access access = new StorageTokenResponseDto.Access();
        StorageTokenResponseDto.Token token = new StorageTokenResponseDto.Token();

        ReflectionTestUtils.setField(tokenResponseDto, "access", access);
        ReflectionTestUtils.setField(access, "token", token);
        ReflectionTestUtils.setField(token, "id", "token id");
        ReflectionTestUtils.setField(token, "expires", LocalDateTime.now().plusHours(1));

        ResponseEntity<StorageTokenResponseDto> response = new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);

        // when
        when(objectStorageProperties.getTenantId()).thenReturn("tenantId");
        when(objectStorageProperties.getUsername()).thenReturn("username");
        when(objectStorageProperties.getPassword()).thenReturn("password");

        when(objectStorageProperties.getTokenIssueUrl()).thenReturn("token url");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(),
                eq(StorageTokenResponseDto.class))).thenReturn(response);

        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(alingFile));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(),
                eq(Void.class))).thenReturn(null);

        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(objectStorageProperties.getStoreUrl()).thenReturn("storage url");
        when(objectStorageProperties.getContainerName()).thenReturn("container");

        when(restTemplate.getMessageConverters()).thenReturn(List.of(new MappingJackson2HttpMessageConverter()));
        when(restTemplate.execute(anyString(), any(), any(), any())).thenReturn("200");
        when(fileRepository.save(any(AlingFile.class))).thenReturn(alingFile);

        // then
        objectStorageFileService.modifyFile(List.of(fileNo), List.of(multipartFile), 1);

        verify(fileRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Void.class));

        verify(objectStorageProperties, times(1)).getTokenIssueUrl();
        verify(objectStorageProperties, times(1)).getUsername();
        verify(objectStorageProperties, times(1)).getPassword();

        verify(fileCategoryRepository, times(1)).findById(anyInt());
        verify(objectStorageProperties, times(1)).getStoreUrl();
        verify(objectStorageProperties, times(1)).getContainerName();
        verify(fileRepository, times(1)).save(any(AlingFile.class));
    }

    @Test
    @DisplayName("object storage 파일 다운 실패 테스트 - 파일 번호 없는 상황")
    void objectStorage_downloadFile_fail_test_notFound_AlingFile() {
        // given

        // when
        when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> objectStorageFileService.downloadFile(1L))
                .isInstanceOf(AlingFileNotFoundException.class)
                .hasMessage(AlingFileNotFoundException.MESSAGE);
    }

    @Test
    @DisplayName("object storage 파일 다운 성공 테스트 - 토큰 발급 없는 상황")
    void objectStorage_downloadFile_success_test_with_no_token_issue() {
        // given
        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));

        byte[] bytes = "file data".getBytes();

        // when
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(alingFile));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(byte[].class)))
                .thenReturn(ResponseEntity.ok().body(bytes));

        // then
        FileDownResponseDto result = objectStorageFileService.downloadFile(anyLong());

        verify(fileRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(), eq(byte[].class));
        assertThat(result.getOriginName()).isEqualTo(alingFile.getOriginName());
        assertThat(result.getByteArrayResource().getByteArray()).isEqualTo(bytes);
    }

    @Test
    @DisplayName("object storage 파일 다운 성공 테스트 - 토큰 발급 필요 상황")
    void objectStorage_downloadFile_success_test_with_token_issue() {
        // given
        StorageTokenResponseDto tokenResponseDto = new StorageTokenResponseDto();
        StorageTokenResponseDto.Access access = new StorageTokenResponseDto.Access();
        StorageTokenResponseDto.Token token = new StorageTokenResponseDto.Token();

        ReflectionTestUtils.setField(tokenResponseDto, "access", access);
        ReflectionTestUtils.setField(access, "token", token);
        ReflectionTestUtils.setField(token, "id", "token id");
        ReflectionTestUtils.setField(token, "expires", LocalDateTime.now().plusHours(1));

        ResponseEntity<StorageTokenResponseDto> response = new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);

        byte[] bytes = "file data".getBytes();

        // when
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(alingFile));

        when(objectStorageProperties.getTenantId()).thenReturn("tenantId");
        when(objectStorageProperties.getUsername()).thenReturn("username");
        when(objectStorageProperties.getPassword()).thenReturn("password");

        when(objectStorageProperties.getTokenIssueUrl()).thenReturn("token url");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(),
                eq(StorageTokenResponseDto.class))).thenReturn(response);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(byte[].class)))
                .thenReturn(ResponseEntity.ok().body(bytes));

        // then
        FileDownResponseDto result = objectStorageFileService.downloadFile(anyLong());

        verify(objectStorageProperties, times(1)).getTokenIssueUrl();
        verify(objectStorageProperties, times(1)).getUsername();
        verify(objectStorageProperties, times(1)).getPassword();

        verify(fileRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(), eq(byte[].class));
        assertThat(result.getOriginName()).isEqualTo(alingFile.getOriginName());
        assertThat(result.getByteArrayResource().getByteArray()).isEqualTo(bytes);
    }

}