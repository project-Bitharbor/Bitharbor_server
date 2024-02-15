package developer.domain.qna.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import developer.domain.member.entity.Member;
import developer.domain.qnaComment.entity.QnaComment;
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
public class Qna extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qnaId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;
    @Column(columnDefinition = "LONGTEXT")
    private String realBody;
    @Column
    private int view;
    @Column
    private String category;
    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL)
    @JsonIgnore
    List<QnaComment> qnaComments = new ArrayList<>();

    @Column
    private int commentCount = qnaComments.size();

    @Column
    private String postTime;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


}
