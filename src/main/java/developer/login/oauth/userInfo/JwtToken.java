package developer.login.oauth.userInfo;


import developer.domain.member.entity.Member;
import developer.login.jwt.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class JwtToken {
    private final JwtService jwtService;


    public String delegateAccessToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", member.getEmail());
        claims.put("roles", member.getRoles());
        claims.put("memberId", member.getMemberId());


        String subject = member.getEmail();
        Date expiration = jwtService.getTokenExpiration(jwtService.getRefreshTokenExpirationPeriod());

        String base64EncodedSecretKey = jwtService.encodeBase64SecretKey(jwtService.getSecretKey());

        String accessToken = jwtService.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return "Bearer " + accessToken;
    }

    public String delegateRefreshToken(Member member) {
        String subject = member.getEmail();
        Date expiration = jwtService.getTokenExpiration(jwtService.getRefreshTokenExpirationPeriod());
        String base64EncodedSecretKey = jwtService.encodeBase64SecretKey(jwtService.getSecretKey());

        String refreshToken = jwtService.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        return "Bearer " + refreshToken;
    }

    public Long extractUserIdFromToken(String requestToken){

        if(!StringUtils.isEmpty(requestToken)) {

            Jws<Claims> claims =
                    jwtService.getClaims(requestToken, jwtService.encodeBase64SecretKey(jwtService.getSecretKey()));

            if(Optional.ofNullable(claims.getBody().get("memberId")).isPresent()){
                return Long.parseLong(String.valueOf(claims.getBody().get("memberId")));
            }

        }
        return null;
    }

    public String  extractUserEmailFromToken(String requestToken){

        if(!StringUtils.isEmpty(requestToken)) {

            Jws<Claims> claims =
                    jwtService.getClaims(requestToken, jwtService.encodeBase64SecretKey(jwtService.getSecretKey()));

            if(Optional.ofNullable(claims.getBody().get("sub")).isPresent()){
                return String.valueOf(claims.getBody().get("sub"));
            }

        }
        return null;
    }

    public boolean verifyTokenExpiration(String requestToken) {

        if (!StringUtils.isEmpty(requestToken)) {
            Jws<Claims> claims = jwtService.getClaims(requestToken, jwtService.encodeBase64SecretKey(jwtService.getSecretKey()));
            Date expiration = null;
            //System.out.println("claims: "+claims.getBody());
            if(Optional.ofNullable(claims.getBody().getExpiration()).isPresent()) {
                expiration = claims.getBody().getExpiration();
                Date now = new Date();

                return now.before(expiration);
                // 현재 시간이 토큰의 만료 시간 이전인지 확인
            }
        }

        return false;
    }


}
