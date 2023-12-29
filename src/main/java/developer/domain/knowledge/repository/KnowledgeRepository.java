package developer.domain.knowledge.repository;

import developer.domain.knowledge.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KnowledgeRepository extends JpaRepository<Knowledge,Long> {
    Knowledge findByKnowledgeId(Long knowledgeId);
    @Query(value = "SELECT COUNT(*) FROM knowledge", nativeQuery = true)
    Integer postCount();
}
