package developer.domain.qna.repository;

import developer.domain.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Qna,Long> {
    Qna findByQnaId(Long qnaId);
}
