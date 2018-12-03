package khovalink.persistence;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import khovalink.gui.windows.canvas.LinkCrossing;
import khovalink.gui.windows.canvas.LinkCurve;
import khovalink.gui.windows.canvas.LinkSegment;

/**
 *
 * @author flo
 */
public class LinkCrossingProxy implements Serializable {

    private static final long serialVersionUID = 6662758632955175384L;

    final LinkCurve curve1, curve2;
    final LinkCurve curveOver, curveUnder;
    final double intersectionX, intersectionY, curve1DerivateX, curve1DerivateY, curve2DerivateX, curve2DerivateY;
    final LinkSegment line;
    final double overParameter, underParameter, curve1Parameter, curve2Parameter;
    final boolean isPositive;

    public LinkCrossingProxy(final LinkCurve curveOver, final LinkCurve curveUnder, final double overParameter, final double underParameter,
            final boolean isPositive, final LinkSegment line, final Shape curve1, final Shape curve2, final Point2D intersection,
            final double curve1Parameter, final double curve2Parameter, final Point2D curve1Derivate, final Point2D curve2Derivate) {
        this.curveOver = curveOver;
        this.curveUnder = curveUnder;
        this.line = line;
        this.curve1 = (LinkCurve) curve1;
        this.curve2 = (LinkCurve) curve2;
        this.intersectionX = intersection.getX();
        this.intersectionY = intersection.getY();
        this.curve1DerivateX = curve1Derivate.getX();
        this.curve1DerivateY = curve1Derivate.getY();
        this.curve2DerivateX = curve2Derivate.getX();
        this.curve2DerivateY = curve2Derivate.getY();
        this.overParameter = overParameter;
        this.underParameter = underParameter;
        this.curve1Parameter = curve1Parameter;
        this.curve2Parameter = curve2Parameter;
        this.isPositive = isPositive;
    }

    private Object readResolve() throws ObjectStreamException {
        return new LinkCrossing(curveOver, curveUnder, overParameter, underParameter, isPositive, line, (Shape) curve1,
                (Shape) curve2, new Point2D(intersectionX, intersectionY), curve1Parameter, curve2Parameter,
                new Point2D(curve1DerivateX, curve1DerivateY), new Point2D(curve2DerivateX, curve2DerivateY));
    }
}
