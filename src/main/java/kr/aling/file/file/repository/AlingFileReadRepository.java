package kr.aling.file.file.repository;

import kr.aling.file.file.entity.AlingFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Aling File 조회용 Repository.
 *
 * @author 박경서
 * @since 1.0
 **/
public interface AlingFileReadRepository extends JpaRepository<AlingFile, Long>, AlingFileReadRepositoryCustom {
}
