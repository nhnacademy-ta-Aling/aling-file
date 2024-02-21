package kr.aling.file.file.repository;

import java.util.List;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Aling File 조회용 Custom Repository.
 *
 * @author 박경서
 * @since 1.0
 **/
@NoRepositoryBean
public interface AlingFileReadRepositoryCustom {

    /**
     * 파일 번호 리스트 가지고 정보를 얻는 쿼리.
     *
     * @param fileNoList 파일 번호 리스트
     * @return 파일 정보 리스트
     */
    List<GetFileResponseDto> getFilesInfoByFileNoList(List<Long> fileNoList);

    /**
     * 파일 번호 가지고 단건 정보를 얻는 쿼리.
     *
     * @param fileNo 파일 번호
     * @return 파일 단건 정보
     */
    GetFileResponseDto getFileInfoByFileNo(Long fileNo);
}
