package khovalink.persistence;

import java.io.Serializable;

/**
 * Class representing a link.
 *
 * @author flo
 */
public class Link implements Serializable {

    private static final long serialVersionUID = -6495970131577082915L;

    private final String name;
    private final int nbrCompo;
    private final int nbrCross;
    private final int[][] gauss;
    private final boolean[] signs;
    private final int[] linkCode;

    private final GraphicalLink graphicalLink;

    /**
     * Creates a new {@code Link}.
     *
     * @param name The link name.
     * @param nbrCompo The component number as a {@code String}.
     * @param nbrCross The crossing number as a {@code String}.
     * @param gauss The Gauss code of the link.
     * @param signs The signs of the link.
     * @param linkCode A code of the link.
     * @param graphicalLink A representation of the link.
     */
    public Link(final String name, final int nbrCompo, final int nbrCross, final int[][] gauss, final boolean[] signs, final int[] linkCode, final GraphicalLink graphicalLink) {
        this.name = name;
        this.nbrCompo = nbrCompo;

        this.nbrCross = nbrCross;
        this.gauss = gauss;
        this.signs = signs;
        this.linkCode = linkCode;
        this.graphicalLink = graphicalLink;
    }

    /**
     * Returns a copy of the link code.
     *
     * @return The link code.
     */
    public int[] getCode() {
        return linkCode.clone();
    }

    /**
     * Returns the link name.
     *
     * @return The link name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the component number.
     *
     * @return The component number.
     */
    public int getNbCompo() {
        return nbrCompo;
    }

    /**
     * Returns the crossing number.
     *
     * @return The crossing number.
     */
    public int getNbCross() {
        return nbrCross;
    }

    /**
     * Returns a copy of the Gauss code.
     *
     * @return The Gauss code.
     */
    public int[][] getGauss() {
        return gauss.clone();
    }

    /**
     * Returns a copy of the signs.
     *
     * @return The link signs.
     */
    public boolean[] getSigns() {
        return signs.clone();
    }

    /**
     * Returns the {@code GraphicalLink}.
     *
     * @return The {@code GraphicalLink}.
     */
    public GraphicalLink getGraphicalLink() {
        return graphicalLink;
    }

    @Override
    public String toString() {
        final StringBuilder description = new StringBuilder("");
        description.append("Link name :\t").append(name)
                .append("\nComponent number :\t").append(nbrCompo)
                .append("\nCrossing number :\t").append(nbrCross)
                .append("\nGauss code :\t[");

        for (final int[] gau : gauss) {
            for (int i : gau) {
                description.append(i > 0 ? '+' : "").append(i);
            }
            description.append("][");
        }
        description.deleteCharAt(description.length() - 1);

        description.append("\nCrossing signs :\t");
        for (final boolean i : signs) {
            description.append(i ? " +" : " -");
        }

        return description.toString();
    }
}
