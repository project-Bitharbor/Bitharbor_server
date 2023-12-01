package developer.domain.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import developer.domain.communityComment.entity.Comment;
import developer.domain.member.entity.Member;
import developer.global.audit.Auditable;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    @Column(nullable = false)
    private String imgURL;
    @Column
    private int view;
    @Column
    private String category;
    @ElementCollection
    private List<String> tags;
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @JsonIgnore
    List<Comment> comments = new ArrayList<>();

    @Column
    private int commentCount = comments.size();

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

}
