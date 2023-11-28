package developer.domain.community.repository;

import developer.domain.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community,Long> {
    Community findByCommunityId(Long communityId);

}
