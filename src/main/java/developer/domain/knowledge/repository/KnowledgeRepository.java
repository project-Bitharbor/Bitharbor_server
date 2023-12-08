package developer.domain.knowledge.repository;

import developer.domain.knowledge.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeRepository extends JpaRepository<Knowledge,Long> {
    Knowledge findByKnowledgeId(Long knowledgeId);
}
