package kr.aling.file.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import kr.aling.file.common.enums.FileSaveLocation;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.file.exception.FileSaveException;
import kr.aling.file.file.repository.AlingFileRepository;
import kr.aling.file.file.service.impl.LocalFileServiceImpl;
import kr.aling.file.filecategory.entity.FileCategory;
import kr.aling.file.filecategory.exception.FileCategoryNotFoundException;
import kr.aling.file.filecategory.repository.FileCategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class LocalFileServiceTest {

    @InjectMocks
    LocalFileServiceImpl localFileService;

    @Mock
    AlingFileRepository fileRepository;

    @Mock
    FileCategoryRepository fileCategoryRepository;

    @AfterEach
    void tearDown() {
        String path = System.getProperty("user.dir") + "/local/";
        File directoryPath = new File(path);

        for (File file : Objects.requireNonNull(directoryPath.listFiles())) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Test
    @DisplayName("saveLocation 테스트")
    void fileSaveLocation_test() {
        // given

        // when

        // then
        String actualSaveLocation = localFileService.getSaveLocation();

        assertThat(FileSaveLocation.LOCAL.getName()).isEqualTo(actualSaveLocation);
    }

    @Test
    @DisplayName("file 저장 서비스 저장 테스트")
    void file_save_service_test() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPart(new MockPart("test", "test.png", "test".getBytes()));

        // when

        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(fileRepository.save(any(AlingFile.class))).thenReturn(mock(AlingFile.class));

        // then
        localFileService.saveFile(request, 1);

        verify(fileCategoryRepository, times(1)).findById(anyInt());
        verify(fileRepository, times(1)).save(any(AlingFile.class));
    }

    @Test
    @DisplayName("file 저장 실패 테스트 - FileCategory 없음")
    void file_save_fail_test_no_fileCategory() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> localFileService.saveFile(request, 1))
                .isInstanceOf(FileCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("httpServletRequest getParts() 에서 ServletException 테스트")
    void httpServletRequest_cause_ServletException_test() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(request.getParts()).thenThrow(ServerException.class);

        // then
        assertThatThrownBy(() -> localFileService.saveFile(request, 1))
                .isInstanceOf(FileSaveException.class);
    }

    @Test
    @DisplayName("httpServletRequest getParts() 에서 IOException 테스트")
    void httpServletRequest_cause_IOException_test() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(fileCategoryRepository.findById(anyInt())).thenReturn(Optional.of(mock(FileCategory.class)));
        when(request.getParts()).thenThrow(IOException.class);

        // then
        assertThatThrownBy(() -> localFileService.saveFile(request, 1))
                .isInstanceOf(FileSaveException.class);
    }

}