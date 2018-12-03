package khovalink.persistence;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import khovalink.gui.windows.canvas.LinkQuad;

/**
 * Proxy class to serialize {@code LinkQuad}.
 *
 * @author flo
 */
public class LinkQuadProxy implements Serializable {

    private static final long serialVersionUID = 4351299394991624606L;

    private final double startX, startY, controlX, controlY, endX, endY;
    private final double colorR, colorG, colorB;
    private final int componentNbr, curveNbr;

    public LinkQuadProxy(final double startX, final double startY, final double controlX, final double controlY, final double endX, final double endY,
            final double colorR, final double colorG, final double colorB, final int componentNbr, final int curveNbr) {
        this.startX = startX;
        this.startY = startY;
        this.controlX = controlX;
        this.controlY = controlY;
        this.endX = endX;
        this.endY = endY;
        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
        this.componentNbr = componentNbr;
        this.curveNbr = curveNbr;
    }

    private Object readResolve() throws ObjectStreamException {
        return new LinkQuad(new Point2D(startX, startY), new Point2D(controlX, controlY), new Point2D(endX, endY), componentNbr, curveNbr, new Color(colorR, colorG, colorB, 1));
    }
}
