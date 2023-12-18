package developer.domain.qnaComment.mapper;

import developer.domain.qnaComment.dto.KnowledgeCommentDto;
import developer.domain.qnaComment.entity.KnowledgeComment;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

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
        response.profileImgUrl( comment.getMember().getImgURL() );

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

}
