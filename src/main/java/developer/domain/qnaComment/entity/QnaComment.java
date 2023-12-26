package developer.domain.qnaComment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import developer.domain.member.entity.Member;
import developer.global.audit.Auditable;
import developer.domain.qna.entity.Qna;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class QnaComment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;
    @Column(nullable=false)
    private String content;

    @ManyToOne
    @JoinColumn(name="qnaId")
    @JsonIgnore
    private Qna qna;


    @ManyToOne
    @JoinColumn(name = "memberId")
    @JsonIgnore
    private Member member;

    private String postTime;

    @Builder
    public QnaComment(long commentId, String content, Qna qna, Member member, String postTime) {
        this.commentId = commentId;
        this.content = content;
        this.qna = qna;
        this.member = member;
        this.postTime = postTime;
    }

    public long getCommentId() {
        return commentId;
    }
}
