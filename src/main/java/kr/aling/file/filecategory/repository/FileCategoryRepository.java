package kr.aling.file.filecategory.repository;

import kr.aling.file.filecategory.entity.FileCategory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * FileCategory Repository.
 *
 * @author : 박경서
 * @since : 1.0
 **/
public interface FileCategoryRepository extends JpaRepository<FileCategory, Integer> {
}
