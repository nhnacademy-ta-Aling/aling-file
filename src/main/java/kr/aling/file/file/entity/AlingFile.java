package kr.aling.file.file.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import kr.aling.file.filecategory.entity.FileCategory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * File Entity Class.
 *
 * @author 박경서
 * @since 1.0
 **/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "file")
public class AlingFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_no")
    private Long fileNo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "file_category_no")
    private FileCategory fileCategory;

    @NotNull
    @Column(name = "file_path")
    private String path;

    @NotNull
    @Column(name = "file_origin_name")
    private String originName;

    @NotNull
    @Column(name = "file_save_name")
    private String saveName;

    @NotNull
    @Column(name = "file_size")
    private String size;

    @NotNull
    @Column(name = "create_at")
    private LocalDateTime createAt;

    /**
     * File 생성자.
     *
     * @param fileCategory 파일 Category
     * @param path         파일 저장 위치
     * @param originName   파일 original 이름
     * @param saveName     파일 저장(uuid) 이름
     * @param size         파일 크기
     * @param createAt     생성 시간
     */
    @Builder
    public AlingFile(FileCategory fileCategory, String path, String originName, String saveName, String size,
                     LocalDateTime createAt) {
        this.fileCategory = fileCategory;
        this.path = path;
        this.originName = originName;
        this.saveName = saveName;
        this.size = size;
        this.createAt = createAt;
    }

}
