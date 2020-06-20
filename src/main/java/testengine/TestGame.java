package testengine;

import engine.annotations.game.*;
import engine.system.game.GameEvent;
import engine.system.game.GameExceptionEvent;

@Game(name = "Test")
public class TestGame {

    @GameInit
    public void gameInit(GameEvent event) {

    }

    @GameScreenInitException
    public void gameScreenInitException(GameExceptionEvent event) {
        event.exception.printStackTrace();
    }

    @GameScreenCloseException
    public void gameScreenCloseException(GameExceptionEvent event) {
        event.exception.printStackTrace();
    }

    @GameTickException
    public void gameTickException(GameExceptionEvent event) {
        event.exception.printStackTrace();
    }

    @GameRenderException
    public void gameRenderException(GameExceptionEvent event) {
        event.exception.printStackTrace();
    }

}
