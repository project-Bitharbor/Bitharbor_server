package developer.domain.member.entity;

import developer.global.audit.Auditable;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String checkPassword;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;


    private String imgURL;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    private String provider;

    @Getter
    @Setter
    private String verificationCode;

    public Member updateMember(String userName, String email) {
        this.userName = userName;
        this.email = email;

        return this;
    }
}