package engine.system.game;

public class GameExceptionEvent {

    public final Exception exception;

    public GameExceptionEvent(Exception exception) {
        this.exception = exception;
    }

}
