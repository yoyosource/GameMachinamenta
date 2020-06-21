package testengine.alpha;

import engine.annotations.screen.*;
import engine.system.screen.RenderEvent;
import engine.system.screen.ScreenEvent;
import engine.system.screen.TickEvent;

import java.awt.*;

@Screen(name = "Hugo", ups = 1, fps = 1)
@Resource(source = "preview.png", name = "test")
public class TestScreenTest {

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
