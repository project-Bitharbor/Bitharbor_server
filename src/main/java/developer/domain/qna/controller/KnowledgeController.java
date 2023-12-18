package developer.domain.qna.controller;

import com.google.gson.Gson;
import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
import developer.domain.qna.dto.KnowledgeDto;
import developer.domain.qna.mapper.KnowledgeMapper;
import developer.domain.qna.repository.KnowledgeRepository;
import developer.global.response.MultiResponse;
import developer.global.response.PageInfo;
import developer.global.response.SingleResponse;
import developer.global.utils.URICreator;
import developer.domain.qna.entity.Knowledge;
import developer.domain.qna.service.KnowledgeService;
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
@RequestMapping("/knowledge")
@Validated
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class KnowledgeController {

    private final KnowledgeService service;
    private final KnowledgeMapper mapper;
    private final KnowledgeRepository repository;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity Postpost(@Validated @RequestBody KnowledgeDto.Post post) {
//        Knowledge newPost = mapper.knowledgePostDtoToKnowledge(post);

        Knowledge createdPost = service.savePost(post);

        URI uri = URICreator.createUri("/knowledge", createdPost.getKnowledgeId());

        return ResponseEntity.created(uri).build();
    }

    @Transactional
    @PatchMapping("/{knowledge-id}")
    public ResponseEntity patchPost(@PathVariable("knowledge-id") @Positive long knowledgeId,
                                    @Validated @RequestBody KnowledgeDto.Patch patch) {

        Member member = memberService.findMember(patch.getMemberId());

        Knowledge newPatch = mapper.knowledgePatchDtoToKnowledge(patch);
        newPatch.setMember(member);

        Knowledge updatedPost = service.updatePost(newPatch,knowledgeId);

        return new ResponseEntity(new SingleResponse<>(mapper.knowledgeToKnowledgeResponseDto(updatedPost)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllCarePost(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("knowledgeId").descending());
        Page<Knowledge> postPage = service.findAllPost(pageable);

        List<KnowledgeDto.Response> response = postPage
                .stream()
                .map(post->mapper.knowledgeToKnowledgeResponseDto(post))
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

    @GetMapping("/{knowledge-id}")
    public ResponseEntity getPost(@PathVariable("knowledge-id") @Positive long knowledgeId) {
        Knowledge find = service.findPost(knowledgeId);
        find.setPostTime(calculateTimeDifference(find.getCreatedAt()));
        find.setView(find.getView() + 1);
        repository.save(find);

        return new ResponseEntity(new SingleResponse<>(mapper.knowledgeToKnowledgeResponseDto(find)), HttpStatus.OK);
    }

    @DeleteMapping("/{knowledge-id}/member/{member-id}")
    public ResponseEntity PatchPost(@PathVariable("knowledge-id") @Positive long knowledgeId
            ,@PathVariable("member-id") @Positive long memberId
//            ,
//                                    @RequestHeader("Authorization") String authorization
    ) {

//        authorization = authorization.replaceAll("Bearer ","");
//        Member member = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        Member member = memberService.findMember(memberId);

        service.deletePost(knowledgeId,memberId);

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
