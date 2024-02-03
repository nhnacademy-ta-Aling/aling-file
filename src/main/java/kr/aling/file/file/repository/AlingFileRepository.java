package kr.aling.file.file.repository;

import kr.aling.file.file.entity.AlingFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * File Entity Repository.
 *
 * @author : 박경서
 * @since : 1.0
 **/
public interface AlingFileRepository extends JpaRepository<AlingFile, Long> {

}
