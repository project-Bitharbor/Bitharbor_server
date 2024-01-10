package developer.domain.knowledgeComment.repository;

import developer.domain.communityComment.entity.CommunityComment;
import developer.domain.knowledgeComment.entity.KnowledgeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeCommentRepository extends JpaRepository<KnowledgeComment, Long> {
    @Query(value = "SELECT COUNT(*) FROM knowledge_comment WHERE knowledge_id = ?1", nativeQuery = true)
    Integer findCountCommentSize(Long knowId);
}
