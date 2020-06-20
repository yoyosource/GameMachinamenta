package engine;

import javax.swing.*;
import java.awt.*;

public class GameView extends JComponent {

    Graphics2D graphics2D = null;

    @Override
    protected void paintComponent(Graphics g) {
        graphics2D = (Graphics2D)g;
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

}
