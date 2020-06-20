package engine.system.screen;

import java.awt.*;

public class RenderEvent {

    public final Graphics2D graphics2D;

    public RenderEvent(Graphics2D g) {
        this.graphics2D = g;
    }

}
