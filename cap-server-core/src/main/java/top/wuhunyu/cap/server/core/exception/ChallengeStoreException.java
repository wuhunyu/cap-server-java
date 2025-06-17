package top.wuhunyu.cap.server.core.exception;

/**
 * 挑战存储异常
 *
 * @author wuhunyu
 * @date 2025/06/16 17:09
 **/

public class ChallengeStoreException extends RuntimeException {

    private String message;

    public ChallengeStoreException(final String message) {
        super(message);
        this.message = message;
    }

    public ChallengeStoreException(final Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public ChallengeStoreException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
