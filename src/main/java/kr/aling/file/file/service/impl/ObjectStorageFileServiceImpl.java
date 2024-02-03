package kr.aling.file.file.service.impl;

import static kr.aling.file.common.enums.FileSaveLocation.OBJECT_STORAGE;

import javax.servlet.http.HttpServletRequest;
import kr.aling.file.file.service.FileService;
import org.springframework.stereotype.Service;

/**
 * Some description here.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Service
public class ObjectStorageFileServiceImpl implements FileService {


    @Override
    public String getSaveLocation() {
        return OBJECT_STORAGE.getName();
    }

    @Override
    public void saveFile(HttpServletRequest request, Integer fileCategoryNo) {


    }
}
