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

//    Knowledge knowledgePostDtoToKnowledge(KnowledgeDto.Post requestBody);
    default Knowledge knowledgePostDtoToKnowledge(KnowledgeDto.Post requestBody) {
        if ( requestBody == null ) {
            return null;
        }

        String newBody = requestBody.getBody();
        newBody = newBody.replaceAll("<img[^>]*>", "");
        newBody = newBody.replaceAll("<p>","");
        newBody = newBody.replaceAll("</p>","");

        Knowledge.KnowledgeBuilder knowledge = Knowledge.builder();

        knowledge.title( requestBody.getTitle() );
        knowledge.body( requestBody.getBody() );
        knowledge.imgURL( requestBody.getImgURL() );
        knowledge.category( requestBody.getCategory() );
        knowledge.realBody(newBody);

        return knowledge.build();
    }

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
        Long memberId = null;
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


        memberId = knowledge.getMember().getMemberId();
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

        KnowledgeDto.Response response = new KnowledgeDto.Response( userNickname, memberId, knowledgeId, title, body, imgURL,category, view, commentCount,postTime, createdAt, modifiedAt, postSize );

        return response;
    }

}
