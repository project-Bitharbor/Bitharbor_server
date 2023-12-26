package developer.domain.communityComment.controller;


import developer.domain.community.entity.Community;
import developer.domain.community.service.CommunityService;
import developer.domain.communityComment.dto.CommunityCommentDto;
import developer.domain.communityComment.entity.CommunityComment;
import developer.domain.communityComment.mapper.CommunityCommentMapper;
import developer.domain.communityComment.repository.CommunityCommentRepository;
import developer.domain.communityComment.service.CommunityCommentService;
import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import developer.global.response.MultiResponse;
import developer.global.response.SingleResponse;
import developer.login.oauth.userInfo.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/{communityId}/comment")
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;
    private final CommunityCommentMapper mapper;
    private final CommunityCommentRepository repository;
    private final CommunityService communityService;
    private final MemberService memberService;
     private final JwtToken jwtToken;


    @PostMapping
    public ResponseEntity postComment(@PathVariable("communityId") long communityId,
                                      @RequestBody CommunityCommentDto.Post requestBody,
                                      @RequestHeader("Authorization") String authorization
    ) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));
        Community community = communityService.findPost(communityId);

        CommunityComment communityComment = mapper.commentPostDtoToComment(requestBody);
        communityComment.setCommunity(community);
        communityComment.setMember(requestMember);
        communityCommentService.createComment(communityComment);

//        community.setCommentCount(community.getCommentCount() + 1);
        community.setCommentCount(repository.findCountCommentSize(communityId));
        communityService.updatePost(community,communityId);


        URI uri = UriComponentsBuilder.newInstance()
                .path("/"+communityId+"/" + communityComment.getCommentId())
                .build().toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity patchComment(
            @PathVariable("commentId") long commentId,
            @RequestBody CommunityCommentDto.Patch requestBody,
            @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        CommunityComment communityComment = communityCommentService.findComment(commentId);
        Member writer = communityComment.getMember();

        if(Objects.equals(writer.getMemberId(), requestMember.getMemberId())){
            communityComment = mapper.commentPatchDtoToComment(requestBody);
            communityComment.setCommentId(commentId);
            CommunityComment response = communityCommentService.updateComment(communityComment);

            CommunityCommentDto.Response responseDto = mapper.commentToCommentResponseDto(response);

            return new ResponseEntity<>(new SingleResponse<>(responseDto), HttpStatus.OK);
        }

        throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(
            @PathVariable("communityId") long communityId,
            @PathVariable("commentId") long commentId,
            @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        CommunityComment communityComment = communityCommentService.findComment(commentId);
        Member writer = communityComment.getMember();

        if(Objects.equals(writer.getMemberId(), requestMember.getMemberId())){

            communityCommentService.deleteComment(commentId);
            Community community = communityService.findPost(communityId);

//            community.setCommentCount(community.getCommentCount() - 1);
            community.setCommentCount(repository.findCountCommentSize(communityId));
            communityService.updatePost(community,communityId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
    }

    @GetMapping
    public ResponseEntity getComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<CommunityComment> commentPage = communityCommentService.findComments(pageable);

        List<CommunityCommentDto.Response> responses =
                mapper.commentPageToCommentResponseListDto(commentPage);

        return new ResponseEntity<>(new MultiResponse<>(responses, commentPage), HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity getComment(
            @PathVariable("commentId") Long commentId) {
        CommunityComment communityComment = communityCommentService.findComment(commentId);

        CommunityCommentDto.Response responseDto = mapper.commentToCommentResponseDto(communityComment);

        return new ResponseEntity<>(new SingleResponse<>(responseDto), HttpStatus.OK);
    }
}
