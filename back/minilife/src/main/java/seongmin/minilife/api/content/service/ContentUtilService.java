package seongmin.minilife.api.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import seongmin.minilife.domain.content.entity.Content;
import seongmin.minilife.domain.content.repository.ContentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentUtilService {
    private final ContentRepository contentRepository;

    public Content findById(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글입니다."));
    }

    public Content save(Content content) {
        contentRepository.save(content);

        return content;
    }

    public boolean existsByIdAndUserId(Long contentId, Long userId) {
        return contentRepository.existsByIdAndUserId(contentId, userId);
    }

    public List<Content> findAllContents() {
        return contentRepository.findByCompleteTrue();
    }

    public Page<Content> findContentPages(Pageable pageable) {
        return contentRepository.findByCompleteTrue(pageable);
    }

    public Page<Content> findRecentContents(Pageable pageable) {
        return contentRepository.findByCompleteTrue(pageable);
    }
}
