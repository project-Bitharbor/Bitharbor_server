package developer.domain.qna.repository;

import developer.domain.qna.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QnaRepository extends JpaRepository<Qna,Long> {
    Qna findByQnaId(Long qnaId);
    @Query(value = "SELECT COUNT(*) FROM qna", nativeQuery = true)
    Integer postCount();

    @Query(value = "SELECT * FROM qna WHERE title LIKE %:keyword% OR body LIKE %:keyword%", nativeQuery = true)
    Page<Qna> findQnaByTitleOrBody(String keyword, Pageable pageable);
}
