package developer.domain.qnaComment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class QnaCommentDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Post{
        @NotBlank(message="content is not null")
        private String content;


        Post() {}


    }
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Patch{


        @NotBlank(message="content not null")
        private String content;


        Patch(){}

    }
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private long commentId;
        private String content;
        private long qnaId;
        private LocalDateTime createdAt;
        private String nickName;
        private Long memberId;
        private String profileImgUrl;
        private String postTime;
        Response(){}

    }
}
