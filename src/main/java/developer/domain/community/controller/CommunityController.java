package developer.domain.community.controller;

import com.google.gson.Gson;
import developer.domain.community.dto.CommunityDto;
import developer.domain.community.entity.Community;
import developer.domain.community.mapper.CommunityMapper;
import developer.domain.community.service.CommunityService;
import developer.global.response.MultiResponse;
import developer.global.response.PageInfo;
import developer.global.response.SingleResponse;
import developer.global.utils.URICreator;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/community")
@Validated
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommunityController {
    private final CommunityService service;
    private final CommunityMapper mapper;
    @PostMapping
    public ResponseEntity Postpost(@Validated @RequestBody CommunityDto.Post post) {
//        Community newPost = mapper.communityPostDtoToCommunity(post);

        Community createdPost = service.savePost(post);

        URI uri = URICreator.createUri("/post", createdPost.getCommunityId());

        return ResponseEntity.created(uri).build();
    }

    @Transactional
    @PatchMapping("/{post-id}")
    public ResponseEntity patchPost(@Validated @RequestBody CommunityDto.Patch patch) {
//        Post newPost = mapper.patchPostDtoToPost(patch);
//
//        Post updatedPost = service.savePost(newPost);
//
//        URI uri = URICreator.createUri("/post", createdPost.getPostId());
//
//        return new ResponseEntity(new SingleResponse<>(mapper.postToPostResponseDto(updatedPost)), HttpStatus.OK);

        return null;
    }

    @GetMapping
    public ResponseEntity getAllCarePost(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("communityId").descending());
        Page<Community> postPage = service.findAllPost(pageable);

        List<CommunityDto.Response> response = postPage
                .stream()
                .map(post->mapper.communityToCommunityResponseDto(post))
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

        return new ResponseEntity(new SingleResponse<>(mapper.communityToCommunityResponseDto(find)), HttpStatus.OK);
    }

    @DeleteMapping("/{post-id}")
    public ResponseEntity PatchPost(@PathVariable("post-id") @Positive long postId) {
        return null;
    }
}
