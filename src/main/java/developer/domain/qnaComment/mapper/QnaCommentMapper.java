package developer.domain.qnaComment.mapper;

import developer.domain.qnaComment.dto.QnaCommentDto;
import developer.domain.qnaComment.entity.QnaComment;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

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

        QnaCommentDto.Response.ResponseBuilder response = QnaCommentDto.Response.builder();

        response.commentId( comment.getCommentId() );
        response.content( comment.getContent() );
        response.qnaId( comment.getQna().getQnaId() );
        response.createdAt( comment.getCreatedAt() );
        response.nickName( comment.getMember().getUserNickname() );
        response.memberId( comment.getMember().getMemberId());
        response.profileImgUrl( comment.getMember().getImgURL() );

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

}
