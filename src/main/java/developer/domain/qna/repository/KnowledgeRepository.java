package developer.domain.qna.repository;

import developer.domain.qna.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeRepository extends JpaRepository<Knowledge,Long> {
    Knowledge findByKnowledgeId(Long knowledgeId);
}
