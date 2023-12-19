package developer.domain.qna.controller;

import com.google.gson.Gson;
import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
import developer.domain.qna.dto.QnaDto;
import developer.domain.qna.entity.Qna;
import developer.domain.qna.mapper.QnaMapper;
import developer.domain.qna.repository.QnaRepository;
import developer.global.response.MultiResponse;
import developer.global.response.PageInfo;
import developer.global.response.SingleResponse;
import developer.global.utils.URICreator;
import developer.domain.qna.service.QnaService;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/qna")
@Validated
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QnaController {

    private final QnaService service;
    private final QnaMapper mapper;
    private final QnaRepository repository;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity Postpost(@Validated @RequestBody QnaDto.Post post) {
//        Knowledge newPost = mapper.knowledgePostDtoToKnowledge(post);

        Qna createdPost = service.savePost(post);

        URI uri = URICreator.createUri("/qna", createdPost.getQnaId());

        return ResponseEntity.created(uri).build();
    }

    @Transactional
    @PatchMapping("/{qna-id}")
    public ResponseEntity patchPost(@PathVariable("qna-id") @Positive long qnaId,
                                    @Validated @RequestBody QnaDto.Patch patch) {

        Member member = memberService.findMember(patch.getMemberId());

        Qna newPatch = mapper.qnaPatchDtoToQna(patch);
        newPatch.setMember(member);

        Qna updatedPost = service.updatePost(newPatch,qnaId);

        return new ResponseEntity(new SingleResponse<>(mapper.qnaToQnaResponseDto(updatedPost)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllCarePost(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("qnaId").descending());
        Page<Qna> postPage = service.findAllPost(pageable);

        List<QnaDto.Response> response = postPage
                .stream()
                .map(post->mapper.qnaToQnaResponseDto(post))
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

    @GetMapping("/{qna-id}")
    public ResponseEntity getPost(@PathVariable("qna-id") @Positive long qnaId) {
        Qna find = service.findPost(qnaId);
        find.setPostTime(calculateTimeDifference(find.getCreatedAt()));
        find.setView(find.getView() + 1);
        repository.save(find);

        return new ResponseEntity(new SingleResponse<>(mapper.qnaToQnaResponseDto(find)), HttpStatus.OK);
    }

    @DeleteMapping("/{qna-id}/member/{member-id}")
    public ResponseEntity PatchPost(@PathVariable("qna-id") @Positive long qnaId
            ,@PathVariable("member-id") @Positive long memberId
//            ,
//                                    @RequestHeader("Authorization") String authorization
    ) {

//        authorization = authorization.replaceAll("Bearer ","");
//        Member member = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        Member member = memberService.findMember(memberId);

        service.deletePost(qnaId,memberId);

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