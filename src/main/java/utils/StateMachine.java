package utils;


public class StateMachine<I> {
    private State<I> state;

    public StateMachine(State<I> initialState) {
        this.state = initialState;
    }

    public void next(I x) {
        state = state.next(x);
    }
}
