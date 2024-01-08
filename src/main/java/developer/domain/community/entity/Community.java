package developer.domain.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import developer.domain.communityComment.entity.CommunityComment;
import developer.domain.member.entity.Member;
import developer.global.audit.Auditable;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private String realBody;
    @Column
    private int view;
    @Column
    private String category;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @JsonIgnore
    List<CommunityComment> communityComments = new ArrayList<>();

    @Column
    private int commentCount = communityComments.size();

    @Column
    private String postTime;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

}
