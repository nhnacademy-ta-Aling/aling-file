package kr.aling.file.file.repository;

import static org.assertj.core.api.Assertions.assertThat;

import kr.aling.file.dummy.AlingFileDummy;
import kr.aling.file.dummy.FileCategoryDummy;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.filecategory.entity.FileCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class FileRepositoryTest {

    @Autowired
    AlingFileRepository fileRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    @DisplayName("file entity 저장 테스트")
    void file_entity_save_test() {
        // given
        FileCategory fileCategory = FileCategoryDummy.fileCategoryDummy();
        AlingFile fileDummy = AlingFileDummy.fileDummy(fileCategory);

        testEntityManager.persist(fileCategory);

        // when
        AlingFile save = fileRepository.save(fileDummy);

        // then
        assertThat(save).isNotNull();
        assertThat(save.getFileCategory().getCategoryNo()).isEqualTo(fileCategory.getCategoryNo());
        assertThat(save.getFileCategory().getName()).isEqualTo(fileCategory.getName());
        assertThat(save.getPath()).isEqualTo(fileDummy.getPath());
        assertThat(save.getOriginName()).isEqualTo(fileDummy.getOriginName());
        assertThat(save.getSaveName()).isEqualTo(fileDummy.getSaveName());
        assertThat(save.getSize()).isEqualTo(fileDummy.getSize());
        assertThat(save.getCreateAt()).isEqualTo(fileDummy.getCreateAt());
    }

}