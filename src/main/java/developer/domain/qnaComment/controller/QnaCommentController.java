package developer.domain.qnaComment.controller;


import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
import developer.domain.qnaComment.dto.QnaCommentDto;
import developer.domain.qnaComment.entity.QnaComment;
import developer.domain.qnaComment.service.QnaCommentService;
import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import developer.global.response.MultiResponse;
import developer.global.response.SingleResponse;
import developer.domain.qna.entity.Qna;
import developer.domain.qna.service.QnaService;
import developer.domain.qnaComment.mapper.QnaCommentMapper;
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
@RequestMapping("/qna/{qnaId}")
public class QnaCommentController {

    private final QnaCommentService qnaCommentService;
    private final QnaCommentMapper mapper;
    private final QnaService qnaService;
    private final MemberService memberService;
     private final JwtToken jwtToken;


    @PostMapping
    public ResponseEntity postComment(@PathVariable("qnaId") long qnaId,
                                      @RequestBody QnaCommentDto.Post requestBody,
                                      @RequestHeader("Authorization") String authorization
    ) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));
        Qna qna = qnaService.findPost(qnaId);

        QnaComment qnaComment = mapper.commentPostDtoToComment(requestBody);
        qnaComment.setQna(qna);
        qnaComment.setMember(requestMember);
        qnaCommentService.createComment(qnaComment);

        qna.setCommentCount(qna.getCommentCount() + 1);
        qnaService.updatePost(qna,qnaId);


        URI uri = UriComponentsBuilder.newInstance()
                .path("/"+qnaId+"/" + qnaComment.getCommentId())
                .build().toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity patchComment(
            @PathVariable("commentId") long commentId,
            @RequestBody QnaCommentDto.Patch requestBody,
            @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        QnaComment qnaComment = qnaCommentService.findComment(commentId);
        Member writer = qnaComment.getMember();

        if(Objects.equals(writer.getMemberId(), requestMember.getMemberId())){
            qnaComment = mapper.commentPatchDtoToComment(requestBody);
            qnaComment.setCommentId(commentId);
            QnaComment response = qnaCommentService.updateComment(qnaComment);

            QnaCommentDto.Response responseDto = mapper.commentToCommentResponseDto(response);

            return new ResponseEntity<>(new SingleResponse<>(responseDto), HttpStatus.OK);
        }

        throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(
            @PathVariable("qnaId") long qnaId,
            @PathVariable("commentId") long commentId,
            @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member requestMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        QnaComment communityComment = qnaCommentService.findComment(commentId);
        Member writer = communityComment.getMember();

        if(Objects.equals(writer.getMemberId(), requestMember.getMemberId())){

            qnaCommentService.deleteComment(commentId);
            Qna qna = qnaService.findPost(qnaId);

            qna.setCommentCount(qna.getCommentCount() - 1);
            qnaService.updatePost(qna,commentId);

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
        Page<QnaComment> commentPage = qnaCommentService.findComments(pageable);


        List<QnaCommentDto.Response> responses =
                mapper.commentPageToCommentResponseListDto(commentPage);



        return new ResponseEntity<>(new MultiResponse<>(responses, commentPage), HttpStatus.OK);
    }

    @GetMapping("{commentId}")
    public ResponseEntity getComment(
            @PathVariable("commentId") Long commentId) {
        QnaComment communityComment = qnaCommentService.findComment(commentId);

        QnaCommentDto.Response responseDto = mapper.commentToCommentResponseDto(communityComment);

        return new ResponseEntity<>(new SingleResponse<>(responseDto), HttpStatus.OK);
    }
}
