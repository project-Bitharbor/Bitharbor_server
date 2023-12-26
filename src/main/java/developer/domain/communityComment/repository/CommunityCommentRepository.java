package developer.domain.communityComment.repository;

import developer.domain.communityComment.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    @Query(value = "SELECT COUNT(*) FROM community_comment WHERE community_id = ?1", nativeQuery = true)
    Integer findCountCommentSize(Long comId);

}
