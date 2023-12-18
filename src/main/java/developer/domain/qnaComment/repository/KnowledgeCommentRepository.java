package developer.domain.qnaComment.repository;

import developer.domain.qnaComment.entity.KnowledgeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeCommentRepository extends JpaRepository<KnowledgeComment, Long> {
}
