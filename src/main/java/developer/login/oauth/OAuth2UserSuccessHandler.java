package developer.login.oauth;

import developer.domain.member.entity.Member;
import developer.domain.member.entity.SocialType;
import developer.domain.member.repository.MemberRepository;
import developer.login.jwt.util.CustomAuthorityUtils;
import developer.login.oauth.userInfo.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class OAuth2UserSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtToken jwtToken;
    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils customAuthorityUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = ((OAuth2User) authentication.getPrincipal());

        String type = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId(); // oauth 타입
        String email;
        String nickName;
        String proImg;
        SocialType oauth;

        if ("google".equals(type)) {
            email = String.valueOf(oAuth2User.getAttributes().get("email"));
            nickName = String.valueOf(oAuth2User.getAttributes().get("name"));
            proImg = String.valueOf(oAuth2User.getAttributes().get("picture"));
            oauth = SocialType.GOOGLE;
        }
        else if ("kakao".equals(type))  {
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            nickName = (String) profile.get("nickname");
            proImg = (String) profile.get("profile_image_url");
            oauth = SocialType.KAKAO;
        }
        else {
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Map<String, Object> naverAccount = (Map<String, Object>) attributes.get("response");
            email = (String) naverAccount.get("email");
            Map<String, Object> profile = (Map<String, Object>) naverAccount.get("profile");
            nickName = (String) profile.get("nickname");
            proImg = (String) profile.get("profile_image");
            oauth = SocialType.NAVER;
        }

        Member member;
        Optional<Member> userOptional = memberRepository.findByEmail(email);
        member = userOptional.orElseGet(() -> saveMember(email, oauth, nickName, proImg));

        redirect(request, response, member);
    }
    private Member saveMember (String email, SocialType oauth, String name, String profileImg) {
        Member member = new Member();
        member.setEmail(email);
        member.setSocialType(oauth);
        member.setUserNickname(name);
        member.setProfileImg(profileImg);
        member.setRoles(customAuthorityUtils.createRoles(email));

        return memberRepository.save(member);
    }

    private void redirect (HttpServletRequest request, HttpServletResponse response, Member member) throws IOException{
        String accessToken = jwtToken.delegateAccessToken(member);
        String refreshToken = jwtToken.delegateRefreshToken(member);

        String uri = createURI(accessToken, refreshToken).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }
    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("bit-barbor.vercel.app")
//                .port(80)
//                .path("/receive-token.html")
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}
