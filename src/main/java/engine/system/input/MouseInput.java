package engine.system.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInput extends MouseAdapter {
    public final static int KEYNUMBER = 3;

    public final static boolean[] keys = new boolean[KEYNUMBER];
    public final static boolean[] lastkeys = new boolean[KEYNUMBER];

    public static int x, y, lastX, lastY;
    public static boolean moving;

    @Override
    public void mousePressed(MouseEvent e) {
        keys[e.getButton()-1] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        keys[e.getButton()-1] = false;
    }

    public static void update() {
        for (int i = 0; i < keys.length; i++)
            lastkeys[i] = keys[i];
        lastX = x;
        lastY = y;
    }

    public static boolean isKeyDown(int keycode) {
        return keys[keycode];
    }

    public static boolean wasPressed(int keycode) {
        return isKeyDown(keycode) && !lastkeys[keycode];
    }

    public static boolean wasReleased(int keycode) {
        return !isKeyDown(keycode) && lastkeys[keycode];
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        moving = x != lastX || y != lastY;
    }
}
