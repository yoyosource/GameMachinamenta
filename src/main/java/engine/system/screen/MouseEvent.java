package engine.system.screen;

import java.awt.*;

public class MouseEvent {

    public final Point point;
    public final Click button;

    public MouseEvent(Point point, int button) {
        this.point = point;
        this.button = Click.getClick(button);
    }

    public enum Click {

        LEFT_MOUSE_BUTTON(0),
        MIDDLE_MOUSE_BUTTON(1),
        RIGHT_MOUSE_BUTTON(2);

        private final int value1;
        static Click[] clicks = new Click[] {LEFT_MOUSE_BUTTON, MIDDLE_MOUSE_BUTTON, RIGHT_MOUSE_BUTTON};

        Click(int value1) {
            this.value1 = value1;
        }

        static Click getClick(int button) {
            return clicks[button];
        }
    }
}
