package developer.domain.communityComment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CommunityCommentDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Post{
        @NotBlank(message="content is not null")
        private String content;

/*        @NotBlank(message = "userId not null, JWT 구현이후 token 으로 바꿀 예정입니다.")
        private long userId;*/

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
        private long communityId;
        private LocalDateTime createdAt;
        private String nickName;
        private Long memberId;
        private String profileImgUrl;
        private String postTime;
        Response(){}

    }
}
