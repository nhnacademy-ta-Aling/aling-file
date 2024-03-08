package kr.aling.file.file.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시물에 대한 파일 경로 응답 객체입니다.
 * 게시물 번호와 해당 게시물에 포함된 파일경로를 담습니다
 *
 * @author : 이성준
 * @since : 1.0
 */
@Getter
@AllArgsConstructor
public class ReadPostFileResponseDto {


    private Long postNo;

    private List<GetFileResponseDto> files;
}
