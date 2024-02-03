package kr.aling.file.file.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 파일 서비스 interface.
 *
 * @author : 박경서
 * @since : 1.0
 **/
public interface FileService {

    /**
     * 파일 저장 하는 공간 return.
     *
     * @return 파일 저장 위치
     */
    String getSaveLocation();

    /**
     * 파일을 Local, Object Storage 등등에 저장.
     *
     * @param request        HttpServletRequest
     * @param fileCategoryNo 파일 Category 번호
     */
    void saveFile(HttpServletRequest request, Integer fileCategoryNo);


}
