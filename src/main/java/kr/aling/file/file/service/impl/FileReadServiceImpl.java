package kr.aling.file.file.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kr.aling.file.common.annotation.ReadService;
import kr.aling.file.file.dto.request.ReadPostFileRequestDto;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import kr.aling.file.file.dto.response.ReadPostFileResponseDto;
import kr.aling.file.file.exception.AlingFileNotFoundException;
import kr.aling.file.file.repository.AlingFileReadRepository;
import kr.aling.file.file.service.FileReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReadPostFileResponseDto> getPostsFiles(List<ReadPostFileRequestDto> requests) {
        List<ReadPostFileResponseDto> responseDtos = new ArrayList<>();

        requests.forEach(
                request -> {
                    List<Long> fileNoList = request.getFileNoList();

                    responseDtos.add(
                            new ReadPostFileResponseDto(
                                    request.getPostNo(),
                                    ObjectUtils.isEmpty(fileNoList) ?
                                            alingReadRepository.getFilesInfoByFileNoList(request.getFileNoList()) :
                                            new ArrayList<>()));
                }
        );

        return responseDtos;
    }
}
