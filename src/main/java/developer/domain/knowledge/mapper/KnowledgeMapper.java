package developer.domain.knowledge.mapper;

import developer.domain.knowledge.dto.KnowledgeDto;
import developer.domain.knowledge.entity.Knowledge;
import developer.domain.knowledgeComment.dto.KnowledgeCommentDto;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface KnowledgeMapper {

    Knowledge knowledgePostDtoToKnowledge(KnowledgeDto.Post requestBody);

    Knowledge knowledgePatchDtoToKnowledge(KnowledgeDto.Patch requestBody);

    default KnowledgeDto.CommentResponse knowledgeToKnowledgeCommentResponseDto(Knowledge knowledge) {
        if ( knowledge == null ) {
            return null;
        }

        List<KnowledgeCommentDto.Response> comments = null;

        KnowledgeDto.CommentResponse commentResponse = new KnowledgeDto.CommentResponse( comments );

        return commentResponse;
    }
    default KnowledgeDto.Response knowledgeToKnowledgeResponseDto(Knowledge knowledge, Integer postSize) {
        if ( knowledge == null ) {
            return null;
        }

        String userNickname = null;
        Long knowledgeId = null;
        String title = null;
        String body = null;
        String imgURL = null;
        String category = null;
        Integer commentCount = null;
        Integer view = null;
        String postTime = null;
        LocalDateTime createdAt = null;
        LocalDateTime modifiedAt = null;


        knowledgeId = knowledge.getKnowledgeId();
        title = knowledge.getTitle();
        body = knowledge.getBody();
        imgURL = knowledge.getImgURL();
        category = knowledge.getCategory();
        view = knowledge.getView();
        commentCount = knowledge.getCommentCount();
        postTime = knowledge.getPostTime();
        createdAt = knowledge.getCreatedAt();
        modifiedAt = knowledge.getModifiedAt();
        userNickname = knowledge.getMember().getUserNickname();

        KnowledgeDto.Response response = new KnowledgeDto.Response( userNickname, knowledgeId, title, body, imgURL,category, view, commentCount,postTime, createdAt, modifiedAt, postSize );

        return response;
    }

}
