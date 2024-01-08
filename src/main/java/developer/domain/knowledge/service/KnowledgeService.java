package developer.domain.knowledge.service;


import developer.domain.community.dto.CommunityDto;
import developer.domain.community.entity.Community;
import developer.domain.community.mapper.CommunityMapper;
import developer.domain.community.repository.CommunityRepository;
import developer.domain.knowledge.dto.KnowledgeDto;
import developer.domain.knowledge.entity.Knowledge;
import developer.domain.knowledge.mapper.KnowledgeMapper;
import developer.domain.knowledge.repository.KnowledgeRepository;
import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
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
public class KnowledgeService {

    private final KnowledgeRepository repository;
    private final KnowledgeMapper mapper;
    private final MemberService memberService;

    public Knowledge savePost(Knowledge knowledge) {

        return repository.save(knowledge);
    }

    public Knowledge updatePost(Knowledge patch,Long postId)   {

        Member member = memberService.verifiedMember(patch.getMember().getMemberId());

        Knowledge findPost = repository.findByKnowledgeId(postId);

        verifiedPostMember(findPost, member.getMemberId());

        Optional.ofNullable(patch.getTitle())
                .ifPresent(findPost::setTitle);
        Optional.ofNullable(patch.getBody())
                .ifPresent(findPost::setBody);
        Optional.ofNullable(patch.getImgURL())
                .ifPresent(findPost::setImgURL);
        Optional.ofNullable(patch.getCategory())
                .ifPresent(findPost::setCategory);
        Optional.ofNullable(patch.getCategory())
                .ifPresent(findPost::setCategory);
        return repository.save(findPost);
    }

    public Knowledge findPost(long postId) {
        return repository.findByKnowledgeId(postId);
    }

    public Page<Knowledge> findAllPost(Pageable pageable) {

        return repository.findAll(pageable);

    }

    public Page<Knowledge> findSearchPost(String keyword, Pageable pageable) {

        return repository.findKnowledgeByTitleOrRealBody(keyword, pageable);

    }

    public Page<Knowledge> findCategoryPost(String category, Pageable pageable) {

        return repository.findKnowledgeByCategory(category, pageable);

    }

    public void deletePost (long postId,long memberId) {

        Knowledge post = existsPost(postId);

        verifiedPostMember(post, memberId);

        repository.deleteById(postId);
    }

    public Knowledge existsPost (long postId) {
        Optional<Knowledge> optional = repository.findById(postId);
        Knowledge findId = optional.orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));

        return findId;
    }

    public void verifiedPostMember(Knowledge post, long memberId) {
        if (post.getMember().getMemberId() != memberId) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_WRITE);
        }
    }
    public Page<Community> findUserCarePost (Pageable pageable, List<Community> carePosts) {

        return new PageImpl<>(carePosts, pageable, carePosts.size());
    }
}
