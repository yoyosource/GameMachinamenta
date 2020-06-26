package testengine.alpha;

import engine.annotations.screen.*;
import engine.system.screen.MouseEvent;
import engine.system.screen.RenderEvent;
import engine.system.screen.ScreenEvent;
import engine.system.screen.TickEvent;

import java.awt.*;

@Screen(name = "Test", ups = 30, fps = 1)
@LaunchScreen
@Resource(source = "preview.png", name = "test")
@ResourceLoader(name = "Hugo")
public class TestScreen {

    boolean test = false;

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
        if(!test) event.graphics2D.setColor(Color.BLACK);
        else event.graphics2D.setColor(Color.RED);
        event.graphics2D.drawString("Hello World", 10, 10);
        test = false;
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

    @MouseClick
    public void mouseClick(MouseEvent event) {
        if(event.button == MouseEvent.Click.LEFT_MOUSE_BUTTON) {
            test = true;
            System.out.println("Funzt!");
        }
    }
}
