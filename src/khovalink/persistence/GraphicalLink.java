package khovalink.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import khovalink.gui.windows.canvas.LinkCrossing;
import khovalink.gui.windows.canvas.LinkCurve;

/**
 * Class representing a regular plannar diagram of a link with curves and
 * intersections.
 *
 * @author flo
 */
public class GraphicalLink implements Serializable {

    private static final long serialVersionUID = -7716305086132845876L;

    private final ArrayList<LinkCrossing> intersections;
    private final ArrayList<ArrayList<LinkCurve>> curves;

    /**
     * Instanciates a new {@code GraphicalLink}.
     *
     * @param intersections An array of crossings.
     * @param curves An array of link components.
     */
    public GraphicalLink(final ArrayList<LinkCrossing> intersections, final ArrayList<ArrayList<LinkCurve>> curves) {
        this.intersections = intersections;
        this.curves = curves;
    }

    /**
     * Returns the crossings.
     *
     * @return The crossings.
     */
    public ArrayList<LinkCrossing> getIntersections() {
        return new ArrayList<>(intersections);
    }

    /**
     * Returns the link curves.
     *
     * @return The link curves.
     */
    public ArrayList<ArrayList<LinkCurve>> getCurves() {
        return new ArrayList<>(curves);
    }
}
