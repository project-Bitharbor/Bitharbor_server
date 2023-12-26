package developer.domain.community.repository;

import developer.domain.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityRepository extends JpaRepository<Community,Long> {
    Community findByCommunityId(Long communityId);
    @Query(value = "SELECT COUNT(*) FROM community", nativeQuery = true)
    Integer postCount();
}
