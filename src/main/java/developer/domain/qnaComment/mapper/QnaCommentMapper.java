package developer.domain.qnaComment.mapper;

import developer.domain.qnaComment.dto.QnaCommentDto;
import developer.domain.qnaComment.entity.QnaComment;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QnaCommentMapper {

    QnaComment commentPostDtoToComment(QnaCommentDto.Post postDto);
    QnaComment commentPatchDtoToComment(QnaCommentDto.Patch patchDto);

    default QnaCommentDto.Response commentToCommentResponseDto(QnaComment comment) {
        if ( comment == null ) {
            return null;
        }
        String profileImg = comment.getMember().getProfileImg();

        QnaCommentDto.Response.ResponseBuilder response = QnaCommentDto.Response.builder();

        response.commentId( comment.getCommentId() );
        response.content( comment.getContent() );
        response.qnaId( comment.getQna().getQnaId() );
        response.createdAt( comment.getCreatedAt() );
        response.nickName( comment.getMember().getUserNickname() );
        response.memberId( comment.getMember().getMemberId());
        response.profileNum(Long.parseLong(profileImg.substring(profileImg.length()-5,profileImg.length()-4)));
        response.profileImgUrl( comment.getMember().getProfileImg() );
        response.postTime(calculateTimeDifference(comment.getCreatedAt()));

        return response.build();
    }

    default List<QnaCommentDto.Response> commentListToCommentResponseListDto(List<QnaComment> commentList){

        if(commentList == null)
            return null;

        List<QnaCommentDto.Response> response =  commentList
                .stream()
                .map(comment->commentToCommentResponseDto(comment))
                .collect(Collectors.toList());

        return response;
    }

    default List<QnaCommentDto.Response> commentPageToCommentResponseListDto(Page<QnaComment> commentPage){

        if(commentPage == null)
            return null;

        List<QnaCommentDto.Response> response = commentPage
                .stream()
                .map(comment->commentToCommentResponseDto(comment))
                .collect(Collectors.toList());

        return response;
    }

    // 시간별 게시물 작성시간 표기 메서드(작성시간 기준)
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
