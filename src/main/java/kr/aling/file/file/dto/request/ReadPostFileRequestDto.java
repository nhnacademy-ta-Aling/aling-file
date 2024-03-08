package kr.aling.file.file.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시물에 대한 파일 경로 요청 객체입니다.
 * 게시물 번호와 해당 게시물에 포함된 파일번호를 담습니다
 *
 * @author : 이성준
 * @since : 1.0
 */
@Getter
@NoArgsConstructor
public class ReadPostFileRequestDto {
    private Long postNo;
    private List<Long> fileNoList;
}
