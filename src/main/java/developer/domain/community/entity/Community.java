package developer.domain.community.entity;

import developer.domain.member.entity.Member;
import developer.global.audit.Auditable;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Getter @Builder @Setter
@NoArgsConstructor @AllArgsConstructor
public class Community extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String body;
    @Column(nullable = false)
    private String imgURL;
    @Column
    private Long view;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

}
