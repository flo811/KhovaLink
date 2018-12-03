package khovalink.homology;

import khovalink.persistence.Link;
import maths.exceptions.MathsArgumentException;
import maths.homology.DifferentialBiComplex;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.concurrent.Task;
import khovalink.KhovaLog;
import maths.homology.BiGradedHomology;
import maths.homology.SNFCalculator;
import maths.matrix.IntegerMatrix;
import maths.numbers.IntegerCalc;

/**
 *
 * @author ib
 */
public class LinkHomology extends Task<BiGradedHomology> {

    private final Link link;
    private final Resolution resolution;

    private final int crossingNbr;

    private final int negCross;
    private final int posCross;

    public LinkHomology(final Link link) {
        this.link = link;
        resolution = new Resolution(link);
        crossingNbr = link.getNbCross();

        int x = 0, y = 0;
        for (boolean sgn : link.getSigns()) {
            if (sgn) {
                y++;
            } else {
                x++;
            }
        }
        negCross = x;
        posCross = y;
    }

    @Override
    protected BiGradedHomology call() {
        try {
            final StringBuilder infos = new StringBuilder("\tLink Informations :\n\n")
                    .append(link.toString())
                    .append("\n\n\tLaunching homology calculation :\n\n- Starting timer.");
            updateMessage(infos.toString());
            final Instant start = Instant.now();

            updateMessage(infos.append("- Creating chain complex...").toString());
            final GeneratorsChainComplex generators = getGenerators();

            final Instant chainCplxStep = Instant.now();
            updateMessage(infos.append("Ok ! ").append(Duration.between(start, chainCplxStep)).append("\n- Creating differential complex...").toString());

            updateProgress(0, 1);
            final int tot = generators.getTotChains();
            final DifferentialBiComplex biComplex = new DifferentialBiComplex();
            final AtomicInteger i = new AtomicInteger(0);

            generators.getjGrads().parallelStream().forEach(jGrad -> {
                final HashMap<Integer, ArrayList<int[]>> jComp = generators.getjComplex(jGrad);
                jComp.keySet().parallelStream().forEach(iGrad -> {
                    biComplex.setijDiff(iGrad, jGrad, getijDiff(jComp.get(iGrad), jComp.get(iGrad + 1)));
                    updateProgress(i.incrementAndGet(), tot);
                });
            });

            final Instant diffCplxStep = Instant.now();
            updateMessage(infos.append("Ok ! ").append(Duration.between(chainCplxStep, diffCplxStep)).append("\n- Calculating homology...").toString());

            final BiGradedHomology homology = biComplex.getHomology(new SNFCalculator());

            final Instant end = Instant.now();
            updateMessage(infos.append("Ok ! ").append(Duration.between(diffCplxStep, end))
                    .append("\nTotal time elapsed : ").append(Duration.between(start, end)).toString());

            return homology;
        } catch (final MathsArgumentException ex) {
            KhovaLog.addLog(ex);
            cancel();
        }

        return null;
    }

    private GeneratorsChainComplex getGenerators() throws MathsArgumentException {
        final GeneratorsChainComplex generators = new GeneratorsChainComplex();
        final int resolNbr = IntegerCalc.pow2(crossingNbr);

        for (int resol = 0; resol < resolNbr; resol++) {
            final int circleNbr = resolution.resolve(resol);
            final int markerNbr = IntegerCalc.pow2(circleNbr);
            final int iGrad = Integer.bitCount(resol) - negCross;

            for (int circlesMarker = 0; circlesMarker < markerNbr; circlesMarker++) {
                final int jGrad = iGrad + 2 * Integer.bitCount(circlesMarker) - circleNbr - negCross + posCross;
                generators.addijGenerator(new int[]{resol, circlesMarker}, iGrad, jGrad);
            }

            updateProgress(resol + 1, resolNbr);
        }

        return generators;
    }

    private IntegerMatrix getijDiff(final ArrayList<int[]> bases1, final ArrayList<int[]> bases2) {
        try {
            if (bases1 == null) {
                if (bases2 != null) {
                    return IntegerMatrix.getEmpty(bases2.size(), 1);
                }
            } else if (bases2 == null) {
                return IntegerMatrix.getEmpty(1, bases1.size());
            }

            final int[][] diff = new int[bases2.size()][bases1.size()];

            for (int i = 0; i < bases1.size(); i++) {
                for (int j = 0; j < bases2.size(); j++) {
                    final int[] eks1 = bases1.get(i), eks2 = bases2.get(j);

                    //Check if there is exactly 1 difference between the markers of eks1 and eks2 where the one of eks2 is positive and the one of eks1 is negative.
                    if (Integer.bitCount(eks2[0] - eks1[0]) != 1) {
                        continue;
                    }

                    final ArrayList<ArrayList<Integer>> circles1 = resolution.getCircles(eks1[0]);
                    final ArrayList<ArrayList<Integer>> circles2 = resolution.getCircles(eks2[0]);

                    final ArrayList<ArrayList<Integer>> lessCircles, moreCircles;
                    if (circles1.size() > circles2.size()) {
                        lessCircles = circles2;
                        moreCircles = circles1;
                    } else {
                        lessCircles = circles1;
                        moreCircles = circles2;
                    }

                    int lCircDiffIndex = 0, mCircDiffIndex1 = 0, mCircDiffIndex2 = 0;
                    for (int k = 0; k < lessCircles.size(); k++) {
                        if (!moreCircles.contains(lessCircles.get(k))) {
                            lCircDiffIndex = k;
                            break;
                        }
                    }
                    boolean first = true;
                    for (int k = 0; k < moreCircles.size(); k++) {
                        if (!lessCircles.contains(moreCircles.get(k))) {
                            if (first) {
                                mCircDiffIndex1 = k;
                                first = false;
                            } else {
                                mCircDiffIndex2 = k;
                                break;
                            }
                        }
                    }

                    final int diffMarkerIndex = IntegerCalc.log2(eks2[0] - eks1[0]);

                    if (circles1.size() > circles2.size()) {
                        final int sameCircle2Marker = ((eks2[1] >> (lCircDiffIndex + 1)) << lCircDiffIndex) + (eks2[1] % IntegerCalc.pow2(lCircDiffIndex));
                        int sameCircle1Marker = ((eks1[1] >> (mCircDiffIndex1 + 1)) << mCircDiffIndex1) + (eks1[1] % IntegerCalc.pow2(mCircDiffIndex1));
                        sameCircle1Marker = ((sameCircle1Marker >> mCircDiffIndex2) << (mCircDiffIndex2 - 1)) + (sameCircle1Marker % (IntegerCalc.pow2(mCircDiffIndex2) / 2));

                        if (sameCircle1Marker != sameCircle2Marker) {
                            continue;
                        }

                        final int sign11 = (eks1[1] >> mCircDiffIndex1) & 1;
                        final int sign12 = (eks1[1] >> mCircDiffIndex2) & 1;
                        final int sign21 = (eks2[1] >> lCircDiffIndex) & 1;

                        if ((sign11 != sign12 && sign21 == 0) || (sign11 == 1 && sign12 == 1 && sign21 == 1)) {
                            diff[j][i] = (crossingNbr - diffMarkerIndex - 1 - Integer.bitCount(eks1[0] >> diffMarkerIndex + 1)) % 2 == 0 ? 1 : -1;
                        }
                    } else {
                        final int sameCircle1Marker = ((eks1[1] >> (lCircDiffIndex + 1)) << lCircDiffIndex) + (eks1[1] % IntegerCalc.pow2(lCircDiffIndex));
                        int sameCircle2Marker = ((eks2[1] >> (mCircDiffIndex1 + 1)) << mCircDiffIndex1) + (eks2[1] % IntegerCalc.pow2(mCircDiffIndex1));
                        sameCircle2Marker = ((sameCircle2Marker >> mCircDiffIndex2) << (mCircDiffIndex2 - 1)) + (sameCircle2Marker % (IntegerCalc.pow2(mCircDiffIndex2) / 2));

                        if (sameCircle1Marker != sameCircle2Marker) {
                            continue;
                        }

                        final int sign11 = (eks1[1] >> lCircDiffIndex) % 2;
                        final int sign21 = (eks2[1] >> mCircDiffIndex1) % 2;
                        final int sign22 = (eks2[1] >> mCircDiffIndex2) % 2;
                        if ((sign21 != sign22 && sign11 == 1) || (sign11 == 0 && sign21 == 0 && sign22 == 0)) {
                            diff[j][i] = (crossingNbr - diffMarkerIndex - 1 - Integer.bitCount(eks1[0] >> diffMarkerIndex + 1)) % 2 == 0 ? 1 : -1;
                        }
                    }
                }
            }

            return new IntegerMatrix(diff);
        } catch (final MathsArgumentException ex) {
            KhovaLog.addLog(ex);
            this.cancel();
        }

        return null;
    }
}
