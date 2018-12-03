package khovalink.gui.windows.canvas;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import khovalink.persistence.LinkSegmentProxy;

/**
 * Class representing a personalized {@code Line}.
 *
 * @author flo
 */
public class LinkSegment extends Line implements LinkCurve, Serializable {

    private static final long serialVersionUID = -8258106032562252395L;

    private final int component, curveNbr;
    private Color color;

    /**
     * Creates a new {@code LinkSegment}.
     *
     * @param start The starting point of the {@code LinkSegment}.
     * @param end The ending point of the {@code LinkSegment}.
     * @param component The component number of the curve.
     * @param curveNbr The number of the curve in the link's component.
     * @param color The color of the line.
     */
    public LinkSegment(final Point2D start, final Point2D end, final int component, final int curveNbr, final Color color) {
        super(start.getX(), start.getY(), end.getX(), end.getY());

        this.component = component;
        this.curveNbr = curveNbr;
        this.color = color;
    }

    @Override
    public void drawOn(final GraphicsContext gc) {
        final Point2D midPoint = getStart().midpoint(getEnd());
        double theta = Math.atan2(getStartY() - getEndY(), getStartX() - getEndX()) - Math.PI / 6;
        final Point2D leftPoint = new Point2D(midPoint.getX() + 15 * Math.cos(theta), midPoint.getY() + 15 * Math.sin(theta));
        theta += Math.PI / 3;
        final Point2D rightPoint = new Point2D(midPoint.getX() + 15 * Math.cos(theta), midPoint.getY() + 15 * Math.sin(theta));

        final double xValues[] = {midPoint.getX(), leftPoint.getX(), rightPoint.getX()};
        final double yValues[] = {midPoint.getY(), leftPoint.getY(), rightPoint.getY()};

        gc.setStroke(getColor());
        gc.setFill(getColor());
        gc.fillPolygon(xValues, yValues, 3);
        gc.moveTo(getStartX(), getStartY());
        gc.lineTo(getEndX(), getEndY());
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
        return new LinkSegmentProxy(getStartX(), getStartY(), getEndX(), getEndY(), color.getRed(), color.getGreen(), color.getBlue(), component, curveNbr);
    }
}
