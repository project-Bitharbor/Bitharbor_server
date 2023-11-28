package developer.domain.community.mapper;

import developer.domain.community.dto.CommunityDto;
import developer.domain.community.entity.Community;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommunityMapper {
    Community communityPostDtoToCommunity(CommunityDto.Post requestBody);

//    CommunityDto.Response communityToCommunityResponseDto(Community community);
    default CommunityDto.Response communityToCommunityResponseDto(Community community) {
        if ( community == null ) {
            return null;
        }

        String userNickname = null;
        Long communityId = null;
        String title = null;
        String body = null;
        String imgURL = null;
        LocalDateTime createdAt = null;
        LocalDateTime modifiedAt = null;


        communityId = community.getCommunityId();
        title = community.getTitle();
        body = community.getBody();
        imgURL = community.getImgURL();
        createdAt = community.getCreatedAt();
        modifiedAt = community.getModifiedAt();
        userNickname = community.getMember().getNickname();

        CommunityDto.Response response = new CommunityDto.Response( userNickname, communityId, title, body, imgURL, createdAt, modifiedAt );

        return response;
    }



}
