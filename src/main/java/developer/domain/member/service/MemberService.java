package developer.domain.member.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import developer.domain.member.dto.MemberDto;
import developer.domain.member.entity.Member;
import developer.domain.member.repository.MemberRepository;
import developer.global.exception.BusinessLogicException;
import developer.global.exception.ExceptionCode;
import developer.login.jwt.util.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private Map<String, String> tempStorage = new HashMap<>();

    private final MemberRepository repository;

    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;
    private final JavaMailSender javaMailSender;

//    private final String ACCOUNT_SID = "TWILO_ACCOUNT";
//    private final String AUTH_TOKEN = "TWILO_AUTH";
//    private final String TWILIO_PHONE_NUMBER = "TWILO_PHONE";

    public Member createMember(Member member) {

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);
        member.setCheckPassword(encryptedPassword);

        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        return repository.save(member);
    }

    public Member updateMember(Member member) {

        Member findMember = repository.findByMemberId(member.getMemberId());

        Optional.ofNullable(member.getUserName())
                .ifPresent(findMember::setUserName);
        Optional.ofNullable(member.getPassword())
                .ifPresent(findMember::setPassword);
        Optional.ofNullable(member.getCheckPassword())
                .ifPresent(findMember::setCheckPassword);
        Optional.ofNullable(member.getUserNickname())
                .ifPresent(findMember::setUserNickname);
        Optional.ofNullable(member.getPhoneNumber())
                .ifPresent(findMember::setPhoneNumber);
        Optional.ofNullable(member.getProfileImg())
                .ifPresent(findMember::setProfileImg);
        Optional.ofNullable(member.getBigProfileImg())
                .ifPresent(findMember::setBigProfileImg);

        return repository.save(findMember);
    }

    public Member findMember(long memberId) {

        Member findMember = repository.findByMemberId(memberId);

        return findMember;

    }

    public List<Member> findMembers() {
        return repository.findAll();
    }

    public void deleteMember(long memberId) {

        repository.deleteById(memberId);
    }

    public Member verifiedMember(long memberId) {
        Optional<Member> optional = repository.findById(memberId);
        Member findId = optional.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return findId;
    }

    public void sendIdVerificationCode(String email, String phoneNumber) {

        // 인증번호 생성 및 저장
        String verificationCode = generateVerificationCode();

        tempStorage.put(email,verificationCode);
        tempStorage.put("phoneNumber",phoneNumber);

        // 이메일 발송
        String appUrl = getAppUrl(); // 애플리케이션 URL 가져오기
        String message = "인증번호: " + verificationCode + "\n" + appUrl;
        sendEmail(email, "인증번호 발송", message);
    }

    // 인증번호 확인 로직
    public Member verifyIdCode(String email, String verificationCode) {

        String storedCode = tempStorage.get(email);
        Member member = repository.findByPhoneNumber(tempStorage.get("phoneNumber"));
        if (storedCode != null && storedCode.equals(verificationCode)) {
            // 인증번호 일치
            tempStorage.remove(email); // 인증번호 사용 후 삭제
            tempStorage.remove("phoneNumber");
            tempStorage = new HashMap<>();
            return member;
        } else {
            // 인증번호 불일치
            return null;
        }
    }

    public void sendPWVerificationCode(String email) {

        // 사용자 정보 가져오기
        Member member = repository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("입력한 이메일 주소가 존재하지 않습니다."));

        // 인증번호 생성 및 저장
        String verificationCode = generateVerificationCode();
        member.setVerificationCode(verificationCode);
        repository.save(member);

        // 이메일 발송
        String appUrl = getAppUrl(); // 애플리케이션 URL 가져오기
        String message = "인증번호: " + verificationCode + "\n" + appUrl ;
        sendEmail(email, "인증번호 발송", message);
    }
    public void verifyPWCode(String email, String verificationCode) {
        // 사용자 정보 가져오기
        Member member = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("입력한 이메일 주소가 존재하지 않습니다."));

        // 인증번호 확인
        String savedVerificationCode = member.getVerificationCode();
        if (savedVerificationCode == null || !savedVerificationCode.equals(verificationCode)) {
            throw new IllegalArgumentException("잘못된 인증번호입니다.");
        }

        // 인증번호 검증 완료
        member.setVerificationCode(null);
        repository.save(member);
    }

    private void sendEmail(String email, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("ksr940818@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }

    private String generateVerificationCode() {
        // 랜덤 숫자 문자열 생성
        return RandomStringUtils.randomNumeric(6);
    }

    private String getAppUrl() {
        // 애플리케이션 URL 반환
        return "https://bit-harbor.vercel.app";
    }

//    public void phoneVerificationCode(String phoneNumber) {
//
//        Member member = repository.findByPhoneNumber(phoneNumber)
//                .orElseThrow(() -> new IllegalArgumentException("입력한 이메일 주소가 존재하지 않습니다."));
//
//            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//
//            String verificationCode = generateVerificationCode();
//
//            // 인증 코드를 저장
//            member.setVerificationCode(verificationCode);
//
//            String formattedPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");  // 숫자만 남기기
//            formattedPhoneNumber = "+82" + formattedPhoneNumber.substring(1);  // 국가 코드 추가
//
//            // Twilio를 사용하여 SMS 발송
//            Message message = Message.creator(
////                    new PhoneNumber(formattedPhoneNumber),
//                    new PhoneNumber(PhoneNumber),
//                    new PhoneNumber(TWILIO_PHONE_NUMBER),
//                    "Your verification code is: " + verificationCode
//            ).create();
//
//        System.out.println(message.getSid());
//    }
//}

//    public long getLoginMemberId() {
//        String loginEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        long memberId = memberRepository.findByEmail(loginEmail).get().getMemberId();
//        return memberId;
//    }
}