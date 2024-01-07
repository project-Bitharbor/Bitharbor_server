package developer.domain.community.repository;

import developer.domain.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityRepository extends JpaRepository<Community,Long> {
    Community findByCommunityId(Long communityId);
    @Query(value = "SELECT COUNT(*) FROM community", nativeQuery = true)
    Integer postCount();

    @Query(value = "SELECT * FROM community WHERE title LIKE %:keyword% OR real_body LIKE %:keyword%", nativeQuery = true)
    Page<Community> findCommunityByTitleOrRealBody(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM community WHERE category = :category", nativeQuery = true)
    Page<Community> findCommunityByCategory(String category, Pageable pageable);
}
