package developer.domain.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import developer.domain.communityComment.entity.CommunityComment;
import developer.domain.knowledgeComment.entity.KnowledgeComment;
import developer.domain.member.entity.Member;
import developer.global.audit.Auditable;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Knowledge extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long knowledgeId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String body;
    @Column
    private String imgURL;
    @Column
    private int view;
    @Column
    private String category;
    @ElementCollection
    private List<String> tags;
    @OneToMany(mappedBy = "knowledge", cascade = CascadeType.ALL)
    @JsonIgnore
    List<KnowledgeComment> knowledgeComments = new ArrayList<>();

    @Column
    private int commentCount = knowledgeComments.size();

    @Column
    private String postTime;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


}
