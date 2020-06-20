package testengine;

import engine.annotations.screen.*;
import engine.system.screen.RenderEvent;
import engine.system.screen.ScreenEvent;
import engine.system.screen.TickEvent;

@Screen(name = "Test")
@LaunchScreen
public class TestScreen {

    @ScreenInit
    public void screenInit(ScreenEvent event) {

    }

    @ScreenClose
    public void screenClose(ScreenEvent event) {

    }

    @RenderPre
    public void renderPre(RenderEvent event) {

    }

    @Render
    public void render(RenderEvent event) {

    }

    @RenderPost
    public void renderPost(RenderEvent event) {

    }

    @TickPre
    public void tickPre(TickEvent event) {

    }

    @Tick
    public void tick(TickEvent event) {

    }

    @TickPost
    public void tickPost(TickEvent event) {

    }

}
