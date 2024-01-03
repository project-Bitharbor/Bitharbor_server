package developer.domain.knowledgeComment.mapper;

import developer.domain.knowledgeComment.dto.KnowledgeCommentDto;
import developer.domain.knowledgeComment.entity.KnowledgeComment;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface KnowledgeCommentMapper {

    KnowledgeComment commentPostDtoToComment(KnowledgeCommentDto.Post postDto);
    KnowledgeComment commentPatchDtoToComment(KnowledgeCommentDto.Patch patchDto);

    default KnowledgeCommentDto.Response commentToCommentResponseDto(KnowledgeComment comment) {
        if ( comment == null ) {
            return null;
        }

        KnowledgeCommentDto.Response.ResponseBuilder response = KnowledgeCommentDto.Response.builder();

        response.commentId( comment.getCommentId() );
        response.content( comment.getContent() );
        response.knowledgeId( comment.getKnowledge().getKnowledgeId() );
        response.createdAt( comment.getCreatedAt() );
        response.nickName( comment.getMember().getUserNickname() );
        response.memberId(comment.getMember().getMemberId());
        response.profileImgUrl( comment.getMember().getProfileImg() );
        response.postTime(calculateTimeDifference(comment.getCreatedAt()));

        return response.build();
    }

    default List<KnowledgeCommentDto.Response> commentListToCommentResponseListDto(List<KnowledgeComment> commentList){

        if(commentList == null)
            return null;

        List<KnowledgeCommentDto.Response> response =  commentList
                .stream()
                .map(comment->commentToCommentResponseDto(comment))
                .collect(Collectors.toList());

        return response;
    }

    default List<KnowledgeCommentDto.Response> commentPageToCommentResponseListDto(Page<KnowledgeComment> commentPage){

        if(commentPage == null)
            return null;

        List<KnowledgeCommentDto.Response> response =                 commentPage
                .stream()
                .map(comment->commentToCommentResponseDto(comment))
                .collect(Collectors.toList());

        return response;
    }

    default String calculateTimeDifference(LocalDateTime createdAt) {
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
