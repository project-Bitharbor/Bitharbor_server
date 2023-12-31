package developer.domain.member.controller;

import developer.domain.member.dto.MemberDto;
import developer.domain.member.entity.Member;
import developer.domain.member.entity.MemberProfile;
import developer.domain.member.mapper.MemberMapper;
import developer.domain.member.repository.MemberRepository;
import developer.domain.member.service.MemberService;
import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import developer.global.response.SingleResponse;
import developer.global.utils.URICreator;
import developer.login.oauth.userInfo.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/members")
@Validated
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper mapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtToken jwtToken;


    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberDto.Post requestBody) {

        requestBody.setProfileImg(MemberProfile.getMemberProfile(requestBody.getProfileNum()).getProfile());
        Member member = mapper.memberPostDtoToMember(requestBody);

        if (memberRepository.existsByEmail(requestBody.getEmail())) {
            String errorMessage = "이메일 중복! 다른 이메일을 사용해주세요!";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }

        if(!requestBody.getPassword().equals(requestBody.getCheckPassword()) ) {
            String errorMessage = "비밀번호가 다릅니다. 비밀번호를 확인해주세요!";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }

        Member createdMember = memberService.createMember(member);

        URI uri = URICreator.createUri("/members", createdMember.getMemberId());

        return ResponseEntity.created(uri).build();
    }


    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@Valid @RequestBody MemberDto.Patch requestBody,
                                      @PathVariable("member-id") Long memberId,
                                      @RequestHeader("Authorization") String authorization) {

        Member loginMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        if(loginMember.getMemberId() != memberId) {
            String errorMessage = "권한이 없습니다! 확인하여주세요!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }

        Member member = mapper.memberPatchDtoToMember(requestBody);
        member.setMemberId(memberId);

        if (!passwordEncoder.matches(member.getCurrentPassword(), memberRepository.findByMemberId(memberId).getPassword())) {
            String errorMessage = "기존 비밀번호가 다릅니다. 비밀번호를 확인해주세요!";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }

        if(!requestBody.getPassword().equals(requestBody.getCheckPassword()) ) {
            String errorMessage = "확인 비밀번호가 다릅니다. 비밀번호를 확인해주세요!";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }

        Member findedMember = memberService.updateMember(member);
        MemberDto.Response response = mapper.memberToMemberResponseDto(findedMember);

        return new ResponseEntity<>(
                new SingleResponse<>(response),
                HttpStatus.OK);
    }

    @GetMapping("/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") Long memberId,
                                    @RequestHeader("Authorization") String authorization) {

        Member loginMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        if(loginMember.getMemberId() != memberId) {
            String errorMessage = "권한이 없습니다! 확인하여주세요!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }

        Member member = memberService.findMember(memberId);

        MemberDto.Response response= mapper.memberToMemberResponseDto(member);
        return new ResponseEntity<>(
                new SingleResponse<>(response),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getMembers() {
        List<Member> members = memberService.findMembers();
        return new ResponseEntity<>(
                new SingleResponse<>(mapper.membersToMemberReponseDtos(members)),
                HttpStatus.OK);
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMembers(@PathVariable("member-id") Long memberId,
                                        @RequestHeader("Authorization") String authorization) {
        Member loginMember = memberService.findMember(jwtToken.extractUserIdFromToken(authorization));

        if(loginMember.getMemberId() != memberId) {
            String errorMessage = "권한이 없습니다! 확인하여주세요!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }
        memberService.deleteMember(memberId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/findingPW")
    public ResponseEntity<Void> sendingVerifyCode(@RequestParam("email") String email) {
        try {
            // 인증번호 발송
            memberService.sendVerificationCode(email);

            // 인증번호 발송 성공 시 비밀번호 재설정 페이지로 이동
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // 인증번호 발송 실패 시 에러 응답
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verify-code")
    public String checkingVerifyCode(@RequestParam("email") String email,
                                     @RequestParam("verificationCode") String verificationCode) {
        try {
            // 인증번호 확인
            memberService.verifyCode(email, verificationCode);

            return "password-reset"; // 인증번호 검증 성공 시 비밀번호 재설정 페이지로 이동
        } catch (IllegalArgumentException e) {
            // 인증번호 검증 실패 시 에러 메시지와 함께 인증번호 확인 페이지로 이동
            return "redirect:/verification-code?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
