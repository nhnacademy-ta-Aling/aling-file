package kr.aling.file.file.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import kr.aling.file.file.service.impl.LocalFileServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FileFacadeServiceTest {

    @InjectMocks
    FileFacadeService fileFacadeService;

    @Mock
    FileServiceResolver fileServiceResolver;

    @Mock
    LocalFileServiceImpl localFileService;

    @Test
    @DisplayName("FileFacadeService uploadFiles 메서드 테스트")
    void uploadFiles_method_test() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        when(fileServiceResolver.chooseFileService(anyString())).thenReturn(localFileService);

        // then
        fileFacadeService.uploadFiles(request, 1, "location");

        verify(fileServiceResolver, times(1)).chooseFileService(anyString());
    }

}