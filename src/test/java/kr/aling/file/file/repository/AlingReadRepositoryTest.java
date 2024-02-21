package kr.aling.file.file.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import kr.aling.file.file.dummy.AlingFileDummy;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.filecategory.dummy.FileCategoryDummy;
import kr.aling.file.filecategory.entity.FileCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class AlingReadRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    AlingFileReadRepository alingFileReadRepository;

    FileCategory fileCategory;

    @BeforeEach
    void setUp() {
        fileCategory = FileCategoryDummy.fileCategoryDummy();

        testEntityManager.persist(fileCategory);
    }

    @Test
    @DisplayName("파일 번호 리스트 가지고 파일 정보 조회 쿼리 테스트")
    void get_filesInfo_by_fileNoList() {
        // given
        AlingFile file = AlingFileDummy.fileDummy(fileCategory);

        AlingFile save = alingFileReadRepository.save(file);

        List<Long> fileNoList = List.of(save.getFileNo());

        // when
        List<GetFileResponseDto> result = alingFileReadRepository.getFilesInfoByFileNoList(fileNoList);

        // then
        assertThat(result.get(0).getFileNo()).isEqualTo(save.getFileNo());
        assertThat(result.get(0).getCategoryNo()).isNotNull();
        assertThat(result.get(0).getCategoryName()).isEqualTo(fileCategory.getName());
        assertThat(result.get(0).getPath()).isEqualTo(file.getPath());
        assertThat(result.get(0).getOriginName()).isEqualTo(file.getOriginName());
        assertThat(result.get(0).getFileSize()).isEqualTo(file.getSize());
    }

    @Test
    @DisplayName("파일 번호 가지고 파일 정보 단건 조회 쿼리 테스트")
    void get_fileInfo_by_fileNo() {
        // given
        AlingFile file = AlingFileDummy.fileDummy(fileCategory);

        AlingFile save = alingFileReadRepository.save(file);

        // when
        GetFileResponseDto result = alingFileReadRepository.getFileInfoByFileNo(save.getFileNo());

        // then
        assertThat(result.getFileNo()).isEqualTo(save.getFileNo());
        assertThat(result.getCategoryNo()).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo(fileCategory.getName());
        assertThat(result.getPath()).isEqualTo(file.getPath());
        assertThat(result.getOriginName()).isEqualTo(file.getOriginName());
        assertThat(result.getFileSize()).isEqualTo(file.getSize());
    }

}
