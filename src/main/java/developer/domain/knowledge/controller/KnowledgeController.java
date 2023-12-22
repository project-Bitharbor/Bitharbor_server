package developer.domain.knowledge.controller;

import com.google.gson.Gson;
import developer.domain.knowledge.dto.KnowledgeDto;
import developer.domain.knowledge.entity.Knowledge;
import developer.domain.knowledge.mapper.KnowledgeMapper;
import developer.domain.knowledge.repository.KnowledgeRepository;
import developer.domain.knowledge.service.KnowledgeService;
import developer.domain.knowledgeComment.mapper.KnowledgeCommentMapper;
import developer.domain.member.entity.Member;
import developer.domain.member.service.MemberService;
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
    private final KnowledgeCommentMapper commentMapper;
    private final JwtToken jwtToken;

    @PostMapping
    public ResponseEntity Postpost(@Validated @RequestBody KnowledgeDto.Post post,
                                   @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member member = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        Knowledge newPost = mapper.knowledgePostDtoToKnowledge(post);

        newPost.setMember(member);

        Knowledge createdPost = service.savePost(newPost);

        URI uri = URICreator.createUri("/knowledge", createdPost.getKnowledgeId());

        return ResponseEntity.created(uri).build();
    }

    @Transactional
    @PatchMapping("/{knowledge-id}")
    public ResponseEntity patchPost(@PathVariable("knowledge-id") @Positive long knowledgeId,
                                    @Validated @RequestBody KnowledgeDto.Patch patch,
                                    @RequestHeader("Authorization") String authorization) {

        authorization = authorization.replaceAll("Bearer ","");
        Member member = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

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

        KnowledgeDto.Response response = mapper.knowledgeToKnowledgeResponseDto(find);

        return new ResponseEntity(new SingleResponse<>(response), HttpStatus.OK);
    }

    @GetMapping("/{knowledge-id}/comments")
    public ResponseEntity getComments(@PathVariable("knowledge-id") @Positive long knowledgeId) {
        Knowledge find = service.findPost(knowledgeId);

        KnowledgeDto.CommentResponse response = mapper.knowledgeToKnowledgeCommentResponseDto(find);
        response.setComments(commentMapper.commentListToCommentResponseListDto(find.getKnowledgeComments()));

        return new ResponseEntity(new SingleResponse<>(response), HttpStatus.OK);
    }

    @DeleteMapping("/{knowledge-id}")
    public ResponseEntity PatchPost(@PathVariable("knowledge-id") @Positive long knowledgeId,
                                    @RequestHeader("Authorization") String authorization
    ) {

        authorization = authorization.replaceAll("Bearer ","");
        Member member = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        service.deletePost(knowledgeId, member.getMemberId());

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
