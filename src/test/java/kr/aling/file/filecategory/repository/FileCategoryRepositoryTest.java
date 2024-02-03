package kr.aling.file.filecategory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import kr.aling.file.dummy.FileCategoryDummy;
import kr.aling.file.filecategory.entity.FileCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class FileCategoryRepositoryTest {

    @Autowired
    FileCategoryRepository fileCategoryRepository;

    @Test
    @DisplayName("FileCategory Entity 저장 테스트")
    void save_fileCategory_test() {
        // given
        FileCategory fileCategoryDummy = FileCategoryDummy.fileCategoryDummy();

        // when
        FileCategory save = fileCategoryRepository.save(fileCategoryDummy);

        // then
        assertThat(save).isNotNull();
        assertThat(save.getFileCategoryNo()).isEqualTo(fileCategoryDummy.getFileCategoryNo());
        assertThat(save.getName()).isEqualTo(fileCategoryDummy.getName());
    }

}