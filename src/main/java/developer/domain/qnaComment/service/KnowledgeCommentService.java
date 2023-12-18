package developer.domain.qnaComment.service;


import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import developer.domain.qnaComment.entity.KnowledgeComment;
import developer.domain.qnaComment.repository.KnowledgeCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KnowledgeCommentService {
    private final KnowledgeCommentRepository knowledgeCommentRepository;

    public KnowledgeCommentService(KnowledgeCommentRepository knowledgeCommentRepository) {
        this.knowledgeCommentRepository = knowledgeCommentRepository;
    }

    public KnowledgeComment createComment(KnowledgeComment knowledgeComment){

        return knowledgeCommentRepository.save(knowledgeComment);
    }
    public KnowledgeComment updateComment(KnowledgeComment knowledgeComment) {

        KnowledgeComment findKnowledgeComment = findVerifiedComment(knowledgeComment.getCommentId());

        Optional.ofNullable(knowledgeComment.getContent())
                .ifPresent(text-> findKnowledgeComment.setContent(text));

        return knowledgeCommentRepository.save(findKnowledgeComment);
    }

    public void deleteComment(long commentId) {

        KnowledgeComment knowledgeComment = knowledgeCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));

        knowledgeCommentRepository.delete(knowledgeComment);
    }
    public Page<KnowledgeComment> findComments(Pageable pageable){

        return knowledgeCommentRepository.findAll(pageable);
    }

    public KnowledgeComment findComment(Long knowledgeId) {
        return findVerifiedComment(knowledgeId);
    }

    public KnowledgeComment findVerifiedComment(long c_id) {
        Optional<KnowledgeComment> optionalBoard =
                knowledgeCommentRepository.findById(c_id);

        return optionalBoard.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }


}
