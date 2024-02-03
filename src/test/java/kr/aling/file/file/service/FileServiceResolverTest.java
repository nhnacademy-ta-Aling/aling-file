package kr.aling.file.file.service;

import static kr.aling.file.common.enums.FileSaveLocation.LOCAL;
import static kr.aling.file.common.enums.FileSaveLocation.OBJECT_STORAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import kr.aling.file.file.exception.FileSaveLocationException;
import kr.aling.file.file.service.impl.LocalFileServiceImpl;
import kr.aling.file.file.service.impl.ObjectStorageFileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FileServiceResolverTest {

    FileServiceResolver fileServiceResolver;

    @Mock
    LocalFileServiceImpl localFileService;

    @Mock
    ObjectStorageFileServiceImpl objectStorageFileService;

    @BeforeEach
    void setUp() {
        fileServiceResolver = new FileServiceResolver(List.of(localFileService, objectStorageFileService));
    }

    @Test
    @DisplayName("LocalFileService instance 반환 테스트")
    void localFileService_instance_return_test() {
        // given

        // when
        when(localFileService.getSaveLocation()).thenReturn(LOCAL.getName());
        when(objectStorageFileService.getSaveLocation()).thenReturn("none");

        // then
        FileService fileService = fileServiceResolver.chooseFileService(LOCAL.getName());

        assertThat(fileService).isInstanceOf(LocalFileServiceImpl.class);
    }

    @Test
    @DisplayName("ObjectStorageFileService instance 반환 테스트")
    void objectStorageFileService_instance_return_test() {
        // given

        // when
        when(localFileService.getSaveLocation()).thenReturn("none");
        when(objectStorageFileService.getSaveLocation()).thenReturn(OBJECT_STORAGE.getName());

        // then
        FileService fileService = fileServiceResolver.chooseFileService(OBJECT_STORAGE.getName());

        assertThat(fileService).isInstanceOf(ObjectStorageFileServiceImpl.class);
    }

    @Test
    @DisplayName("잘못된 fileSaveLocation exception 테스트")
    void invalid_fileSaveLocation_exception_test() {
        // given

        // when
        when(localFileService.getSaveLocation()).thenReturn(LOCAL.getName());
        when(objectStorageFileService.getSaveLocation()).thenReturn(OBJECT_STORAGE.getName());

        // then
        assertThatThrownBy(() -> fileServiceResolver.chooseFileService("Invalid Location"))
                .isInstanceOf(FileSaveLocationException.class);
    }

}