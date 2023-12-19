package developer.domain.qnaComment.service;


import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import developer.domain.qnaComment.entity.QnaComment;
import developer.domain.qnaComment.repository.QnaCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QnaCommentService {
    private final QnaCommentRepository qnaCommentRepository;

    public QnaCommentService(QnaCommentRepository qnaCommentRepository) {
        this.qnaCommentRepository = qnaCommentRepository;
    }

    public QnaComment createComment(QnaComment qnaComment){

        return qnaCommentRepository.save(qnaComment);
    }
    public QnaComment updateComment(QnaComment qnaComment) {

        QnaComment findQnaComment = findVerifiedComment(qnaComment.getCommentId());

        Optional.ofNullable(qnaComment.getContent())
                .ifPresent(text-> findQnaComment.setContent(text));

        return qnaCommentRepository.save(findQnaComment);
    }

    public void deleteComment(long commentId) {

        QnaComment qnaComment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));

        qnaCommentRepository.delete(qnaComment);
    }
    public Page<QnaComment> findComments(Pageable pageable){

        return qnaCommentRepository.findAll(pageable);
    }

    public QnaComment findComment(Long qnaId) {
        return findVerifiedComment(qnaId);
    }

    public QnaComment findVerifiedComment(long c_id) {
        Optional<QnaComment> optionalBoard =
                qnaCommentRepository.findById(c_id);

        return optionalBoard.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }


}
