package kr.aling.file.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import kr.aling.file.common.enums.FileSaveLocation;
import kr.aling.file.common.properties.ObjectStorageProperties;
import kr.aling.file.file.dto.response.StorageTokenResponseDto;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.file.exception.FileSaveException;
import kr.aling.file.file.repository.AlingFileRepository;
import kr.aling.file.file.service.impl.ObjectStorageFileServiceImpl;
import kr.aling.file.filecategory.entity.FileCategory;
import kr.aling.file.filecategory.exception.FileCategoryNotFoundException;
import kr.aling.file.filecategory.repository.FileCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
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

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPart(new MockPart("test", "test.png", "test".getBytes()));

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(objectStorageProperties.getStoreUrl()).thenReturn("storage url");
        when(objectStorageProperties.getContainerName()).thenReturn("container");

        when(restTemplate.getMessageConverters()).thenReturn(List.of(new MappingJackson2HttpMessageConverter()));
        when(restTemplate.execute(anyString(), any(), any(RequestCallback.class), any())).thenReturn(any(String.class));

        // then
        objectStorageFileService.saveFile(request, 1);

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
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPart(new MockPart("test", "test.png", "test".getBytes()));

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
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(StorageTokenResponseDto.class))).thenReturn(response);

        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));

        when(objectStorageProperties.getStoreUrl()).thenReturn("storage url");
        when(objectStorageProperties.getContainerName()).thenReturn("container");

        when(restTemplate.getMessageConverters()).thenReturn(List.of(new MappingJackson2HttpMessageConverter()));
        when(restTemplate.execute(anyString(), any(), any(), any())).thenReturn("200");

        // then
        objectStorageFileService.saveFile(request, 1);

        verify(objectStorageProperties, times(1)).getTokenIssueUrl();
        verify(objectStorageProperties, times(1)).getUsername();
        verify(objectStorageProperties, times(1)).getPassword();

        verify(fileCategoryRepository, times(1)).findById(anyInt());
        verify(objectStorageProperties, times(1)).getStoreUrl();
        verify(objectStorageProperties, times(1)).getContainerName();
        verify(fileRepository, times(1)).save(any(AlingFile.class));
    }

    @Test
    @DisplayName("object storage file 저장 실패 테스트 (ServletException)")
    void object_storage_file_save_fail_test_ServletException() throws ServletException, IOException {
        // given
        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));

        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(request.getParts()).thenThrow(ServletException.class);

        // then
        assertThatThrownBy(() -> objectStorageFileService.saveFile(request, 1))
                .isInstanceOf(FileSaveException.class);
    }

    @Test
    @DisplayName("object storage file 저장 실패 테스트 (IOException)")
    void object_storage_file_save_fail_test_IOException() throws ServletException, IOException {
        // given
        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));

        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(request.getParts()).thenThrow(IOException.class);

        // then
        assertThatThrownBy(() -> objectStorageFileService.saveFile(request, 1))
                .isInstanceOf(FileSaveException.class);
    }

    @Test
    @DisplayName("object storage 파일 저장 실패 테스트 - FileCateogry 없음")
    void object_storage_file_save_fail_test_no_fileCategory() {
        // given
        ReflectionTestUtils.setField(objectStorageFileService, "tokenId", "token info");
        ReflectionTestUtils.setField(objectStorageFileService, "tokenExpires", LocalDateTime.now().plusMinutes(1));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPart(new MockPart("test", "test.png", "test".getBytes()));

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> objectStorageFileService.saveFile(request, 1))
                .isInstanceOf(FileCategoryNotFoundException.class);
    }

}