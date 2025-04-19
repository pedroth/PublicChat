package exception;

/**
 * Basic Runtime exception for PublicChat
 */
public final class PublicChatRuntimeException extends RuntimeException {

    public PublicChatRuntimeException(String s) {
        super(s);
    }

    public PublicChatRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
