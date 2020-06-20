package engine.system.loops;

import engine.GameEngine;
import engine.system.screen.RenderEvent;

public class RenderLoop extends Loop {

    public RenderLoop(int target, String name) {
        super(target, name);
    }

    @Override
    public void tick() {
        GameEngine.gameEngine.render();
    }
}
