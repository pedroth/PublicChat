package exception;

/**
 * Basic Runtime exception for PublicChat
 */
public final class PublicChatRuntimeException extends RuntimeException {

    public PublicChatRuntimeException() {
    }

    public PublicChatRuntimeException(String s) {
        super(s);
    }

    public PublicChatRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PublicChatRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public PublicChatRuntimeException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
