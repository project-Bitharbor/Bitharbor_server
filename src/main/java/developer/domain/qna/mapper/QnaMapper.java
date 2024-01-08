package developer.domain.qna.mapper;

import developer.domain.qna.dto.QnaDto;
import developer.domain.qna.entity.Qna;
import developer.domain.qnaComment.dto.QnaCommentDto;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QnaMapper {

    Qna qnaPostDtoToQna(QnaDto.Post requestBody);

    Qna qnaPatchDtoToQna(QnaDto.Patch requestBody);

    default QnaDto.CommentResponse qnaToQnaCommentResponseDto(Qna qna) {
        if ( qna == null ) {
            return null;
        }

        List<QnaCommentDto.Response> comments = null;

        QnaDto.CommentResponse commentResponse = new QnaDto.CommentResponse( comments );

        return commentResponse;
    }
    default QnaDto.Response qnaToQnaResponseDto(Qna qna, Integer postSize) {
        if ( qna == null ) {
            return null;
        }

        String userNickname = null;
        Long qnaId = null;
        String title = null;
        String body = null;
        String imgURL = null;
        String category = null;
        List<QnaCommentDto.Response> comments = null;
        Integer commentCount = null;
        Integer view = null;
        String postTime = null;
        LocalDateTime createdAt = null;
        LocalDateTime modifiedAt = null;


        qnaId = qna.getQnaId();
        title = qna.getTitle();
        body = qna.getBody();
        category = qna.getCategory();
        view = qna.getView();
        commentCount = qna.getCommentCount();
        postTime = qna.getPostTime();
        createdAt = qna.getCreatedAt();
        modifiedAt = qna.getModifiedAt();
        userNickname = qna.getMember().getUserNickname();

        QnaDto.Response response = new QnaDto.Response( userNickname, qnaId, title, body,category, view, commentCount,postTime, createdAt, modifiedAt, postSize );

        return response;
    }

}
