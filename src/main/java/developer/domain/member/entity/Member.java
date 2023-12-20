package developer.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import developer.domain.community.entity.Community;
import developer.domain.knowledge.entity.Knowledge;
import developer.domain.qna.entity.Qna;
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

    //Todo: 비밀번호 변경 관련 코딩하기!!
    @Column(nullable = false)
    private String password;

    @Column
    private String checkPassword;
    @Column
    private String currentPassword;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String userNickname;

    @Column(nullable = false)
    private String phoneNumber;


    private String imgURL;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Community> communities = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Knowledge> knowledges = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Qna> qnas = new ArrayList<>();

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