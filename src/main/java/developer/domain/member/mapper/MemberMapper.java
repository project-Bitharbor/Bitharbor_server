package developer.domain.member.mapper;

import developer.domain.member.dto.MemberDto;
import developer.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MemberMapper {
    Member memberPostDtoToMember(MemberDto.Post requestBody);

    default Member memberOauthPostDtoToMember(MemberDto.OauthPost requestBody) {
        if ( requestBody == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.email( requestBody.getEmail() );
        member.password("1234");
        member.checkPassword("1234");
        member.userNickname(requestBody.getName());
        member.userName(requestBody.getName());
        member.profileImg(requestBody.getPicture());
        member.bigProfileImg(requestBody.getPicture());
        member.phoneNumber("010-0000-0000");


        return member.build();
    }

    Member memberPatchDtoToMember(MemberDto.Patch requestBody);
    Member memberPatchPWDtoToMember(MemberDto.PatchPW requestBody);

    MemberDto.Response memberToMemberResponseDto(Member member);

    List<Member> membersToMemberReponseDtos(List<Member> members);
}
