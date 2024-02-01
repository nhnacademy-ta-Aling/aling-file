package kr.aling.file.filetype.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FileType Entity.
 *
 * @author 박경서
 * @since 1.0
 **/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "file_type")
public class FileType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_type_no")
    private Integer fileTypeNo;

    @Column(name = "file_type_name")
    public String typeName;
}
