package developer.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class MemberDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String checkPassword;
        @NotBlank
        private String userName;
        @NotBlank
        private String userNickname;
        @NotBlank
        private String phoneNumber;
        private String profileImg;
        private Integer profileNum;
    }

    @Getter
    @NoArgsConstructor
    public static class Patch {
        private long memberId;
        private String currentPassword;
        private String password;
        private String checkPassword;
        @NotBlank
        private String userName;
        @NotBlank
        private String userNickname;
        @NotBlank
        private String phoneNumber;

        public Patch addMemberId(Long memberId) {
            Assert.notNull(memberId, "member id must not be null.");
            this.memberId = memberId;

            return this;
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Response {
        private long memberId;
        private String email;
        private String userName;
        private String userNickname;
        private String phoneNumber;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String profileImg;

    }
}
