package developer.domain.member.entity;

public enum MemberProfile {

    PROFILE1("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/profile/user_icon1.png"),
    PROFILE2("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/profile/user_icon2.png"),
    PROFILE3("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/profile/user_icon3.png"),
    PROFILE4("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/profile/user_icon4.png"),
    PROFILE5("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/profile/user_icon5.png"),
    PROFILE6("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/profile/user_icon6.png"),
    PROFILE7("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/profile/user_icon7.png");

    private final String profile;

    MemberProfile(String profile) {
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }

    public static MemberProfile getMemberProfile(int num) {
        return values()[num-1];
    }
}
