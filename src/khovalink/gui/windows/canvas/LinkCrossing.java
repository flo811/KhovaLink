package khovalink.gui.windows.canvas;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Shape;
import khovalink.persistence.LinkCrossingProxy;
import maths.exceptions.MathsArgumentException;
import maths.exceptions.MathsLoopOverflowException;
import maths.exceptions.MathsPrecisionException;
import maths.exceptions.MathsWrongResultException;
import maths.intersection.DetailledIntersection2D;
import maths.intersection.InterShape;
import maths.intersection.SimpleIntersection2D;

/**
 * Class representing a link crossing.
 *
 * @author flo
 */
public final class LinkCrossing extends DetailledIntersection2D<Shape, Shape> implements Serializable {

    private static final long serialVersionUID = 517751509406050281L;

    private LinkCurve curveOver, curveUnder;
    private double overParameter, underParameter;
    private boolean isPositive;

    private LinkSegment line;

    /**
     * Creates a new {@code LinkCrossing}.
     *
     * @param inter An {@code DetailledIntersection2D} to initialize.
     */
    public LinkCrossing(final DetailledIntersection2D<? extends Shape, ? extends Shape> inter) {
        super(inter.getCurve1(), inter.getCurve2(), inter.getIntersection(),
                inter.getCurve1Parameter(), inter.getCurve2Parameter(),
                inter.getCurve1Derivate(), inter.getCurve2Derivate());
    }

    public LinkCrossing(final LinkCurve curveOver, final LinkCurve curveUnder, final double overParameter, final double underParameter,
            final boolean isPositive, final LinkSegment line, final Shape curve1, final Shape curve2, final Point2D intersection,
            final double curve1Parameter, final double curve2Parameter, final Point2D curve1Derivate, final Point2D curve2Derivate) {
        super(curve1, curve2, intersection, curve1Parameter, curve2Parameter, curve1Derivate, curve2Derivate);
        this.curveOver = curveOver;
        this.curveUnder = curveUnder;
        this.overParameter = overParameter;
        this.underParameter = underParameter;
        this.isPositive = isPositive;
        this.line = line;
    }

    /**
     * Calculates if the crossing is positive or negative.
     *
     * @param isCurve1Over Boolean telling if the first curve is over the second
     * one.
     *
     * @throws MathsArgumentException Thrown by
     * {@code InterShape.circleLineSegment(final Circle circle, final Line segment)}
     * or
     * {@code InterShape.circleQuad(final Circle circle, final QuadCurve quad)}.
     * @throws MathsPrecisionException Thrown by
     * {@code InterShape.circleLineSegment(final Circle circle, final Line segment)}
     * or
     * {@code InterShape.circleQuad(final Circle circle, final QuadCurve quad)}.
     * @throws MathsLoopOverflowException Thrown by
     * {@code InterShape.circleQuad(final Circle circle, final QuadCurve quad)}.
     * @throws MathsWrongResultException If the number of intersection points is
     * different from two.
     */
    public void makeSign(final boolean isCurve1Over) throws MathsArgumentException, MathsPrecisionException, MathsLoopOverflowException, MathsWrongResultException {
        curveOver = (LinkCurve) (isCurve1Over ? getCurve1() : getCurve2());
        curveUnder = (LinkCurve) (isCurve1Over ? getCurve2() : getCurve1());
        overParameter = isCurve1Over ? getCurve1Parameter() : getCurve2Parameter();
        underParameter = isCurve1Over ? getCurve2Parameter() : getCurve1Parameter();
        isPositive = (getCurve1Derivate().getX() * getCurve2Derivate().getY() - getCurve1Derivate().getY() * getCurve2Derivate().getX()) * (isCurve1Over ? 1 : -1) < 0;

        final ArrayList<SimpleIntersection2D<Circle, ? extends Shape>> interList = new ArrayList<>(2);
        if (curveOver instanceof Line) {
            interList.addAll(InterShape.circleLineSegment(new Circle(intersection.getX(), intersection.getY(), 10), (Line) curveOver));
        } else {
            interList.addAll(InterShape.circleQuad(new Circle(intersection.getX(), intersection.getY(), 10), (QuadCurve) curveOver));
        }

        if (interList.size() != 2) {
            throw new MathsWrongResultException("The number of intersection points is " + interList.size() + '.');
        } else {
            line = new LinkSegment(new Point2D(interList.get(0).getIntersection().getX(), interList.get(0).getIntersection().getY()),
                    new Point2D(interList.get(1).getIntersection().getX(), interList.get(1).getIntersection().getY()), -1, -1, Color.TRANSPARENT);
        }
    }

    /**
     * Returns a {@code boolean} set to {@code true} if the crossing is positive
     * anf {@code false} if it is negative.
     *
     * @return The crossing sign.
     */
    public boolean isPositive() {
        return isPositive;
    }

    /**
     * Returns a {@code Line} to draw the link at the crossing point.
     *
     * @return The {@code Line} to draw.
     */
    public Line getLine() {
        return line;
    }

    /**
     * Returns the crossing's overlapping {@code LinkCurve}.
     *
     * @return The overlapping {@code LinkCurve}.
     */
    public LinkCurve getCurveOver() {
        return curveOver;
    }

    /**
     * Returns the crossing's underlapping {@code LinkCurve}.
     *
     * @return The underlapping {@code LinkCurve}.
     */
    public LinkCurve getCurveUnder() {
        return curveUnder;
    }

    /**
     * Returns the crossing's overlapping {@code LinkCurve} parameter.
     *
     * @return The parameter.
     */
    public double getOverParameter() {
        return overParameter;
    }

    /**
     * Returns the crossing's underlapping {@code LinkCurve} parameter.
     *
     * @return The parameter.
     */
    public double getUnderParameter() {
        return underParameter;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new LinkCrossingProxy(curveOver, curveUnder, overParameter, underParameter, isPositive, line, curve1,
                curve2, intersection, curve1Parameter, curve2Parameter, curve1Derivate, curve2Derivate);
    }
}
