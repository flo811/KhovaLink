package khovalink.gui.windows.canvas;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import khovalink.persistence.LinkQuadProxy;

/**
 * Class representing a personalized {@code QuadCurve}.
 *
 * @author flo
 */
public class LinkQuad extends QuadCurve implements LinkCurve, Serializable {

    private static final long serialVersionUID = -5072819363994593453L;

    private final int component, curveNbr;
    private Color color;

    /**
     * Creates a new {@code LinkQuad}.
     *
     * @param start The starting point of the {@code LinkQuad}.
     * @param control The control point of the {@code LinkQuad}.
     * @param end The ending point of the {@code LinkQuad}.
     * @param component The component number of the curve.
     * @param curveNbr The number of the curve in the link's component.
     * @param color The color of the curve.
     */
    public LinkQuad(final Point2D start, final Point2D control, final Point2D end, final int component, final int curveNbr, final Color color) {
        super(start.getX(), start.getY(), control.getX(), control.getY(), end.getX(), end.getY());

        this.component = component;
        this.curveNbr = curveNbr;
        this.color = color;
    }

    @Override
    public void drawOn(final GraphicsContext gc) {
        gc.setStroke(getColor());
        gc.setFill(getColor());
        gc.moveTo(getStartX(), getStartY());
        gc.quadraticCurveTo(getControlX(), getControlY(), getEndX(), getEndY());
    }

    @Override
    public int getComponent() {
        return component;
    }

    @Override
    public int getCurveNbr() {
        return curveNbr;
    }

    @Override
    public Point2D getStart() {
        return new Point2D(getStartX(), getStartY());
    }

    /**
     * Returns the control point of the {@code QuadCurve}.
     *
     * @return A {@code Point2D} beeing the curve's control point.
     */
    public Point2D getControl() {
        return new Point2D(getControlX(), getControlY());
    }

    @Override
    public Point2D getEnd() {
        return new Point2D(getEndX(), getEndY());
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(final Color color) {
        this.color = color;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new LinkQuadProxy(getStartX(), getStartY(), getControlX(), getControlY(), getEndX(), getEndY(), color.getRed(), color.getGreen(), color.getBlue(), component, curveNbr);
    }
}
