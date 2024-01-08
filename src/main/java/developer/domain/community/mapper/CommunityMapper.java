package developer.domain.community.mapper;

import developer.domain.community.dto.CommunityDto;
import developer.domain.community.entity.Community;
import developer.domain.communityComment.dto.CommunityCommentDto;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommunityMapper {
//    Community communityPostDtoToCommunity(CommunityDto.Post requestBody);
    default Community communityPostDtoToCommunity(CommunityDto.Post requestBody) {
        if ( requestBody == null ) {
            return null;
        }

        String newBody = requestBody.getBody();
        newBody = newBody.replaceAll("<img[^>]*>", "");
        newBody = newBody.replaceAll("<p>","");
        newBody = newBody.replaceAll("</p>","");


        Community.CommunityBuilder community = Community.builder();

        community.title( requestBody.getTitle() );
        community.body( requestBody.getBody() );
        community.category( requestBody.getCategory() );
        community.realBody(newBody);

        return community.build();
    }

    Community communityPatchDtoToCommunity(CommunityDto.Patch requestBody);

    default CommunityDto.CommentResponse communityToCommunityCommentResponseDto(Community community) {
        if ( community == null ) {
            return null;
        }

        List<CommunityCommentDto.Response> comments = null;

        CommunityDto.CommentResponse commentResponse = new CommunityDto.CommentResponse( comments );

        return commentResponse;
    }
    default CommunityDto.Response communityToCommunityResponseDto(Community community, Integer postSize) {
        if ( community == null ) {
            return null;
        }

        String userNickname = null;
        Long memberId = null;
        Long communityId = null;
        String title = null;
        String body = null;
        String category = null;
        Integer commentCount = null;
        Integer view = null;
        String postTime = null;
        LocalDateTime createdAt = null;
        LocalDateTime modifiedAt = null;


        memberId = community.getMember().getMemberId();
        communityId = community.getCommunityId();
        title = community.getTitle();
        body = community.getBody();
        category = community.getCategory();
        view = community.getView();
        commentCount = community.getCommentCount();
        postTime = community.getPostTime();
        createdAt = community.getCreatedAt();
        modifiedAt = community.getModifiedAt();
        userNickname = community.getMember().getUserNickname();

        CommunityDto.Response response = new CommunityDto.Response( userNickname, memberId, communityId, title, body,category, view, commentCount,postTime, createdAt, modifiedAt, postSize );

        return response;
    }



}
