package khovalink.homology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author flo
 */
public class GeneratorsChainComplex {

    private final HashMap<Integer, HashMap<Integer, ArrayList<int[]>>> complex = new HashMap<>();

    private int totChains = 0;

    public GeneratorsChainComplex() {
    }

    public void addijGenerator(final int[] eks, final int iGrad, final int jGrad) {
        if (!complex.containsKey(jGrad)) {
            complex.put(jGrad, new HashMap<>());
        }

        final HashMap<Integer, ArrayList<int[]>> jComplex = complex.get(jGrad);
        if (!jComplex.containsKey(iGrad)) {
            jComplex.put(iGrad, new ArrayList<>());
            totChains++;
        }

        jComplex.get(iGrad).add(eks);
    }

    public ArrayList<int[]> getijGenerators(final int iGrad, final int jGrad) {
        return complex.get(jGrad) == null ? null : complex.get(jGrad).get(iGrad);
    }

    public HashMap<Integer, ArrayList<int[]>> getjComplex(final int jGrad) {
        return complex.get(jGrad);
    }

    public Set<Integer> getjGrads() {
        return complex.keySet();
    }

    public int getTotChains() {
        return totChains;
    }
}
