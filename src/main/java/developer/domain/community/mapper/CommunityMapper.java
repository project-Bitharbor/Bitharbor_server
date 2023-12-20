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
    Community communityPostDtoToCommunity(CommunityDto.Post requestBody);

    Community communityPatchDtoToCommunity(CommunityDto.Patch requestBody);

    // CommunityDto.Response communityToCommunityResponseDto(Community community);
    default CommunityDto.Response communityToCommunityResponseDto(Community community) {
        if ( community == null ) {
            return null;
        }

        String userNickname = null;
        Long memberId = null;
        Long communityId = null;
        String title = null;
        String body = null;
        String imgURL = null;
        String category = null;
        List<String> tags = null;
        List<CommunityCommentDto.Response> comments = null;
        Integer commentCount = null;
        Integer view = null;
        String postTime = null;
        LocalDateTime createdAt = null;
        LocalDateTime modifiedAt = null;


        memberId = community.getMember().getMemberId();
        communityId = community.getCommunityId();
        title = community.getTitle();
        body = community.getBody();
        imgURL = community.getImgURL();
        category = community.getCategory();
        List<String> list = community.getTags();
        if ( list != null ) {
            tags = new ArrayList<String>( list );
        }
        view = community.getView();
        commentCount = community.getCommentCount();
        postTime = community.getPostTime();
        createdAt = community.getCreatedAt();
        modifiedAt = community.getModifiedAt();
        userNickname = community.getMember().getUserNickname();

        CommunityDto.Response response = new CommunityDto.Response( userNickname, memberId, communityId, title, body, imgURL,category,tags, view, comments, commentCount,postTime, createdAt, modifiedAt );

        return response;
    }



}
