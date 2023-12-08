package developer.domain.communityComment.service;


import developer.domain.communityComment.entity.CommunityComment;
import developer.domain.communityComment.repository.CommunityCommentRepository;
import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommunityCommentService {
    private final CommunityCommentRepository communityCommentRepository;

    public CommunityCommentService(CommunityCommentRepository communityCommentRepository) {
        this.communityCommentRepository = communityCommentRepository;
    }

    public CommunityComment createComment(CommunityComment communityComment){

        return communityCommentRepository.save(communityComment);
    }
    public CommunityComment updateComment(CommunityComment communityComment) {

        CommunityComment findCommunityComment = findVerifiedComment(communityComment.getCommentId());

        Optional.ofNullable(communityComment.getContent())
                .ifPresent(text-> findCommunityComment.setContent(text));

        return communityCommentRepository.save(findCommunityComment);
    }

    public void deleteComment(long commentId) {

        CommunityComment communityComment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));

        communityCommentRepository.delete(communityComment);
    }
    public Page<CommunityComment> findComments(Pageable pageable){

        return communityCommentRepository.findAll(pageable);
    }

    public CommunityComment findComment(Long commentId) {
        return findVerifiedComment(commentId);
    }

    public CommunityComment findVerifiedComment(long c_id) {
        Optional<CommunityComment> optionalBoard =
                communityCommentRepository.findById(c_id);

        return optionalBoard.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }


}
