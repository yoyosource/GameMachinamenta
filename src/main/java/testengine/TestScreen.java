package testengine;

import engine.annotations.game.Icon;
import engine.annotations.screen.*;
import engine.system.screen.RenderEvent;
import engine.system.screen.ScreenEvent;
import engine.system.screen.TickEvent;

import java.awt.*;

@Screen(name = "Test", ups = 1, fps = 1)
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
        System.out.println(event.graphics2D);
        event.graphics2D.setColor(Color.BLACK);
        event.graphics2D.drawString("Hello World", 10, 10);
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
