package developer.domain.community.controller;

import com.google.gson.Gson;
import developer.domain.community.dto.CommunityDto;
import developer.domain.community.entity.Community;
import developer.domain.community.mapper.CommunityMapper;
import developer.domain.community.repository.CommunityRepository;
import developer.domain.community.service.CommunityService;
import developer.domain.communityComment.mapper.CommunityCommentMapper;
import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import developer.global.response.MultiResponse;
import developer.global.response.PageInfo;
import developer.global.response.SingleResponse;
import developer.global.utils.URICreator;
import developer.login.oauth.userInfo.JwtToken;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/community")
@Validated
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommunityController {
    private final CommunityService service;
    private final CommunityMapper mapper;
    private final CommunityRepository repository;
    private final MemberService memberService;
    private final CommunityCommentMapper commentMapper;
    private final JwtToken jwtToken;

    @PostMapping
    public ResponseEntity Postpost(@Validated @RequestBody CommunityDto.Post post,
                                   @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member member = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));


        Community newPost = mapper.communityPostDtoToCommunity(post);

        newPost.setPostTime(calculateTimeDifference(LocalDateTime.now()));
        newPost.setMember(member);

        Community createdPost = service.savePost(newPost);

        URI uri = URICreator.createUri("/post", createdPost.getCommunityId());

        return ResponseEntity.created(uri).build();
    }

    @Transactional
    @PatchMapping("/{post-id}")
    public ResponseEntity patchPost(@PathVariable("post-id") @Positive long postId,
                                    @Validated @RequestBody CommunityDto.Patch patch,
                                    @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member member = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        Community newPatch = mapper.communityPatchDtoToCommunity(patch);
        newPatch.setMember(member);

        Community updatedPost = service.updatePost(newPatch,postId);


        return new ResponseEntity(new SingleResponse<>(mapper.communityToCommunityResponseDto(updatedPost,0)),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllPost(@RequestParam() int page,
                                         @RequestParam() int size) {
        // default 값이 아닌 경우는 page 번호를 1번부터 받음.
        if (page != 0) page -= 1;
        Pageable pageable = PageRequest.of(page, size, Sort.by("communityId").descending());
        Page<Community> postPage = service.findAllPost(pageable);

        Integer postSize = repository.postCount();

        List<CommunityDto.Response> response = postPage
                .stream()
                .map(post->mapper.communityToCommunityResponseDto(post,postSize))
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Info", new Gson().toJson(pageInfo));

        return ResponseEntity.ok().headers(headers).body(new MultiResponse<>(response,postPage));

    }

    @GetMapping("/{community-id}")
    public ResponseEntity getPost(@PathVariable("community-id") @Positive long communityId) {
        Community find = service.findPost(communityId);
        find.setPostTime(calculateTimeDifference(find.getCreatedAt()));
        find.setView(find.getView() + 1);

        repository.save(find);
        Integer postSize = repository.postCount();

        CommunityDto.Response response = mapper.communityToCommunityResponseDto(find,postSize);
//        response.setComments(commentMapper.commentListToCommentResponseListDto(find.getCommunityComments()));

        return new ResponseEntity(new SingleResponse<>(response), HttpStatus.OK);
    }

    @GetMapping("/{community-id}/comments")
    public ResponseEntity getComments(@PathVariable("community-id") @Positive long communityId) {
        Community find = service.findPost(communityId);

        CommunityDto.CommentResponse response = mapper.communityToCommunityCommentResponseDto(find);
        response.setComments(commentMapper.commentListToCommentResponseListDto(find.getCommunityComments()));

        return new ResponseEntity(new SingleResponse<>(response), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity getSearchPost(@RequestParam() int page,
                                     @RequestParam() int size,
                                     @RequestParam() String keyword) {
        // default 값이 아닌 경우는 page 번호를 1번부터 받음.
        if (page != 0) page -= 1;
        Pageable pageable = PageRequest.of(page, size,Sort.by("community_id").descending());
        Page<Community> postPage = service.findSearchPost(keyword,pageable);

        Integer postSize = repository.postCount();

        List<CommunityDto.Response> response = postPage
                .stream()
                .map(post->mapper.communityToCommunityResponseDto(post,postSize))
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Info", new Gson().toJson(pageInfo));

        return ResponseEntity.ok().headers(headers).body(new MultiResponse<>(response,postPage));

    }

    @GetMapping("/category")
    public ResponseEntity getCategoryPost(@RequestParam() int page,
                                          @RequestParam() int size,
                                          @RequestParam() String category) {
        // default 값이 아닌 경우는 page 번호를 1번부터 받음.
        if (page != 0) page -= 1;
        Pageable pageable = PageRequest.of(page, size,Sort.by("community_id").descending());
        Page<Community> postPage = service.findCategoryPost(category,pageable);

        Integer postSize = repository.postCount();

        List<CommunityDto.Response> response = postPage
                .stream()
                .map(post->mapper.communityToCommunityResponseDto(post,postSize))
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Info", new Gson().toJson(pageInfo));

        return ResponseEntity.ok().headers(headers).body(new MultiResponse<>(response,postPage));

    }


    @DeleteMapping("/{community-id}")
    public ResponseEntity PatchPost(@PathVariable("community-id") @Positive long communityId,
                                    @RequestHeader("Authorization") String authorization
    ) {

        authorization = authorization.replaceAll("Bearer ","");
        Member member = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        service.deletePost(communityId,member.getMemberId());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    public String calculateTimeDifference(LocalDateTime createdAt) {
        long hoursDifference = java.time.Duration.between(createdAt, LocalDateTime.now() ).toHours();
        if (hoursDifference < 1) {
            return "방금 전";
        } else if (hoursDifference < 24) {
            return hoursDifference + "시간 전";
        } else {
            return (hoursDifference / 24) + "일 전";
        }
    }
}
