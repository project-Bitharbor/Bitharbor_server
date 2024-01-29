package developer.domain.qna.service;


import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
import developer.domain.qna.entity.Qna;
import developer.domain.qna.mapper.QnaMapper;
import developer.domain.qna.repository.QnaRepository;
import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository repository;
    private final QnaMapper mapper;
    private final MemberService memberService;

    public Qna savePost(Qna qna) {

        return repository.save(qna);
    }

    public Qna updatePost(Qna patch, Long postId)   {

        Member member = memberService.verifiedMember(patch.getMember().getMemberId());

        Qna findPost = repository.findByQnaId(postId);

        verifiedPostMember(findPost, member.getMemberId());

        Optional.ofNullable(patch.getTitle())
                .ifPresent(findPost::setTitle);
        Optional.ofNullable(patch.getBody())
                .ifPresent(findPost::setBody);
        Optional.ofNullable(patch.getCategory())
                .ifPresent(findPost::setCategory);
        Optional.ofNullable(patch.getCategory())
                .ifPresent(findPost::setCategory);
        return repository.save(findPost);
    }

    public Qna findPost(long postId) {
        return repository.findByQnaId(postId);
    }

    public Page<Qna> findAllPost(Pageable pageable) {

        return repository.findAll(pageable);

    }

    public Page<Qna> findSearchPost(String keyword, Pageable pageable) {

        return repository.findQnaByTitleOrRealBody(keyword, pageable);

    }

    public Page<Qna> findCategoryPost(String category, Pageable pageable) {

        return repository.findQnaByCategory(category, pageable);

    }

    public void deletePost (long postId,long memberId) {

        Qna post = existsPost(postId);

        verifiedPostMember(post, memberId);

        repository.deleteById(postId);
    }

    public Qna existsPost (long postId) {
        Optional<Qna> optional = repository.findById(postId);
        Qna findId = optional.orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));

        return findId;
    }

    public void verifiedPostMember(Qna post, long memberId) {
        if (post.getMember().getMemberId() != memberId) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_WRITE);
        }
    }
}
