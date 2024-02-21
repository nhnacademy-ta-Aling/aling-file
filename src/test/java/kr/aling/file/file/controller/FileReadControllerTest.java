package kr.aling.file.file.controller;

import static kr.aling.file.util.RestDocsUtil.REQUIRED;
import static kr.aling.file.util.RestDocsUtil.REQUIRED_YES;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import kr.aling.file.file.service.FileReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileReadController.class)
@AutoConfigureRestDocs(uriPort = 9080)
class FileReadControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    FileReadService fileReadService;

    GetFileResponseDto getFileResponseDto;
    String url = "/api/v1/files";

    @BeforeEach
    void setUp() {
        getFileResponseDto = new GetFileResponseDto(1L,
                1, "POST_ATTACH",
                "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e/aling-dev/HOOKS/x8iCAghRK6gYlovkzsuK-orcOSNNqVgxKtTkpmYYR.jpeg",
                "picture.jpeg", "400KB");
    }

    @Test
    @DisplayName("파일 정보 단건 조회 API 테스트")
    void getFileInfo_api_test() throws Exception {
        // given

        // when
        when(fileReadService.getFileInfo(anyLong())).thenReturn(getFileResponseDto);

        // then
        mvc.perform(RestDocumentationRequestBuilders.get(url + "/{fileNo}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("file-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        pathParameters(
                                parameterWithName("fileNo").description("파일 번호")
                        ),

                        responseFields(
                                fieldWithPath("fileNo").description("파일 번호"),
                                fieldWithPath("categoryNo").description("파일 카테고리 번호"),
                                fieldWithPath("categoryName").description("파일 카테고리 이름"),
                                fieldWithPath("path").description("파일 리소스 위치"),
                                fieldWithPath("originName").description("파일 원본 이름"),
                                fieldWithPath("fileSize").description("파일 크기")
                        )
                ));
    }

    @Test
    @DisplayName("파일 번호 리스트 파일 여러개 조회 API 테스트")
    void getFilesInfo_api_test() throws Exception {
        // given
        GetFileResponseDto fileResponseDto = new GetFileResponseDto(2L, 1, "POST_ATTACH",
                "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e/aling-dev/POST-ATTACH/YTwM6I4WrOeO7z9rYiMW-uCNYPQgdCtNCeMAyaKic.jpeg",
                "dog.jpeg", "1MB"
        );

        List<GetFileResponseDto> result = List.of(getFileResponseDto, fileResponseDto);

        // when
        when(fileReadService.getFilesInfo(anyList())).thenReturn(result);

        // then
        mvc.perform(get(url)
                        .param("no", "1", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("file-get-by-fileNoList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestParameters(
                                parameterWithName("no").description("파일 번호")
                                        .attributes(key(REQUIRED).value(REQUIRED_YES))
                        ),

                        responseFields(
                                fieldWithPath("[].fileNo").description("파일 번호"),
                                fieldWithPath("[].categoryNo").description("파일 카테고리 번호"),
                                fieldWithPath("[].categoryName").description("파일 카테고리 이름"),
                                fieldWithPath("[].path").description("파일 리소스 위치"),
                                fieldWithPath("[].originName").description("파일 원본 이름"),
                                fieldWithPath("[].fileSize").description("파일 크기")
                        )
                ));

        verify(fileReadService, times(1)).getFilesInfo(anyList());
    }

}