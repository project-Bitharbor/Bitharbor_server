package developer.domain.communityComment.mapper;

import developer.domain.communityComment.dto.CommunityCommentDto;
import developer.domain.communityComment.entity.CommunityComment;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommunityCommentMapper {

    CommunityComment commentPostDtoToComment(CommunityCommentDto.Post postDto);
    CommunityComment commentPatchDtoToComment(CommunityCommentDto.Patch patchDto);

    default CommunityCommentDto.Response commentToCommentResponseDto(CommunityComment comment) {
        if ( comment == null ) {
            return null;
        }

        CommunityCommentDto.Response.ResponseBuilder response = CommunityCommentDto.Response.builder();

        response.commentId( comment.getCommentId() );
        response.content( comment.getContent() );
        response.communityId( comment.getCommunityId() );
        response.createdAt( comment.getCreatedAt() );
        response.nickName( comment.getMember().getUserNickname() );
        response.memberId(comment.getMember().getMemberId());
        response.profileImgUrl( comment.getMember().getProfileImg() );
        response.postTime(calculateTimeDifference(comment.getCreatedAt()));

        return response.build();
    }

    default List<CommunityCommentDto.Response> commentListToCommentResponseListDto(List<CommunityComment> commentList){

        if(commentList == null)
            return null;

        List<CommunityCommentDto.Response> response =  commentList
                .stream()
                .map(comment->commentToCommentResponseDto(comment))
                .collect(Collectors.toList());

        return response;
    }

    default List<CommunityCommentDto.Response> commentPageToCommentResponseListDto(Page<CommunityComment> commentPage){

        if(commentPage == null)
            return null;

        List<CommunityCommentDto.Response> response =                 commentPage
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
