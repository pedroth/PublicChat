package utils;

import exception.PublicChatRuntimeException;

import java.util.function.Consumer;

public interface ConsumerWithException<T> {

    void accept(T val) throws Exception;

    static <K> Consumer<K> wrap(ConsumerWithException<K> consumer) {
        return x ->  {
          try {
              consumer.accept(x);
          }catch (Exception e) {
              throw new PublicChatRuntimeException(e);
          }
        };
    }
}

