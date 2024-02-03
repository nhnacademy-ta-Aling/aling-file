package kr.aling.file.file.service;

import java.util.List;
import kr.aling.file.file.exception.FileSaveLocationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 여러 FileService 구현체 중 실제 사용할 FileService 선택 하는 서비스.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Service
@RequiredArgsConstructor
public class FileServiceResolver {

    private final List<FileService> fileServices;

    /**
     * 파일 저장 위치를 가지고 FileService 선택 하는 메서드.
     *
     * @param fileSaveLocation 파일 저장 위치
     * @return 파일 저장 위치에 맞는 FileService
     */
    public FileService chooseFileService(String fileSaveLocation) {
        return fileServices.stream()
                .filter(x -> x.getSaveLocation().equals(fileSaveLocation))
                .findFirst()
                .orElseThrow(FileSaveLocationException::new);
    }

}
