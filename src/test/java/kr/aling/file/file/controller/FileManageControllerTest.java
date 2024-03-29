package kr.aling.file.file.controller;

import static kr.aling.file.common.enums.FileSaveLocation.OBJECT_STORAGE;
import static kr.aling.file.common.util.ConstantUtil.X_FILE_CATEGORY;
import static kr.aling.file.common.util.ConstantUtil.X_FILE_SAVE_LOCATION;
import static kr.aling.file.util.RestDocsUtil.REQUIRED;
import static kr.aling.file.util.RestDocsUtil.REQUIRED_YES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import kr.aling.file.file.dto.response.FileDownResponseDto;
import kr.aling.file.file.dto.response.FileUploadResponseDto;
import kr.aling.file.file.dto.response.HookResponseDto;
import kr.aling.file.file.service.facade.FileFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(FileManageController.class)
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 9080)
class FileManageControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    FileFacadeService fileFacadeService;

    MockMultipartFile multipartFileImage;
    MockMultipartFile multipartFilePdf;

    List<FileUploadResponseDto> fileUploadResponseDtoList;

    private static final String URL = "/api/v1";
    private static final String FILES = "files";

    @BeforeEach
    void setUp() {
        multipartFileImage = new MockMultipartFile(FILES, "test.png", "image/png", "image byte".getBytes());
        multipartFilePdf = new MockMultipartFile(FILES, "test2.pdf", "application/pdf", "pdf file data".getBytes());

        fileUploadResponseDtoList = List.of(new FileUploadResponseDto(1L), new FileUploadResponseDto(2L));
    }

    @Test
    @DisplayName("File 업로드 API 성공 테스트")
    void file_upload_api_success_test() throws Exception {
        // given

        // when
        when(fileFacadeService.uploadFiles(any(List.class), anyInt(), anyString())).thenReturn(
                fileUploadResponseDtoList);

        // then
        mvc.perform(multipart(URL + "/files")
                        .file(multipartFileImage)
                        .file(multipartFilePdf)
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE)
                        .header(X_FILE_CATEGORY, 4))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].fileNo").value(fileUploadResponseDtoList.get(0).getFileNo()))
                .andExpect(jsonPath("$[1].fileNo").value(fileUploadResponseDtoList.get(1).getFileNo()))
                .andDo(print())
                .andDo(document("files-upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestHeaders(
                                headerWithName(X_FILE_SAVE_LOCATION).description("파일 저장 위치")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES)),
                                headerWithName(X_FILE_CATEGORY).description("파일 Category 번호")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES))
                        ),

                        requestParts(
                                partWithName(FILES).description("파일 리스트")
                        ),

                        responseFields(
                                fieldWithPath("[].fileNo").description("파일 번호")
                        )

                ));

        verify(fileFacadeService, times(1)).uploadFiles(any(List.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("File 업로드 API 실패 테스트 - files 없는 경우")
    void file_upload_api_fail_test_no_files() throws Exception {
        // given

        // when

        // then
        mvc.perform(multipart(URL + "/files")
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE)
                        .header(X_FILE_CATEGORY, 4))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(fileFacadeService, times(0)).uploadFiles(any(List.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("File 업로드 API 실패 테스트 - X-File-Save-Location 헤더가 없는 경우")
    void file_upload_api_fail_test_noHeader_X_File_Save_location() throws Exception {
        // given

        // when

        // then
        mvc.perform(multipart(URL + "/files")
                        .file(multipartFileImage)
                        .header(X_FILE_CATEGORY, 4))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(fileFacadeService, times(0)).uploadFiles(any(List.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("File 업로드 API 실패 테스트 - X-File-Category 헤더가 없는 경우")
    void file_upload_api_fail_test_noHeader_X_File_Category() throws Exception {
        // given

        // when

        // then
        mvc.perform(multipart(URL + "/files")
                        .file(multipartFileImage)
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(fileFacadeService, times(0)).uploadFiles(any(List.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("hook file 업로드 API 성공 테스트")
    void hook_file_upload_api_success_test() throws Exception {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
        HookResponseDto hookResponseDto = new HookResponseDto("image path");

        // when
        when(fileFacadeService.uploadHookImage(any(MultipartFile.class), anyInt(), anyString())).thenReturn(
                hookResponseDto);

        // then
        mvc.perform(multipart(URL + "/hook-files")
                        .file(multipartFile)
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE)
                        .header(X_FILE_CATEGORY, 4))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("path").value(hookResponseDto.getPath()))
                .andDo(document("hook-file-upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestHeaders(
                                headerWithName(X_FILE_SAVE_LOCATION).description("파일 저장 위치")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES)),
                                headerWithName(X_FILE_CATEGORY).description("파일 Category 번호")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES))
                        ),

                        requestParts(
                                partWithName("image").description("이미지 파일")
                        ),

                        responseFields(
                                fieldWithPath("path").description("이미지 파일 위치")
                        )
                ));

        verify(fileFacadeService, times(1)).uploadHookImage(any(MultipartFile.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("hook file 업로드 API 실패 테스트 - image 파일 없음")
    void hook_file_upload_api_fail_test_no_image() throws Exception {
        // given

        // when

        // then
        mvc.perform(multipart(URL + "/hook-files")
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE)
                        .header(X_FILE_CATEGORY, 4))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(fileFacadeService, times(0)).uploadHookImage(any(MockMultipartFile.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("hook file 업로드 API 실패 테스트 - X-File-Save_Location 헤더 없음")
    void hook_file_upload_api_fail_test_noHeader_X_File_Save_Location() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("image", "test".getBytes());

        // when

        // then
        mvc.perform(multipart(URL + "/hook-files")
                        .file(file)
                        .header(X_FILE_CATEGORY, 4))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(fileFacadeService, times(0)).uploadHookImage(any(MockMultipartFile.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("hook file 업로드 API 실패 테스트 - X-File-Category 헤더 없음")
    void hook_file_upload_api_fail_test_noHeader_X_File_Category() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("image", "test".getBytes());

        // when

        // then
        mvc.perform(multipart(URL + "/hook-files")
                        .file(file)
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(fileFacadeService, times(0)).uploadHookImage(any(MockMultipartFile.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("파일 수정 API 성공 테스트")
    void modifyFiles_api_test() throws Exception {
        // given
        List<FileUploadResponseDto> result = List.of(new FileUploadResponseDto(2L));

        // when
        when(fileFacadeService.modifyFiles(anyList(), anyList(), anyInt(), anyString()))
                .thenReturn(result);

        // then
        mvc.perform(multipart(URL + "/files")
                        .file(multipartFileImage)
                        .param("no", "1")
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE)
                        .header(X_FILE_CATEGORY, 4)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("files-modify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestHeaders(
                                headerWithName(X_FILE_SAVE_LOCATION).description("파일 저장 위치")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES)),
                                headerWithName(X_FILE_CATEGORY).description("파일 Category 번호")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES))
                        ),

                        requestParameters(
                                parameterWithName("no").description("파일 번호")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES))
                        ),

                        responseFields(
                                fieldWithPath("[].fileNo").description("파일 번호")
                        )
                ));

        verify(fileFacadeService, times(1)).modifyFiles(anyList(), anyList(), anyInt(), anyString());
    }

    @Test
    @DisplayName("파일 삭제 API 성공 테스트")
    void deleteFile_api_success_test() throws Exception {
        // given

        // when
        doNothing().when(fileFacadeService).deleteFile(anyLong(), anyString());

        // then
        mvc.perform(RestDocumentationRequestBuilders.delete(URL + "/files/{fileNo}", 1L)
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("file-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestHeaders(
                                headerWithName(X_FILE_SAVE_LOCATION).description("파일 저장 위치")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES))
                        ),

                        pathParameters(
                                parameterWithName("fileNo").description("파일 번호")
                        )
                ));

        verify(fileFacadeService, times(1)).deleteFile(anyLong(), anyString());
    }

    @Test
    @DisplayName("파일 다운 API 성공 테스트")
    void downloadFile_api_success_test() throws Exception {
        // given
        FileDownResponseDto fileDownResponseDto =
                new FileDownResponseDto("origin file name", new ByteArrayResource("file".getBytes()));

        // when
        when(fileFacadeService.downloadFile(anyLong(), anyString())).thenReturn(fileDownResponseDto);

        // then
        mvc.perform(RestDocumentationRequestBuilders.get(URL + "/files/{fileNo}/download", 1L)
                        .header(X_FILE_SAVE_LOCATION, OBJECT_STORAGE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("file-download",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestHeaders(
                                headerWithName(X_FILE_SAVE_LOCATION).description("파일 저장 위치")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES))
                        ),

                        pathParameters(
                                parameterWithName("fileNo").description("파일 번호")
                        )
                ));
    }

}