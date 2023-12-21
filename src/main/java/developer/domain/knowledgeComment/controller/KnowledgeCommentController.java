package developer.domain.knowledgeComment.controller;


import developer.domain.knowledge.entity.Knowledge;
import developer.domain.knowledge.mapper.KnowledgeMapper;
import developer.domain.knowledge.service.KnowledgeService;
import developer.domain.knowledgeComment.dto.KnowledgeCommentDto;
import developer.domain.knowledgeComment.entity.KnowledgeComment;
import developer.domain.knowledgeComment.mapper.KnowledgeCommentMapper;
import developer.domain.knowledgeComment.service.KnowledgeCommentService;
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
@RequestMapping("/knowledge/{knowledgeId}/comment")
public class KnowledgeCommentController {

    private final KnowledgeCommentService knowledgeCommentService;
    private final KnowledgeCommentMapper mapper;
    private final KnowledgeService knowledgeService;
    private final MemberService memberService;
     private final JwtToken jwtToken;


    @PostMapping
    public ResponseEntity postComment(@PathVariable("knowledgeId") long knowledgeId,
                                      @RequestBody KnowledgeCommentDto.Post requestBody,
                                      @RequestHeader("Authorization") String authorization
    ) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));
        Knowledge knowledge = knowledgeService.findPost(knowledgeId);

        KnowledgeComment knowledgeComment = mapper.commentPostDtoToComment(requestBody);
        knowledgeComment.setKnowledge(knowledge);
        knowledgeComment.setMember(requestMember);
        knowledgeCommentService.createComment(knowledgeComment);

        knowledge.setCommentCount(knowledge.getCommentCount() + 1);
        knowledgeService.updatePost(knowledge,knowledgeId);


        URI uri = UriComponentsBuilder.newInstance()
                .path("/"+knowledgeId+"/" + knowledgeComment.getCommentId())
                .build().toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity patchComment(
            @PathVariable("commentId") long commentId,
            @RequestBody KnowledgeCommentDto.Patch requestBody,
            @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        KnowledgeComment knowledgeComment = knowledgeCommentService.findComment(commentId);
        Member writer = knowledgeComment.getMember();

        if(Objects.equals(writer.getMemberId(), requestMember.getMemberId())){
            knowledgeComment = mapper.commentPatchDtoToComment(requestBody);
            knowledgeComment.setCommentId(commentId);
            KnowledgeComment response = knowledgeCommentService.updateComment(knowledgeComment);

            KnowledgeCommentDto.Response responseDto = mapper.commentToCommentResponseDto(response);

            return new ResponseEntity<>(new SingleResponse<>(responseDto), HttpStatus.OK);
        }

        throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(
            @PathVariable("knowledgeId") long knowledgeId,
            @PathVariable("commentId") long commentId,
            @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        KnowledgeComment communityComment = knowledgeCommentService.findComment(commentId);
        Member writer = communityComment.getMember();

        if(Objects.equals(writer.getMemberId(), requestMember.getMemberId())){

            knowledgeCommentService.deleteComment(commentId);
            Knowledge knowledge = knowledgeService.findPost(knowledgeId);

            knowledge.setCommentCount(knowledge.getCommentCount() - 1);
            knowledgeService.updatePost(knowledge,commentId);

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
        Page<KnowledgeComment> commentPage = knowledgeCommentService.findComments(pageable);


        List<KnowledgeCommentDto.Response> responses =
                mapper.commentPageToCommentResponseListDto(commentPage);



        return new ResponseEntity<>(new MultiResponse<>(responses, commentPage), HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity getComment(
            @PathVariable("commentId") Long commentId) {
        KnowledgeComment communityComment = knowledgeCommentService.findComment(commentId);

        KnowledgeCommentDto.Response responseDto = mapper.commentToCommentResponseDto(communityComment);

        return new ResponseEntity<>(new SingleResponse<>(responseDto), HttpStatus.OK);
    }
}
