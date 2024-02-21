package kr.aling.file.file.dummy;

import java.time.LocalDateTime;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.filecategory.entity.FileCategory;

public class AlingFileDummy {

    public static AlingFile fileDummy(FileCategory fileCategory) {
        return AlingFile.builder()
                .fileCategory(fileCategory)
                .path("test path")
                .originName("test originName")
                .saveName("test saveName")
                .size("10KB")
                .createAt(LocalDateTime.of(2024, 2, 1, 10, 30))
                .build();
    }
}
