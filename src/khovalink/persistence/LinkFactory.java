package khovalink.persistence;

import java.util.ArrayList;
import java.util.Collections;
import khovalink.gui.windows.canvas.LinkCrossing;
import khovalink.gui.windows.canvas.LinkCurve;

/**
 * Utility class used as a {@code Link} factory.
 *
 * @author flo
 */
public class LinkFactory {

    private static final int MAX_LENGTH_NAME = 32;
    private static final int MAX_CROSSINGS = 21;

    /**
     * Non instanciable class.
     */
    private LinkFactory() {
    }

    /**
     * Creates a new {@code Link} from another.
     *
     * @param name The link name.
     * @param link A {@code Link} from wich copy informations.
     *
     * @return The created {@code Link}.
     */
    public static Link rename(final String name, final Link link) {
        return new Link(name, link.getNbCompo(), link.getNbCross(), link.getGauss(), link.getSigns(), link.getCode(), link.getGraphicalLink());
    }

    /**
     * Creates a new {@code Link}.
     *
     * @param name The link name.
     * @param gauss The Gauss code of the link.
     * @param signs The signs of the link.
     *
     * @return The created {@code Link}.
     *
     * @throws LinkException If arguments doesn't fit together.
     */
    public static Link create(final String name, final int[][] gauss, final boolean[] signs) throws LinkException {
        return make(name, gauss.length, signs.length, gauss, signs, null);
    }

    /**
     * Creates a new {@code Link}.
     *
     * @param name The link name.
     * @param nbrCompo The component number as a {@code String}.
     * @param nbrCross The crossing number as a {@code String}.
     * @param gauss A {@code String} representation of the Gauss code of the
     * link.
     * @param signs A {@code String} representation of the signs of the link.
     *
     * @return The created {@code Link}.
     *
     * @throws LinkException If arguments doesn't fit together.
     */
    public static Link create(final String name, final String nbrCompo, final String nbrCross, final String gauss,
            final String signs) throws LinkException {
        final String[] splitsigns = signs.isEmpty() ? new String[0] : signs.split("[.]*");
        final boolean[] boolSigns = new boolean[splitsigns.length];
        for (int i = 0; i < splitsigns.length; i++) {
            boolSigns[i] = "+".equals(splitsigns[i]);
        }

        return make(name, Integer.parseInt(nbrCompo), Integer.valueOf(nbrCross), stringToGauss(gauss), boolSigns, null);
    }

    /**
     * Creates a new {@code Link}.
     *
     * @param name The link name.
     * @param nbrCompo The component number as a {@code String}.
     * @param nbrCross The crossing number as a {@code String}.
     * @param gauss A {@code String} representation of the Gauss code of the
     * link.
     * @param signs A {@code String} representation of the signs of the link.
     * @param graphicalLink The {@code GraphicalLink} of the link.
     *
     * @return The created {@code Link}.
     *
     * @throws LinkException If arguments doesn't fit together.
     */
    public static Link create(final String name, final String nbrCompo, final String nbrCross, final String gauss,
            final String signs, final GraphicalLink graphicalLink) throws LinkException {
        final String[] splitsigns = signs.isEmpty() ? new String[0] : signs.split("[.]*");
        final boolean[] boolSigns = new boolean[splitsigns.length];
        for (int i = 0; i < splitsigns.length; i++) {
            boolSigns[i] = "+".equals(splitsigns[i]);
        }

        return make(name, Integer.parseInt(nbrCompo), Integer.valueOf(nbrCross), stringToGauss(gauss), boolSigns, graphicalLink);
    }

    /**
     * Creates a new {@code Link} assuming given informations are consistents.
     *
     * @param name The link name.
     * @param graphicalLink The graphical representation of the link.
     *
     * @return The created {@code Link}.
     */
    public static Link createSafe(final String name, final GraphicalLink graphicalLink) {
        final ArrayList<LinkCrossing> intersections = graphicalLink.getIntersections();
        final boolean[] signs = new boolean[intersections.size()];

        for (int i = 0; i < intersections.size(); i++) {
            signs[i] = intersections.get(i).isPositive();
        }

        final int[][] gauss = makeGauss(graphicalLink.getCurves().size(), intersections);

        return new Link(name, graphicalLink.getCurves().size(), intersections.size(), gauss, signs, makeCode(gauss, signs), graphicalLink);
    }

    private static Link make(final String name, final int nbrCompo, final int nbrCross, final int[][] gauss, final boolean[] signs,
            final GraphicalLink graphicalLink) throws LinkException {
        final String message = validityCheck(name, nbrCompo, nbrCross, gauss, signs, graphicalLink);

        if (message.isEmpty()) {
            return new Link(name, nbrCompo, nbrCross, gauss, signs, makeCode(gauss, signs), graphicalLink);
        } else {
            throw new LinkException(message);
        }
    }

    /**
     * Turns a {@code String} representation of a Gauss code to a Gauss code.
     *
     * @param gauss A {@code String} representation of the Gauss code.
     *
     * @return The Gauss code.
     */
    private static int[][] stringToGauss(final String gauss) throws LinkException {
        if (!gauss.matches("(\\[([+-]\\d+)*\\])+")) {
            throw new LinkException("- Syntactic error in Gauss code.");
        }
        final int[][] goodGauss;

        final String[] splitGauss = gauss.split("(\\]\\[)");

        splitGauss[0] = splitGauss[0].substring(1);
        splitGauss[splitGauss.length - 1] = splitGauss[splitGauss.length - 1].substring(0, splitGauss[splitGauss.length - 1].length() - 1);

        goodGauss = new int[splitGauss.length][];
        for (int i = 0; i < splitGauss.length; i++) {
            goodGauss[i] = new int[splitGauss[i].length() - splitGauss[i].replace("+", "").replace("-", "").length()];
            final String[] splitted;
            if (splitGauss[i].length() != 0) {
                splitted = splitGauss[i].replace("+", " +").replace("-", " -").substring(1).split(" ");
            } else {
                splitted = new String[0];
            }
            for (int j = 0; j < splitted.length; j++) {
                goodGauss[i][j] = "".equals(splitted[0]) ? 0 : Integer.parseInt(splitted[j]);
            }
        }

        return goodGauss;
    }

    /**
     * Creates the code of a link.
     *
     * @param gauss The Gauss code of the link.
     * @param signs The signs of the link.
     *
     * @return The link code.
     */
    private static int[] makeCode(final int[][] gauss, final boolean[] signs) {
        final int[] code = new int[4 * signs.length];

        for (int[] gaus : gauss) {
            for (int i = 0; i < gaus.length; i++) {
                int j = ((i + 1) % gaus.length);
                if (gaus[i] > 0 && gaus[j] > 0) {
                    if (signs[gaus[i] - 1] && signs[gaus[j] - 1]) {
                        code[4 * gaus[i] - 2] = 4 * gaus[j] - 4;
                        code[4 * gaus[j] - 4] = 4 * gaus[i] - 2;
                    } else if (signs[gaus[i] - 1]) {
                        code[4 * gaus[i] - 2] = 4 * gaus[j] - 1;
                        code[4 * gaus[j] - 1] = 4 * gaus[i] - 2;
                    } else if (signs[gaus[j] - 1]) {
                        code[4 * gaus[i] - 3] = 4 * gaus[j] - 4;
                        code[4 * gaus[j] - 4] = 4 * gaus[i] - 3;
                    } else {
                        code[4 * gaus[i] - 3] = 4 * gaus[j] - 1;
                        code[4 * gaus[j] - 1] = 4 * gaus[i] - 3;
                    }
                } else if (gaus[i] > 0) {
                    if (signs[gaus[i] - 1] && signs[-gaus[j] - 1]) {
                        code[4 * gaus[i] - 2] = 4 * -gaus[j] - 1;
                        code[4 * -gaus[j] - 1] = 4 * gaus[i] - 2;
                    } else if (signs[gaus[i] - 1]) {
                        code[4 * gaus[i] - 2] = 4 * -gaus[j] - 4;
                        code[4 * -gaus[j] - 4] = 4 * gaus[i] - 2;
                    } else if (signs[-gaus[j] - 1]) {
                        code[4 * gaus[i] - 3] = 4 * -gaus[j] - 1;
                        code[4 * -gaus[j] - 1] = 4 * gaus[i] - 3;
                    } else {
                        code[4 * gaus[i] - 3] = 4 * -gaus[j] - 4;
                        code[4 * -gaus[j] - 4] = 4 * gaus[i] - 3;
                    }
                } else if (gaus[j] > 0) {
                    if (signs[-gaus[i] - 1] && signs[gaus[j] - 1]) {
                        code[4 * -gaus[i] - 3] = 4 * gaus[j] - 4;
                        code[4 * gaus[j] - 4] = 4 * -gaus[i] - 3;
                    } else if (signs[-gaus[i] - 1]) {
                        code[4 * -gaus[i] - 3] = 4 * gaus[j] - 1;
                        code[4 * gaus[j] - 1] = 4 * -gaus[i] - 3;
                    } else if (signs[gaus[j] - 1]) {
                        code[4 * -gaus[i] - 2] = 4 * gaus[j] - 4;
                        code[4 * gaus[j] - 4] = 4 * -gaus[i] - 2;
                    } else {
                        code[4 * -gaus[i] - 2] = 4 * gaus[j] - 1;
                        code[4 * gaus[j] - 1] = 4 * -gaus[i] - 2;
                    }
                } else if (signs[-gaus[i] - 1] && signs[-gaus[j] - 1]) {
                    code[4 * -gaus[i] - 3] = 4 * -gaus[j] - 1;
                    code[4 * -gaus[j] - 1] = 4 * -gaus[i] - 3;
                } else if (signs[-gaus[i] - 1]) {
                    code[4 * -gaus[i] - 3] = 4 * -gaus[j] - 4;
                    code[4 * -gaus[j] - 4] = 4 * -gaus[i] - 3;
                } else if (signs[-gaus[j] - 1]) {
                    code[4 * -gaus[i] - 2] = 4 * -gaus[j] - 1;
                    code[4 * -gaus[j] - 1] = 4 * -gaus[i] - 2;
                } else {
                    code[4 * -gaus[i] - 2] = 4 * -gaus[j] - 4;
                    code[4 * -gaus[j] - 4] = 4 * -gaus[i] - 2;
                }
            }
        }

        return code;
    }

    /**
     * Creates the Gauss code of a link from its intersection points.
     *
     * @param nbrCompo The link components number.
     * @param intersections The list of the link crossings.
     *
     * @return The Gauss code.
     */
    private static int[][] makeGauss(final int nbrCompo, final ArrayList<LinkCrossing> intersections) {
        final int[][] gaussTmp = new int[nbrCompo][];

        /**
         * Inner class representing an ID for crossings.
         */
        final class CrossID implements Comparable<CrossID> {

            private final int name, component, curve;
            private final double t;

            /**
             * Creates a new {@code CrossID}.
             *
             * @param name The crossing name.
             * @param curve The curve on wich the crossing is.
             * @param t The parameter locating the crossing on the curve.
             * @param isover A {@code boolean} telling if the curve is
             * overcrossing.
             */
            CrossID(final int name, final LinkCurve curve, final double t, final boolean isover) {
                this.name = name * (isover ? 1 : -1);
                this.component = curve.getComponent();
                this.curve = curve.getCurveNbr();
                this.t = t;
            }

            @Override
            public int compareTo(final CrossID crossId) {
                if (this.component < crossId.component) {
                    return -1;
                } else if (this.component > crossId.component) {
                    return 1;
                } else if (this.curve < crossId.curve) {
                    return -1;
                } else if (this.curve > crossId.curve) {
                    return 1;
                } else if (this.t < crossId.t) {
                    return -1;
                } else if (this.t > crossId.t) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        final ArrayList<CrossID> interID = new ArrayList<>(2 * intersections.size());

        for (int i = 0; i < intersections.size(); i++) {
            final LinkCrossing cross = intersections.get(i);
            interID.add(new CrossID(i + 1, cross.getCurveOver(), cross.getOverParameter(), true));
            interID.add(new CrossID(i + 1, cross.getCurveUnder(), cross.getUnderParameter(), false));
        }
        Collections.sort(interID);

        for (int compo = 0; compo < nbrCompo; compo++) {
            int i = 0;
            while (i < interID.size() && interID.get(i).component == compo) {
                i++;
            }
            gaussTmp[compo] = new int[i];
            i = 0;
            while (interID.size() > 0 && interID.get(0).component == compo) {
                gaussTmp[compo][i++] = interID.get(0).name;
                interID.remove(0);
            }
        }

        return gaussTmp;
    }

    /**
     * Creates a {@code String} representation of the errors in the link
     * attributes.
     *
     * @return A {@code String} representation of the errors.
     */
    private static String validityCheck(final String name, final int nbrCompo, final int nbrCross, final int[][] gauss, final boolean[] signs, final GraphicalLink graphicalLink) {
        final StringBuilder err = new StringBuilder(200);
        final ArrayList<Integer> signedCrossings = new ArrayList<>(64);

        if (name.length() > MAX_LENGTH_NAME) {
            err.append("- The link name must be less than ").append(MAX_LENGTH_NAME).append(" typings.\n");
        }
        if (nbrCross > MAX_CROSSINGS) {
            err.append("- The crossing number is limited to ").append(MAX_CROSSINGS).append(".\n");
        }
        if (nbrCompo != gauss.length) {
            err.append("- The component number is incompatible with the Gauss code.\n");
        }

        for (int[] compo : gauss) {
            for (int sCross : compo) {
                signedCrossings.add(sCross);
            }
        }

        if (signedCrossings.size() != 2 * nbrCross) {
            err.append("- The crossing number is incompatible with the Gauss code.\n");
        } else {
            for (int signedCross : signedCrossings) {
                if (Math.abs(signedCross) == 0 || Math.abs(signedCross) > nbrCross) {
                    err.append("- Crossings in the Gauss code must be numbered from 1 to ").append(nbrCross).append(".\n");
                    break;
                }
            }
            for (int i = 1; i <= nbrCross; i++) {
                if (!signedCrossings.remove((Integer) i)) {
                    err.append("- The crossing '+").append(i).append("' doesn't appear in the Gauss code.\n");
                }
                if (!signedCrossings.remove((Integer) (-i))) {
                    err.append("- The crossing '-").append(i).append("' doesn't appear in the Gauss code.\n");
                }
            }
        }

        if (signs.length != nbrCross) {
            err.append("- The crossings number doesn't fit with the number of signs.\n");
        }

        if (graphicalLink != null) {
            if (graphicalLink.getCurves().size() != nbrCompo) {
                err.append("- The components number doesn't fit with the number of graphical coponents.\n");
            }

            if (graphicalLink.getIntersections().size() != nbrCross) {
                err.append("- The crossings number doesn't fit with the number of graphical crossings.\n");
            }
        }

        return err.toString();
    }
}
