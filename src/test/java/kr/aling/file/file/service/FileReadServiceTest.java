package kr.aling.file.file.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import kr.aling.file.file.exception.AlingFileNotFoundException;
import kr.aling.file.file.repository.AlingFileReadRepository;
import kr.aling.file.file.service.impl.FileReadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FileReadServiceTest {

    @InjectMocks
    FileReadServiceImpl fileReadService;

    @Mock
    AlingFileReadRepository alingFileReadRepository;

    GetFileResponseDto getFileResponseDto;

    @BeforeEach
    void setUp() {
        getFileResponseDto = new GetFileResponseDto(1L, 1, "category", "~~", "origin name", "100kb");
    }

    @Test
    @DisplayName("파일 번호 리스트 가지고 조회하는 서비스 테스트")
    void getFilesInfo_service_test() {
        // given
        List<GetFileResponseDto> list = List.of(getFileResponseDto);

        // when
        when(alingFileReadRepository.getFilesInfoByFileNoList(anyList())).thenReturn(list);

        // then
        fileReadService.getFilesInfo(anyList());

        verify(alingFileReadRepository, times(1)).getFilesInfoByFileNoList(anyList());
    }

    @Test
    @DisplayName("파일 번호 가지고 조회 성공 서비스 테스트")
    void getFileInfo_success_service_test() {
        // given

        // when
        when(alingFileReadRepository.existsById(anyLong())).thenReturn(true);

        // then
        fileReadService.getFileInfo(anyLong());

        verify(alingFileReadRepository, times(1)).getFileInfoByFileNo(anyLong());
    }

    @Test
    @DisplayName("파일 번호 가지고 조회 실패 서비스 테스트")
    void getFileInfo_fail_service_test() {
        // given

        // when
        when(alingFileReadRepository.existsById(anyLong())).thenReturn(false);

        // then
        assertThatThrownBy(() -> fileReadService.getFileInfo(anyLong()))
                .isInstanceOf(AlingFileNotFoundException.class)
                .hasMessageContaining(AlingFileNotFoundException.MESSAGE);

        verify(alingFileReadRepository, times(0)).getFileInfoByFileNo(anyLong());
    }

}
