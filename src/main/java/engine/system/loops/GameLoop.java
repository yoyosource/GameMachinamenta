package engine.system.loops;

import engine.GameEngine;
import engine.system.screen.TickEvent;

public class GameLoop extends Loop {

    public GameLoop(int target, String name) {
        super(target, name);
    }

    @Override
    public void tick() {
        GameEngine.gameEngine.tick(new TickEvent(getNsPerTick()));
    }
}
