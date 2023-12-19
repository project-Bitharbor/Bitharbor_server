package developer.domain.qna.mapper;

import developer.domain.qna.dto.QnaDto;
import developer.domain.qna.entity.Qna;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QnaMapper {

    Qna qnaPostDtoToQna(QnaDto.Post requestBody);

    Qna qnaPatchDtoToQna(QnaDto.Patch requestBody);

    // CommunityDto.Response communityToCommunityResponseDto(Community community);
    default QnaDto.Response qnaToQnaResponseDto(developer.domain.qna.entity.Qna qna) {
        if ( qna == null ) {
            return null;
        }

        String userNickname = null;
        Long qnaId = null;
        String title = null;
        String body = null;
        String imgURL = null;
        String category = null;
        List<String> tags = null;
        Integer commentCount = null;
        Integer view = null;
        String postTime = null;
        LocalDateTime createdAt = null;
        LocalDateTime modifiedAt = null;


        qnaId = qna.getQnaId();
        title = qna.getTitle();
        body = qna.getBody();
        imgURL = qna.getImgURL();
        category = qna.getCategory();
        List<String> list = qna.getTags();
        if ( list != null ) {
            tags = new ArrayList<String>( list );
        }
        view = qna.getView();
        commentCount = qna.getCommentCount();
        postTime = qna.getPostTime();
        createdAt = qna.getCreatedAt();
        modifiedAt = qna.getModifiedAt();
        userNickname = qna.getMember().getUserNickname();

        QnaDto.Response response = new QnaDto.Response( userNickname, qnaId, title, body, imgURL,category,tags, view, commentCount,postTime, createdAt, modifiedAt );

        return response;
    }

}
