package developer.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class MemberDto {
    @Getter
    @NoArgsConstructor
    public static class Post {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자에서 16자 사이어야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "특수문자(@$!%*?&)를 최소 1개 포함해야 합니다.")
        private String password;
        @NotBlank
        @Size(min = 8, max = 16, message = "비밀번호는 8자에서 16자 사이어야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "특수문자(@$!%*?&)를 최소 1개 포함해야 합니다.")
        private String checkPassword;
        @NotBlank
        private String userName;
        @NotBlank
        private String userNickname;
        @NotBlank
        private String phoneNumber;
    }

    @Getter
    @NoArgsConstructor
    public static class Patch {
        private long memberId;
        @Size(min = 8, max = 16, message = "비밀번호는 8자에서 16자 사이어야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "특수문자(@$!%*?&)를 최소 1개 포함해야 합니다.")
        private String currentPassword;
        @Size(min = 8, max = 16, message = "비밀번호는 8자에서 16자 사이어야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "특수문자(@$!%*?&)를 최소 1개 포함해야 합니다.")
        private String password;
        @Size(min = 8, max = 16, message = "비밀번호는 8자에서 16자 사이어야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "특수문자(@$!%*?&)를 최소 1개 포함해야 합니다.")
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

    }
}
