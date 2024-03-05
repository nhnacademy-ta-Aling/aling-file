package kr.aling.file.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import kr.aling.file.file.dto.response.HookResponseDto;
import kr.aling.file.file.service.facade.FileFacadeService;
import kr.aling.file.file.service.impl.LocalFileServiceImpl;
import kr.aling.file.file.service.impl.ObjectStorageFileServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FileFacadeServiceTest {

    @InjectMocks
    FileFacadeService fileFacadeService;

    @Mock
    FileServiceResolver fileServiceResolver;

    @Mock
    LocalFileServiceImpl localFileService;

    @Mock
    ObjectStorageFileServiceImpl objectStorageFileService;

    @Test
    @DisplayName("FileFacadeService uploadFiles 메서드 테스트")
    void uploadFiles_method_test() {
        // given
        MockMultipartFile file = new MockMultipartFile("test", "test.png", "image/png", "test".getBytes());

        // when
        when(fileServiceResolver.chooseFileService(anyString())).thenReturn(localFileService);

        // then
        fileFacadeService.uploadFiles(List.of(file), 1, "location");

        verify(fileServiceResolver, times(1)).chooseFileService(anyString());
    }

    @Test
    @DisplayName("FileFacadeService uploadHookImage 메서드 테스트")
    void uploadHookImage_method_test() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes());
        HookResponseDto hookResponseDto = new HookResponseDto("path");

        // when
        when(fileServiceResolver.chooseFileService(anyString())).thenReturn(objectStorageFileService);
        when(objectStorageFileService.saveOnlyHookImageFile(multipartFile, 1)).thenReturn(hookResponseDto);

        // then
        HookResponseDto result = fileFacadeService.uploadHookImage(multipartFile, 1, "location");

        assertThat(result.getPath()).isEqualTo(hookResponseDto.getPath());
        verify(fileServiceResolver, times(1)).chooseFileService(anyString());
    }

    @Test
    @DisplayName("파일 수정 facade 메서드 테스트 ")
    void facade_modifyFiles_test() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes());

        // when
        when(fileServiceResolver.chooseFileService(anyString())).thenReturn(objectStorageFileService);

        // then
        fileFacadeService.modifyFiles(List.of(1L), List.of(multipartFile), 1, "location");

        verify(fileServiceResolver, times(1)).chooseFileService(anyString());
    }

    @Test
    @DisplayName("파일 삭제 facade 메서드 테스트")
    void facade_deleteFile_test() {
        // given

        // when
        when(fileServiceResolver.chooseFileService(anyString())).thenReturn(objectStorageFileService);

        // then
        fileFacadeService.deleteFile(1L, "location");

        verify(fileServiceResolver, times(1)).chooseFileService(anyString());
    }

    @Test
    @DisplayName("파일 다운 로드 facade 메서드 테스트")
    void facade_downloadFile_test() {
        // given

        // when
        when(fileServiceResolver.chooseFileService(anyString())).thenReturn(objectStorageFileService);

        // then
        fileFacadeService.downloadFile(1L, "location");

        verify(fileServiceResolver, times(1)).chooseFileService(anyString());
    }

}