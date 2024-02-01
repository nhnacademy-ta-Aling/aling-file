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
import kr.aling.file.filetype.entity.FileType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * File Entity.
 *
 * @author 박경서
 * @since 1.0
 **/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_no")
    private Long fileNo;

    @ManyToOne
    @JoinColumn(name = "file_type_no")
    private FileType fileType;

    @Column(name = "file_path")
    private String path;

    @Column(name = "file_origin_name")
    private String originName;

    @Column(name = "file_save_name")
    private String saveName;

    @Column(name = "file_size")
    private String size;

    @Column(name = "create_at")
    private LocalDateTime createAt;

}
