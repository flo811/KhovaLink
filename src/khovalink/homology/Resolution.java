package khovalink.homology;

import khovalink.persistence.Link;
import java.util.ArrayList;

/**
 *
 * @author ib
 */
final class Resolution {

    private final int[] linkCode;
    private final int mask;

    private final int unlinked;

    Resolution(final Link link) {
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
        unlinked = not_linked;
    }

    public int resolve(final int resol) {
        final int marker = resol ^ mask;
        final int[] code = linkCode.clone();

        int circleNbr = 0, pos, startPos = 0;

        while (startPos < code.length) {
            if (code[startPos] != -1) {
                pos = startPos;

                while (true) {
                    code[pos] = -1;
                    if ((marker >>> (pos / 4) & 1) == 1) {
                        switch (pos % 4) {
                            case 0:
                                pos++;
                                break;
                            case 1:
                                pos--;
                                break;
                            case 2:
                                pos++;
                                break;
                            case 3:
                                pos--;
                                break;
                        }
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

                    if (code[pos] == -1) {
                        break;
                    }
                }
                circleNbr++;
            }
            startPos++;
        }

        return circleNbr + unlinked;
    }

    public ArrayList<ArrayList<Integer>> getCircles(final int resol) {
        final ArrayList<ArrayList<Integer>> circlesList = new ArrayList<>();
        final int marker = resol ^ mask;
        final int[] code = linkCode.clone();

        int pos, startPos = 0;

        while (startPos < code.length) {
            if (code[startPos] != -1) {
                final ArrayList<Integer> circle = new ArrayList<>();
                pos = startPos;

                while (true) {
                    circle.add(pos);
                    code[pos] = -1;
                    if ((marker >>> (pos / 4) & 1) == 1) {
                        switch (pos % 4) {
                            case 0:
                                pos++;
                                break;
                            case 1:
                                pos--;
                                break;
                            case 2:
                                pos++;
                                break;
                            case 3:
                                pos--;
                                break;
                        }
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

                    if (code[pos] == -1) {
                        break;
                    }
                }
                circlesList.add(circle);
            }
            startPos++;
        }

        return circlesList;
    }
}
