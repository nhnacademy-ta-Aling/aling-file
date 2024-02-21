package kr.aling.file.filecategory.dummy;

import kr.aling.file.filecategory.entity.FileCategory;

public class FileCategoryDummy {

    public static FileCategory fileCategoryDummy() {
        return new FileCategory("test fileCategoryName");
    }
}
