package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import metaheuristics.tabusearch.AbstractTS;
import problems.qbf.QBF_Inverse;
import solutions.Solution;
import triple.Triple;
import triple.TripleElement;

/**
 * Metaheuristic TS (Tabu Search) for obtaining an optimal solution to a QBF
 * (Quadractive Binary Function -- {@link #QuadracticBinaryFunction}). Since by
 * default this TS considers minimization problems, an inverse QBF function is
 * adopted.
 *
 * @author ccavellucci, fusberti
 * @param <E>
 */
public class TS_MAXQBFPT_INTESIFICATION extends AbstractTS<Integer> {

    private final Integer fake = new Integer(-1);

    private TripleElement[] tripleElements;

    private Triple[] triples;

    private Integer[] count;

    private Integer qtd;

    /**
     * Constructor for the TS_QBF class.An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param tenure The Tabu tenure parameter.
     * @param filename Name of the file for which the objective function
     * parameters should be read.
     * @throws IOException necessary for I/O operations.
     */
    public TS_MAXQBFPT_INTESIFICATION(Integer tenure, String filename, Double pct, Integer execTime, Integer conversionIte) throws IOException {
        super(new QBF_Inverse(filename), tenure, execTime, conversionIte);

        this.qtd = new Integer((int) (ObjFunction.getDomainSize() * pct));
        generateTripleElements();
        generateTriples();
        count = new Integer[ObjFunction.getDomainSize()];

        for (int i = 0; i < count.length; i++) {

            count[i] = 0;
        }
    }

    @Override
    public Solution<Integer> solve() {

        long tempoInicial, iteracao;
        int iteracoesSemMelhora = 0;

        bestSol = createEmptySol();
        constructiveHeuristic();
        TL = makeTL();

        tempoInicial = System.currentTimeMillis();
        iteracao = 1;
        while ((((System.currentTimeMillis() - tempoInicial) / 1000.0) / 60.0) <= this.execTime) {
            iteracao++;
            iteracoesSemMelhora++;
            neighborhoodMove();
            if (bestSol.cost > incumbentSol.cost) {
                bestSol = new Solution<Integer>(incumbentSol);
                iteracoesSemMelhora = 0;

                if (verbose) {
                    System.out.println("(Iter. " + iteracao + ") BestSol = " + bestSol);
                }
            }

            if (iteracao % 100 == 0) {
                Boolean marcacao[] = new Boolean[ObjFunction.getDomainSize()];
                for (int j = 0; j < ObjFunction.getDomainSize(); j++) {
                    marcacao[j] = new Boolean(false);
                }
                incumbentSol = new Solution<Integer>(bestSol);
                for (int j = 0; j < qtd; j++) {
                    int imaior = 0;
                    while (marcacao[imaior]) {
                        imaior++;
                    }
                    for (int k = imaior + 1; k < ObjFunction.getDomainSize(); k++) {
                        if (!marcacao[k] && count[k] > count[imaior]) {
                            imaior = k;
                        }
                    }
                    marcacao[imaior] = Boolean.TRUE;

                    if (tripleElements[imaior].getAvailable()) {
                        incumbentSol.add(imaior);
                    }
                }
            }

            if (this.conversionIteration > 0 && iteracoesSemMelhora > this.conversionIteration) {
                break;
            }
        }

        return bestSol;
    }

    /* (non-Javadoc)
	 * @see metaheuristics.tabusearch.AbstractTS#makeCL()
     */
    @Override
    public ArrayList<Integer> makeCL() {

        ArrayList<Integer> _CL = new ArrayList<Integer>();
        for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
            Integer cand = new Integer(i);
            _CL.add(cand);
        }

        return _CL;

    }

    /**
     * Linear congruent function l used to generate pseudo-random numbers.
     */
    public int l(int pi1, int pi2, int u, int n) {
        return 1 + ((pi1 * u + pi2) % n);
    }

    /**
     * Function g used to generate pseudo-random numbers
     */
    public int g(int u, int n) {
        int pi1 = 131;
        int pi2 = 1031;
        int lU = l(pi1, pi2, u, n);

        if (lU != u) {
            return lU;
        } else {
            return 1 + (lU % n);
        }
    }

    /**
     * Function h used to generate pseudo-random numbers
     */
    public int h(int u, int n) {
        int pi1 = 193;
        int pi2 = 1093;
        int lU = l(pi1, pi2, u, n);
        int gU = g(u, n);

        if (lU != u && lU != gU) {
            return lU;
        } else if ((1 + (lU % n)) != u && (1 + (lU % n)) != gU) {
            return 1 + (lU % n);
        } else {
            return 1 + ((lU + 1) % n);
        }
    }

    /**
     * That method generates a list of objects (Triple Elements) that represents
     * each binary variable that could be inserted into a prohibited triple
     */
    private void generateTripleElements() {
        int n = ObjFunction.getDomainSize();
        this.tripleElements = new TripleElement[n];

        for (int i = 0; i < n; i++) {
            tripleElements[i] = new TripleElement(i);
        }
    }

    /**
     * Method that generates a list of n prohibited triples using l g and h
     * functions
     */
    private void generateTriples() {
        int n = ObjFunction.getDomainSize();
        this.triples = new Triple[ObjFunction.getDomainSize()];

        for (int u = 1; u <= n; u++) {
            TripleElement te1, te2, te3;
            Triple novaTripla;

            te1 = tripleElements[u - 1];
            te2 = tripleElements[g(u - 1, n) - 1];
            te3 = tripleElements[h(u - 1, n) - 1];
            novaTripla = new Triple(te1, te2, te3);

            Collections.sort(novaTripla.getElements(), new Comparator<TripleElement>() {
                public int compare(TripleElement te1, TripleElement te2) {
                    return te1.getIndex().compareTo(te2.getIndex());
                }
            });
            //novaTripla.printTriple();
            this.triples[u - 1] = novaTripla;
        }
    }


    /* (non-Javadoc)
	 * @see metaheuristics.tabusearch.AbstractTS#makeRCL()
     */
    @Override
    public ArrayList<Integer> makeRCL() {

        ArrayList<Integer> _RCL = new ArrayList<Integer>();

        return _RCL;

    }

    /* (non-Javadoc)
	 * @see metaheuristics.tabusearch.AbstractTS#makeTL()
     */
    @Override
    public ArrayDeque<Integer> makeTL() {
        int n = ObjFunction.getDomainSize();
        ArrayDeque<Integer> _CL = new ArrayDeque<Integer>(n);

        for (TripleElement tripElem : this.tripleElements) {
            tripElem.setAvailable(true);
            tripElem.setSelected(false);
            _CL.add(tripElem.getIndex());
        }

        return _CL;

    }

    /* (non-Javadoc)
	 * @see metaheuristics.tabusearch.AbstractTS#updateCL()
     */
    @Override
    public void updateCL() {

        ArrayList<Integer> _CL = new ArrayList<Integer>();

        for (TripleElement te : this.tripleElements) {
            te.setSelected(false);
            te.setAvailable(true);
        }

        if (this.incumbentSol != null) {
            for (Integer e : this.incumbentSol) {
                this.tripleElements[e].setSelected(true);
                this.tripleElements[e].setAvailable(false);
            }
        }

        for (Triple trip : this.triples) {
            TripleElement te0, te1, te2;
            te0 = trip.getElements().get(0);
            te1 = trip.getElements().get(1);
            te2 = trip.getElements().get(2);

            if (te0.getSelected() && te1.getSelected()) {
                te2.setAvailable(false);
            } else if (te0.getSelected() && te2.getSelected()) {
                te1.setAvailable(false);
            } else if (te1.getSelected() && te2.getSelected()) {
                te0.setAvailable(false);
            }
        }

        for (TripleElement tripElem : this.tripleElements) {
            if (!tripElem.getSelected() && tripElem.getAvailable()) {
                _CL.add(tripElem.getIndex());
            }
        }

        this.CL = _CL;

    }

    /**
     * {@inheritDoc}
     *
     * This createEmptySol instantiates an empty solution and it attributes a
     * zero cost, since it is known that a QBF solution with all variables set
     * to zero has also zero cost.
     */
    @Override
    public Solution<Integer> createEmptySol() {
        Solution<Integer> sol = new Solution<Integer>();
        sol.cost = 0.0;
        return sol;
    }

    public Solution<Integer> createRandomSol() {
        Solution<Integer> sol = new Solution<Integer>();

        for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
            double rand = Math.random();
            if (rand > 0.5) {
                boolean flag = true;
                for (Triple trip : this.triples) {
                    TripleElement te0, te1, te2;
                    te0 = trip.getElements().get(0);
                    te1 = trip.getElements().get(1);
                    te2 = trip.getElements().get(2);
                    if (te0.getIndex() == i) {
                        if (te1.getSelected() && te2.getSelected()) {
                            flag = false;
                        }
                    }
                    if (te1.getIndex() == i) {
                        if ((te0.getSelected() && te2.getSelected())) {
                            flag = false;
                        }
                    }
                    if (te2.getIndex() == i) {
                        if ((te1.getSelected() && te0.getSelected())) {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    sol.add(i);
                    tripleElements[i].setSelected(true);
                }
            }
        }

        sol.cost = ObjFunction.evaluate(sol);
        System.out.println("solucao aleatoria" + sol.toString());

        return sol;
    }

    /**
     * {@inheritDoc}
     *
     * The local search operator developed for the QBF objective function is
     * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
     */
    @Override
    public Solution<Integer> neighborhoodMove() {

        Double minDeltaCost;
        Integer bestCandIn = null, bestCandOut = null;

        minDeltaCost = Double.POSITIVE_INFINITY;
        updateCL();
        // Evaluate insertions
        for (Integer candIn : CL) {
            Double deltaCost = ObjFunction.evaluateInsertionCost(candIn, incumbentSol);
            if (!TL.contains(candIn) || incumbentSol.cost + deltaCost < bestSol.cost) {
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    bestCandIn = candIn;
                    bestCandOut = null;
                }
            }
        }
        // Evaluate removals
        for (Integer candOut : incumbentSol) {
            Double deltaCost = ObjFunction.evaluateRemovalCost(candOut, incumbentSol);
            if (!TL.contains(candOut) || incumbentSol.cost + deltaCost < bestSol.cost) {
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    bestCandIn = null;
                    bestCandOut = candOut;
                }
            }
        }
        // Evaluate exchanges
        for (Integer candIn : CL) {
            for (Integer candOut : incumbentSol) {
                Double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);
                if ((!TL.contains(candIn) && !TL.contains(candOut)) || incumbentSol.cost + deltaCost < bestSol.cost) {
                    if (deltaCost < minDeltaCost) {
                        minDeltaCost = deltaCost;
                        bestCandIn = candIn;
                        bestCandOut = candOut;
                    }
                }
            }
        }
        // Implement the best non-tabu move
        TL.poll();
        if (bestCandOut != null) {
            incumbentSol.remove(bestCandOut);
            CL.add(bestCandOut);
            TL.add(bestCandOut);
        } else {
            TL.add(fake);
        }
        TL.poll();
        if (bestCandIn != null) {
            incumbentSol.add(bestCandIn);
            CL.remove(bestCandIn);
            TL.add(bestCandIn);
        } else {
            TL.add(fake);
        }

        for (int i = 0; i < incumbentSol.size(); i++) {
            count[incumbentSol.get(i)]++;
        }

        ObjFunction.evaluate(incumbentSol);

        return null;
    }

    /**
     * A main method used for testing the TS metaheuristic.
     *
     */
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        TS_MAXQBFPT_INTESIFICATION tabusearch = new TS_MAXQBFPT_INTESIFICATION(Integer.parseInt(args[0]), "instances/qbf" + args[1], 0.10, 30, 1000000);

        Solution<Integer> bestSol = tabusearch.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");

    }

}
