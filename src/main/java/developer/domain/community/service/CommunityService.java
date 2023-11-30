package developer.domain.community.service;

import developer.domain.community.dto.CommunityDto;
import developer.domain.community.entity.Community;
import developer.domain.community.mapper.CommunityMapper;
import developer.domain.community.repository.CommunityRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository repository;
    private final CommunityMapper mapper;
    private final MemberService memberService;

    public Community savePost(CommunityDto.Post community) {

        Member member = memberService.verifiedMember(community.getMemberId());
        Community newCommunity = mapper.communityPostDtoToCommunity(community);
        newCommunity.setMember(member);


        return repository.save(newCommunity);
    }

    public Community updatePost(Community patch,Long postId)   {

        Member member = memberService.verifiedMember(patch.getMember().getMemberId());

        Community findPost = repository.findByCommunityId(postId);

        verifiedPostUser(findPost, member.getMemberId());

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
        Optional.ofNullable(patch.getTags())
                .ifPresent(findPost::setTags);
        return repository.save(findPost);
    }

    public Community findPost(long postId) {

        return repository.findByCommunityId(postId);

    }

    public Page<Community> findAllPost(Pageable pageable) {

        return repository.findAll(pageable);

    }

    public void deletePost (long postId,long userId) {

        Community post = existsPost(postId);

        verifiedPostUser(post, userId);

        repository.deleteById(postId);
    }

    public Community existsPost (long postId) {
        Optional<Community> optional = repository.findById(postId);
        Community findId = optional.orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));

        return findId;
    }

    public void verifiedPostUser(Community post, long memberId) {
        if (post.getMember().getMemberId() != memberId) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_WRITE);
        }
    }
    public Page<Community> findUserCarePost (Pageable pageable, List<Community> carePosts) {

        return new PageImpl<>(carePosts, pageable, carePosts.size());
    }



}
