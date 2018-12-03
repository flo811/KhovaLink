package khovalink.gui.windows.canvas;

import java.io.Serializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Interface specifying methods for link curves.
 *
 * @author flo
 */
public interface LinkCurve extends Serializable {

    /**
     * Draws the curve on a canvas.
     *
     * @param gc The {@code GraphicsContext} to draw the curve.
     */
    void drawOn(final GraphicsContext gc);

    /**
     * Returns the starting point of the curve.
     *
     * @return A {@code Point2D} beeing the curve's starting point.
     */
    Point2D getStart();

    /**
     * Returns the first coordinate of the curve's starting point.
     *
     * @return A {@code double} beeing the starting point's first coordinate.
     */
    double getStartX();

    /**
     * Returns the second coordinate of the curve's starting point.
     *
     * @return A {@code double} beeing the starting point's second coordinate.
     */
    double getStartY();

    /**
     * Returns the ending point of the curve.
     *
     * @return A {@code Point2D} beeing the curve's ending point.
     */
    Point2D getEnd();

    /**
     * Returns the first coordinate of the curve's ending point.
     *
     * @return A {@code double} beeing the ending point's first coordinate.
     */
    double getEndX();

    /**
     * Returns the second coordinate of the curve's ending point.
     *
     * @return A {@code double} beeing the ending point's second coordinate.
     */
    double getEndY();

    /**
     * Returns the curve's component number.
     *
     * @return An {@code int} beeing the component number.
     */
    int getComponent();

    /**
     * Returns the curve's number in a link component.
     *
     * @return An {@code int} beeing the curve's number in the component.
     */
    int getCurveNbr();

    /**
     * Returns the curve's color.
     *
     * @return The curve's color.
     */
    Color getColor();

    /**
     * Sets the curve's color.
     *
     * @param color The color.
     */
    void setColor(final Color color);
}
