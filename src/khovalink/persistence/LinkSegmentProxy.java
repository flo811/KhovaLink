package khovalink.persistence;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import khovalink.gui.windows.canvas.LinkSegment;

/**
 * Proxy class to serialize {@code LinkSegment}.
 *
 * @author flo
 */
public class LinkSegmentProxy implements Serializable {

    private static final long serialVersionUID = -4686374675299233415L;

    private final double startX, startY, endX, endY;
    private final double colorR, colorG, colorB;
    private final int componentNbr, curveNbr;

    public LinkSegmentProxy(final double startX, final double startY, final double endX, final double endY, final double colorR,
            final double colorG, final double colorB, final int componentNbr, final int curveNbr) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
        this.componentNbr = componentNbr;
        this.curveNbr = curveNbr;
    }

    private Object readResolve() throws ObjectStreamException {
        return new LinkSegment(new Point2D(startX, startY), new Point2D(endX, endY), componentNbr, curveNbr, new Color(colorR, colorG, colorB, 1));
    }
}
