package developer.domain.qna.mapper;

import developer.domain.qna.dto.QnaDto;
import developer.domain.qna.entity.Qna;
import developer.domain.qnaComment.dto.QnaCommentDto;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QnaMapper {

    default Qna qnaPostDtoToQna(QnaDto.Post requestBody) {
        if ( requestBody == null ) {
            return null;
        }

        String newBody = requestBody.getBody();
        newBody = newBody.replaceAll("<img[^>]*>", "");
        newBody = newBody.replaceAll("<p>","");
        newBody = newBody.replaceAll("</p>","");

        Qna.QnaBuilder qna = Qna.builder();

        qna.title( requestBody.getTitle() );
        qna.body( requestBody.getBody() );
        qna.category( requestBody.getCategory() );
        qna.realBody(newBody);

        return qna.build();
    }

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
        Long memberId = null;
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
        Long profileNum = null;

        String profileImg = qna.getMember().getProfileImg();


        qnaId = qna.getQnaId();
        memberId = qna.getMember().getMemberId();
        profileNum = Long.parseLong(profileImg.substring(profileImg.length()-5,profileImg.length()-4));
        title = qna.getTitle();
        body = qna.getBody();
        category = qna.getCategory();
        view = qna.getView();
        commentCount = qna.getCommentCount();
        postTime = qna.getPostTime();
        createdAt = qna.getCreatedAt();
        modifiedAt = qna.getModifiedAt();
        userNickname = qna.getMember().getUserNickname();

        QnaDto.Response response = new QnaDto.Response( userNickname, memberId, profileNum, qnaId, title, body,category, view, commentCount,postTime, createdAt, modifiedAt, postSize );

        return response;
    }

}
