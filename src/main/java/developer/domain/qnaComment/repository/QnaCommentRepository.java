package developer.domain.qnaComment.repository;

import developer.domain.qnaComment.entity.QnaComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaCommentRepository extends JpaRepository<QnaComment, Long> {
    @Query(value = "SELECT COUNT(*) FROM qna_comment WHERE qna_id = ?1", nativeQuery = true)
    Integer findCountCommentSize(Long qnaId);
}
