package kr.aling.file.filecategory.entity;

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
 * FileCategory Entity.
 *
 * @author 박경서
 * @since 1.0
 **/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "file_category")
public class FileCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_category_no")
    private Integer categoryNo;

    @Column(name = "file_category_name")
    public String name;

    /**
     * FileCategory 생성자.
     *
     * @param name Category 이름
     */
    public FileCategory(String name) {
        this.name = name;
    }
}
