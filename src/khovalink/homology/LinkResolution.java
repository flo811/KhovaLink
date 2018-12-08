package khovalink.homology;

import java.util.ArrayList;
import khovalink.persistence.Link;

/**
 * Class used to calculate the number of circles resulting in applying a certain
 * resolution to a link.
 *
 * @author flo
 */
final class LinkResolution {

    private final int[] linkCode;
    private final int mask;

    private final int unlinkedNbr;

    /**
     * Creates a new {@code LinkResolution}, initializes the number of unlinked
     * circles and creates a mask for resolution numbers (due to the link code
     * implementation).
     *
     * @param link The link on wich calculate resolutions.
     */
    LinkResolution(final Link link) {
        linkCode = link.getCode();

        int tmpMask = 0;
        for (int i = link.getNbCross() - 1; i >= 0; i--) {
            tmpMask = (tmpMask << 1) + (link.getSigns()[i] ? 1 : 0);
        }
        mask = tmpMask;

        int not_linked = 0;
        for (final int[] gaus : link.getGauss()) {
            if (gaus.length == 0) {
                not_linked++;
            }
        }
        unlinkedNbr = not_linked;
    }

    /**
     * Calculates the number of circles given by this resolution.
     *
     * @param resol The resolution number.
     *
     * @return The number of circles.
     */
    int calculateCirclesNbr(final int resol) {
        final int marker = resol ^ mask;
        final int[] code = linkCode.clone();

        int circleNbr = 0, startPos = 0, pos;

        while (startPos < code.length) {
            if (code[startPos] != -1) {
                pos = startPos;

                while (code[pos] != -1) {
                    code[pos] = -1;
                    if ((marker >> (pos / 4) & 1) == 1) {
                        pos += pos % 2 == 0 ? 1 : -1;
                    } else {
                        switch (pos % 4) {
                            case 0:
                                pos += 3;
                                break;
                            case 1:
                                pos++;
                                break;
                            case 2:
                                pos--;
                                break;
                            case 3:
                                pos -= 3;
                                break;
                        }
                    }
                    final int tmpPos = pos;
                    pos = code[pos];
                    code[tmpPos] = -1;
                }
                circleNbr++;
            }
            startPos++;
        }

        return circleNbr + unlinkedNbr;
    }

    /**
     * Gives the list of non empty circles given by this resolution (a circle is
     * made of the positions it goes through).
     *
     * @param resol The resolution number.
     *
     * @return The of circles's list.
     */
    ArrayList<ArrayList<Integer>> getNonEmptyCircles(final int resol) {
        final ArrayList<ArrayList<Integer>> circlesList = new ArrayList<>();
        final int marker = resol ^ mask;
        final int[] code = linkCode.clone();

        int pos, startPos = 0;

        while (startPos < code.length) {
            if (code[startPos] != -1) {
                final ArrayList<Integer> circle = new ArrayList<>();
                pos = startPos;

                while (code[pos] != -1) {
                    circle.add(pos);
                    code[pos] = -1;
                    if ((marker >> (pos / 4) & 1) == 1) {
                        pos += pos % 2 == 0 ? 1 : -1;
                    } else {
                        switch (pos % 4) {
                            case 0:
                                pos += 3;
                                break;
                            case 1:
                                pos++;
                                break;
                            case 2:
                                pos--;
                                break;
                            case 3:
                                pos -= 3;
                                break;
                        }
                    }
                    circle.add(pos);
                    final int tmpPos = pos;
                    pos = code[pos];
                    code[tmpPos] = -1;
                }
                circlesList.add(circle);
            }
            startPos++;
        }

        return circlesList;
    }
}
