package seongmin.minilife.domain.tag.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import seongmin.minilife.domain.tag.dto.AllTagsRes;
import seongmin.minilife.domain.tag.dto.CountTagDto;
import seongmin.minilife.domain.tag.dto.GetTagRes;
import seongmin.minilife.domain.tag.entity.QContentTag;
import seongmin.minilife.domain.tag.entity.QTag;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QTagRepositoryImpl implements QTagRepository {

    private final JPAQueryFactory jpaQueryFactory;

    QContentTag contentTag = QContentTag.contentTag;
    QTag tag = QTag.tag;

    @Override
    public List<CountTagDto> countTagsInContents() {
        return jpaQueryFactory
                .select(Projections.constructor(CountTagDto.class,
                        tag.tagName,
                        contentTag.content.id.countDistinct()))
                .from(contentTag)
                .join(contentTag.tag, tag)
                .groupBy(tag.tagName)
                .fetch();
    }
}
