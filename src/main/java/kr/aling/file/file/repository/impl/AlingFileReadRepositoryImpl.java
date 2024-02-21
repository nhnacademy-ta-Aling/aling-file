package kr.aling.file.file.repository.impl;

import com.querydsl.core.types.Projections;
import java.util.List;
import kr.aling.file.file.dto.response.GetFileResponseDto;
import kr.aling.file.file.entity.AlingFile;
import kr.aling.file.file.entity.QAlingFile;
import kr.aling.file.file.repository.AlingFileReadRepositoryCustom;
import kr.aling.file.filecategory.entity.QFileCategory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * Aling File 조회용 Repository 구현체.
 *
 * @author 박경서
 * @since 1.0
 **/
public class AlingFileReadRepositoryImpl extends QuerydslRepositorySupport implements AlingFileReadRepositoryCustom {

    public AlingFileReadRepositoryImpl() {
        super(AlingFile.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param fileNoList 파일 번호 리스트
     * @return 파일 정보 리스트
     */
    @Override
    public List<GetFileResponseDto> getFilesInfoByFileNoList(List<Long> fileNoList) {
        QAlingFile alingFile = QAlingFile.alingFile;
        QFileCategory fileCategory = QFileCategory.fileCategory;

        return from(alingFile)
                .innerJoin(alingFile.fileCategory, fileCategory)
                .select(Projections.constructor(GetFileResponseDto.class,
                        alingFile.fileNo,
                        fileCategory.categoryNo, fileCategory.name,
                        alingFile.path, alingFile.originName, alingFile.size
                ))
                .where(alingFile.fileNo.in(fileNoList))
                .fetch();
    }

    /**
     * {@inheritDoc}
     *
     * @param fileNo 파일 번호
     * @return 파일 단건 정보
     */
    @Override
    public GetFileResponseDto getFileInfoByFileNo(Long fileNo) {
        QAlingFile alingFile = QAlingFile.alingFile;
        QFileCategory fileCategory = QFileCategory.fileCategory;

        return from(alingFile)
                .innerJoin(alingFile.fileCategory, fileCategory)
                .select(Projections.constructor(GetFileResponseDto.class,
                        alingFile.fileNo,
                        fileCategory.categoryNo, fileCategory.name,
                        alingFile.path, alingFile.originName, alingFile.size
                ))
                .where(alingFile.fileNo.eq(fileNo))
                .fetchOne();
    }
}
