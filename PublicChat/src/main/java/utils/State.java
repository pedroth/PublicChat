package utils;

@FunctionalInterface
public interface State<I> {
    State<I> next(I x);
}
