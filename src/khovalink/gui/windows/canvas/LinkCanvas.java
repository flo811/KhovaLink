package khovalink.gui.windows.canvas;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Shape;
import javafx.stage.StageStyle;
import khovalink.KhovaLog;
import khovalink.persistence.GraphicalLink;
import maths.exceptions.MathsException;
import maths.intersection.DetailledIntersection2D;
import maths.intersection.InterShape;

/**
 * Class representing a canvas to drow links.
 *
 * @author flo
 */
public class LinkCanvas extends Canvas {

    private static final double WIDTH = 900;
    private static final double HEIGHT = 650;

    private static final Color[] COLORS = {Color.DODGERBLUE, Color.YELLOW, Color.ORANGERED, Color.FUCHSIA,
        Color.PINK, Color.LAWNGREEN, Color.MEDIUMBLUE, Color.BLACK, Color.LIGHTSEAGREEN, Color.BLUE};

    private final GraphicsContext gc = getGraphicsContext2D();

    private final SimpleBooleanProperty isEmptyProperty = new SimpleBooleanProperty(true);
    private final SimpleObjectProperty<GraphicalLink> graphicalLinkProperty = new SimpleObjectProperty<>();

    private final ArrayList<LinkCurve> knot = new ArrayList<>(30);
    private final ArrayList<ArrayList<LinkCurve>> linkCurves = new ArrayList<>(5);
    private final ArrayList<LinkCrossing> intersectionPoints = new ArrayList<>(20);

    private Point2D start = null;

    /**
     * Creates a new {@code LinkCanvas}.
     */
    public LinkCanvas() {
        super(WIDTH, HEIGHT);

        gc.setFill(Color.AQUA);
        gc.setLineWidth(3);
        repaint();

        this.setOnMouseClicked(e -> addPoint(new Point2D(e.getX(), e.getY())));
        this.setOnMouseMoved(e -> mouseMoving(new Point2D(e.getX(), e.getY())));
    }

    /**
     * Repaints the canvas.
     */
    private void repaint() {
        gc.setFill(Color.AQUA);
        gc.fillRoundRect(0, 0, WIDTH, HEIGHT, 30, 30);

        linkCurves.forEach(curves -> {
            drawComponent(curves);
        });

        if (!knot.isEmpty()) {
            drawComponent(knot);
        }

        intersectionPoints.forEach(cross -> {
            final Line line = cross.getLine();
            gc.setFill(Color.AQUA);
            gc.setStroke(cross.getCurveOver().getColor());
            gc.fillOval(cross.getIntersection().getX() - 10, cross.getIntersection().getY() - 10, 20, 20);
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        });
    }

    /**
     * Draws a component of a link.
     *
     * @param curves An {@code ArrayList<LinkCurve>} representing the component.
     */
    private void drawComponent(final ArrayList<LinkCurve> curves) {
        gc.beginPath();
        curves.stream().forEach(curve -> curve.drawOn(gc));
        gc.stroke();
    }

    /**
     * Actualizes the canvas when the mouse is moving.
     *
     * @param point The {@code Point2D} representing the mouse's position.
     */
    private void mouseMoving(final Point2D point) {
        if (start == null) {
            return;
        }

        final LinkCurve curve = getNewCurve(point);
        repaint();

        try {
            gc.setStroke(Color.ORANGERED);
            findCrossings(curve).forEach(cross -> gc.strokeOval(cross.getIntersection().getX() - 5, cross.getIntersection().getY() - 5, 10, 10));
            gc.beginPath();
            curve.drawOn(gc);
            gc.stroke();
        } catch (final MathsException ex) {
            KhovaLog.addLog(ex);
            repaint();
        }
    }

    /**
     * Actualizes the canvas when the mouse is clicking.
     *
     * @param point The {@code Point2D} representing the mouse's position.
     */
    private void addPoint(final Point2D point) {

        if (start == null) {
            start = point;
            isEmptyProperty.set(false);
            graphicalLinkProperty.set(null);
            return;
        }

        LinkCurve newCurve = getNewCurve(point);

        try {
            final ArrayList<LinkCrossing> crossings = findCrossings(newCurve);
            if (newCurve.getColor() == Color.RED) {
                return;
            }
            setCrossOrietation(crossings);
            intersectionPoints.addAll(crossings);
            knot.add(newCurve);

            if (newCurve.getEnd().equals(start)) {
                linkCurves.add((ArrayList<LinkCurve>) knot.clone());
                knot.clear();
                start = null;

                graphicalLinkProperty.set(new GraphicalLink(intersectionPoints, linkCurves));
            }
        } catch (final MathsException ex) {
            KhovaLog.addLog(ex);
        }

        repaint();
    }

    /**
     * Returns the curve to drow.
     *
     * @param fromPoint The sstarting point of the current component.
     *
     * @return The current curve drown.
     */
    private LinkCurve getNewCurve(final Point2D fromPoint) {
        final Color color = COLORS[linkCurves.size() % COLORS.length];
        final boolean closeFromStart = fromPoint.distance(start) < 20;
        final int component = linkCurves.size();
        final int knotSize = knot.size();
        final LinkCurve curve;

        switch (knotSize) {
            case 0:
                curve = new LinkSegment(start, fromPoint, component, 0, color);
                curve.setColor(closeFromStart ? Color.RED : color);
                break;
            case 1:
                final Point2D lastEnd = knot.get(0).getEnd();
                final Point2D control = lastEnd.multiply(1.3).subtract(start.multiply(0.3));
                curve = new LinkQuad(lastEnd, control, fromPoint, component, 1, color);
                curve.setColor(closeFromStart || fromPoint.distance(lastEnd) < 20 ? Color.RED : color);
                break;
            default:
                final LinkQuad lastCurve = (LinkQuad) knot.get(knotSize - 1);
                final Point2D newControl = lastCurve.getEnd().multiply(1.3).subtract(lastCurve.getControl().multiply(0.3));
                curve = new LinkQuad(lastCurve.getEnd(), newControl, closeFromStart ? start : fromPoint, component, knotSize, color);
                curve.setColor(fromPoint.distance(knot.get(knotSize - 1).getEnd()) < 20 ? Color.RED : color);
        }

        return curve;
    }

    /**
     * Finds crossings between a cuve ans all of those of the link.
     *
     * @param lastDrown The {@code LinkCurve}.
     *
     * @return An {@code ArrayList<LinkCrossing>} of all the crossings found.
     */
    private ArrayList<LinkCrossing> findCrossings(final LinkCurve lastDrown) throws MathsException {
        final ArrayList<LinkCrossing> crossings = new ArrayList<>(5);

        for (final ArrayList<LinkCurve> compo : linkCurves) {
            for (final LinkCurve curve : compo) {
                if (!lastDrown.equals(curve)) {
                    crossings.addAll(getCrossings(lastDrown, curve));
                }
            }
        }

        for (final LinkCurve curve : knot) {
            if (!lastDrown.equals(curve)) {
                crossings.addAll(getCrossings(lastDrown, curve));
            }
        }

        final boolean isNotOk = crossings.stream().
                anyMatch(cross -> Math.abs(cross.getCurve1Derivate().normalize().crossProduct(cross.getCurve2Derivate().normalize()).magnitude()) < 0.2)
                || crossings.stream().anyMatch(cross -> minDistance(cross, crossings) < 20)
                || crossings.stream().anyMatch(cross -> cross.getIntersection().distance(start) < 20);

        if (isNotOk) {
            lastDrown.setColor(Color.RED);
        }

        return crossings;
    }

    private double minDistance(final LinkCrossing crossing, final ArrayList<LinkCrossing> crossList) {
        return Stream.concat(crossList.stream().filter(cross -> !cross.equals(crossing)), intersectionPoints.stream())
                .mapToDouble(cross -> cross.getIntersection().distance(crossing.getIntersection()))
                .min().orElse(20);
    }

    /**
     * Finds crossings between two cuves.
     *
     * @param curve1 The first curve.
     * @param curve2 The second curve.
     *
     * @return An {@code ArrayList<LinkCrossing>} of all the crossings found.
     */
    private ArrayList<LinkCrossing> getCrossings(final LinkCurve curve1, final LinkCurve curve2) throws MathsException {
        final ArrayList<DetailledIntersection2D<? extends Shape, ? extends Shape>> interList = new ArrayList<>(1);

        if (curve1 instanceof Line) {
            if (curve2 instanceof Line) {
                interList.addAll(InterShape.lineSegments((Line) curve1, (Line) curve2));
            } else {
                interList.addAll(InterShape.lineSegmentQuad((Line) curve1, (QuadCurve) curve2));
            }
        } else if (curve2 instanceof Line) {
            interList.addAll(InterShape.quadLineSement((QuadCurve) curve1, (Line) curve2));
        } else {
            interList.addAll(InterShape.quads((QuadCurve) curve1, (QuadCurve) curve2));
        }

        return interList.stream()
                .map(LinkCrossing::new)
                .filter(cross -> !(curve1.getComponent() == curve2.getComponent()
                && (curve1.getCurveNbr() - curve2.getCurveNbr() == 1 && cross.getCurve1Parameter() < 0.01
                || curve2.getCurveNbr() == 0 && cross.getCurve1Parameter() > 0.99)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Inner class representing an alert to ask for corssing orientation.
     */
    private class OverUnderDial extends Alert {

        /**
         * Creates a new {@code OverUnderDial}.
         *
         * @param coord A {@code Point2D} representing the location of the
         * crossing.
         */
        OverUnderDial(final Point2D coord) {
            super(Alert.AlertType.CONFIRMATION);

            initStyle(StageStyle.UNDECORATED);
            getDialogPane().setMinSize(0, 0);
            getDialogPane().setPrefSize(200, 100);
            setHeaderText(null);
            setContentText("Go over ?");
            getButtonTypes().setAll(ButtonType.OK, ButtonType.NO);
            setX(coord.getX());
            setY(coord.getY());
        }
    }

    /**
     * Sets wich curves are over in a list of crossings.
     *
     * @param crossings The list of crossings.
     *
     * @exception MathsArgumentException Thrown by
     * {@code LinkCrossing.makeSign(boolean isCurve1Over)}.
     * @exception MathsPrecisionException Thrown by
     * {@code LinkCrossing.makeSign(boolean isCurve1Over)}.
     * @exception MathsLoopOverflowException Thrown by
     * {@code LinkCrossing.makeSign(boolean isCurve1Over)}.
     * @exception MathsWrongResultException Thrown by
     * {@code LinkCrossing.makeSign(boolean isCurve1Over)}.
     */
    private void setCrossOrietation(final ArrayList<LinkCrossing> crossings) throws MathsException {
        for (LinkCrossing cross : crossings) {
            final OverUnderDial dial = new OverUnderDial(localToScreen(cross.getIntersection().getX() - 100, cross.getIntersection().getY() - 115));
            gc.setStroke(Color.BLUEVIOLET);
            gc.strokeOval(cross.getIntersection().getX() - 10, cross.getIntersection().getY() - 10, 20, 20);
            final Optional<ButtonType> pressedButton = dial.showAndWait();
            cross.makeSign(pressedButton.get() == ButtonType.OK);
            final Line line = cross.getLine();
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }
    }

    /**
     * Clears the canvas and draw a new link.
     *
     * @param graphicalLink The new {@code Link}.
     */
    public void setLink(final GraphicalLink graphicalLink) {
        clear();

        linkCurves.addAll(graphicalLink.getCurves());
        intersectionPoints.addAll(graphicalLink.getIntersections());
        isEmptyProperty.set(false);
        graphicalLinkProperty.set(graphicalLink);

        repaint();
    }

    /**
     * Clears the canvas.
     */
    public void clear() {
        start = null;
        knot.clear();
        linkCurves.clear();
        intersectionPoints.clear();
        isEmptyProperty.set(true);
        graphicalLinkProperty.set(null);

        repaint();
    }

    /**
     * Returns the property saying if the canvas is empty.
     *
     * @return An {@code SimpleBooleanProperty} representing the emptiness
     * property.
     */
    public SimpleBooleanProperty getIsEmptyProperty() {
        return isEmptyProperty;
    }

    /**
     * Returns the property containing the {@code GraphicalLink}.
     *
     * @return An {@code SimpleObjectProperty<GraphicalLink>} representing the
     * graphical link property.
     */
    public SimpleObjectProperty<GraphicalLink> getGraphicalLinkProperty() {
        return graphicalLinkProperty;
    }
}
