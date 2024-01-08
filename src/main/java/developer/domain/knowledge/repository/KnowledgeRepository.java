package developer.domain.knowledge.repository;

import developer.domain.knowledge.entity.Knowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KnowledgeRepository extends JpaRepository<Knowledge,Long> {
    Knowledge findByKnowledgeId(Long knowledgeId);
    @Query(value = "SELECT COUNT(*) FROM knowledge", nativeQuery = true)
    Integer postCount();

    @Query(value = "SELECT * FROM knowledge WHERE title LIKE %:keyword% OR real_body LIKE %:keyword%", nativeQuery = true)
    Page<Knowledge> findKnowledgeByTitleOrRealBody(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM knowledge WHERE category = :category", nativeQuery = true)
    Page<Knowledge> findKnowledgeByCategory(String category, Pageable pageable);
}
