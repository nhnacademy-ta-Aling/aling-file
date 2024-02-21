package kr.aling.file.file.service.impl;

import java.util.List;
import kr.aling.file.common.annotation.ReadService;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import kr.aling.file.file.exception.AlingFileNotFoundException;
import kr.aling.file.file.repository.AlingFileReadRepository;
import kr.aling.file.file.service.FileReadService;
import lombok.RequiredArgsConstructor;

/**
 * File Read Service 구현채.
 *
 * @author 박경서
 * @since 1.0
 **/
@ReadService
@RequiredArgsConstructor
public class FileReadServiceImpl implements FileReadService {

    private final AlingFileReadRepository alingReadRepository;

    /**
     * {@inheritDoc}
     *
     * @param fileNoList 파일 번호 리스트
     * @return 파일 정보 리스트
     */
    @Override
    public List<GetFileResponseDto> getFilesInfo(List<Long> fileNoList) {
        return alingReadRepository.getFilesInfoByFileNoList(fileNoList);
    }

    /**
     * {@inheritDoc}
     *
     * @param fileNo 파일 번호
     * @return 파일 단건 정보
     */
    @Override
    public GetFileResponseDto getFileInfo(Long fileNo) {
        if (!alingReadRepository.existsById(fileNo)) {
            throw new AlingFileNotFoundException();
        }

        return alingReadRepository.getFileInfoByFileNo(fileNo);
    }
}
