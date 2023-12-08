package developer.domain.knowledgeComment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import developer.domain.community.entity.Community;
import developer.domain.knowledge.entity.Knowledge;
import developer.domain.member.entity.Member;
import developer.global.audit.Auditable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class KnowledgeComment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;
    @Column(nullable=false)
    private String content;

    @ManyToOne
    @JoinColumn(name="knowledgeId")
    @JsonIgnore
    private Knowledge knowledge;


    @ManyToOne
    @JoinColumn(name = "memberId")
    @JsonIgnore
    private Member member;


    @Builder
    public KnowledgeComment(long commentId, String content, Knowledge knowledge, Member member) {
        this.commentId = commentId;
        this.content = content;
        this.knowledge = knowledge;
        this.member = member;
    }

    public long getCommentId() {
        return commentId;
    }
}
