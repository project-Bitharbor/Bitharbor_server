package developer.domain.knowledge.entity;

import java.util.Random;

public enum RandomThumbnail {
    THUMBNAIL1("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/thumbnail/knowledge1.png"),
    THUMBNAIL2("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/thumbnail/knowledge2.png"),
    THUMBNAIL3("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/thumbnail/knowledge3.png"),
    THUMBNAIL4("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/thumbnail/knowledge4.png"),
    THUMBNAIL5("https://s3.ap-northeast-2.amazonaws.com/bit-harbor.net/thumbnail/knowledge5.png"),
    ;

    private final String thumbmail;

    RandomThumbnail(String thumbmail) {
        this.thumbmail = thumbmail;
    }

    public String getThumbmail() {
        return thumbmail;
    }

    public static RandomThumbnail getRandomThumbnail() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}
