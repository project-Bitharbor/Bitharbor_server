package developer.domain.community.service;

import developer.domain.community.dto.CommunityDto;
import developer.domain.community.entity.Community;
import developer.domain.community.mapper.CommunityMapper;
import developer.domain.community.repository.CommunityRepository;
import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Community findPost(long postId) {

        return repository.findByCommunityId(postId);

    }

    public Page<Community> findAllPost(Pageable pageable) {

        return repository.findAll(pageable);

    }

}
