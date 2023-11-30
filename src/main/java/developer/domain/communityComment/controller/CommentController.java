package developer.domain.communityComment.controller;


import developer.domain.community.entity.Community;
import developer.domain.community.service.CommunityService;
import developer.domain.communityComment.dto.CommentDto;
import developer.domain.communityComment.entity.Comment;
import developer.domain.communityComment.mapper.CommentMapper;
import developer.domain.communityComment.service.CommentService;
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
@RequestMapping("/boards/{boardId}")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper mapper;
    private final CommunityService communityService;
    private final MemberService memberService;
     private final JwtToken jwtToken;


    @PostMapping
    public ResponseEntity postComment(@PathVariable("communityId") long communityId,
                                      @RequestBody CommentDto.Post requestBody,
                                      @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestUser = memberService.findMember(requestBody.getMemberId());
        Community community = communityService.findPost(communityId);

        Comment comment = mapper.commentPostDtoToComment(requestBody);
        comment.setCommunity(community);
        comment.setMember(requestUser);
        commentService.createComment(comment);

        community.setCommentCount(community.getCommentCount() + 1);
        communityService.updatePost(community,communityId);


        URI uri = UriComponentsBuilder.newInstance()
                .path("/"+communityId+"/" + comment.getCommentId())
                .build().toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity patchComment(
            @PathVariable("commentId") long commentId,
            @RequestBody CommentDto.Patch requestBody,
            @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        Comment comment = commentService.findComment(commentId);
        Member writer = comment.getMember();

        if(Objects.equals(writer.getMemberId(), requestMember.getMemberId())){
            comment = mapper.commentPatchDtoToComment(requestBody);
            comment.setCommentId(commentId);
            Comment response = commentService.updateComment(comment);

            CommentDto.Response responseDto = mapper.commentToCommentResponseDto(response);

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

        Comment comment = commentService.findComment(commentId);
        Member writer = comment.getMember();

        if(Objects.equals(writer.getMemberId(), requestMember.getMemberId())){

            commentService.deleteComment(commentId);
            Community community = communityService.findPost(communityId);

            community.setCommentCount(community.getCommentCount() - 1);
            communityService.updatePost(community,commentId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
    }

    @GetMapping("/comments")
    public ResponseEntity getComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {


        Pageable pageable = PageRequest.of(page-1, size);
        Page<Comment> commentPage = commentService.findComments(pageable);


        List<CommentDto.Response> responses =
                mapper.commentPageToCommentResponseListDto(commentPage);



        return new ResponseEntity<>(new MultiResponse<>(responses, commentPage), HttpStatus.OK);
    }

    @GetMapping("{commentId}")
    public ResponseEntity getComment(
            @PathVariable("commentId") Long commentId) {
        Comment comment = commentService.findComment(commentId);

        CommentDto.Response responseDto = mapper.commentToCommentResponseDto(comment);

        return new ResponseEntity<>(new SingleResponse<>(responseDto), HttpStatus.OK);
    }
}
