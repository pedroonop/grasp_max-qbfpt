package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import alternativeConstruction.ReactiveAlpha;
import solutions.Solution;
import triple.Triple;
import triple.TripleElement;

/**
 * Class that implements the GRASP specifications for solving the MAXQBFPT
 * problem.
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 * @author Felipe de Carvalho Pereira [felipe.pereira@students.ic.unicamp.br]
 */
public class GRASP_MAXQBFPT extends GRASP_QBF {

    // RLC construction mechanisms
    public static final int STANDARD = 1;
    public static final int REACTIVE = 2;
    public static final int SAMPLED_GREEDY = 3;
    private final int contructionMechanism;

    /**
     * List of alphas used in reactive construction.
     */
    private ArrayList<ReactiveAlpha> reactiveAlphas;

    /**
     * Parameter p used in sampleGreedy construction.
     */
    private int sampleGreedyP;

    /**
     * Best solution found by constructive heuristics.
     */
    private Solution<Integer> bestSolConstHeurist;

    /**
     * List of element objects used in prohibited triples. These objects
     * represents the variables of the model.
     */
    private TripleElement[] tripleElements;

    /**
     * List of prohibited triples.
     */
    private Triple[] triples;

    /**
     * Constructor of the class, responsible for defining the fixed Alpha value
     * to be used, the type of construction that will be used, the local search
     * strategy to be used, the time limit to find the solution, the number of
     * iterations without improvement in the solution that defines the
     * convergence and the file with the coefficients of that instance of the
     * problem.
     *
     * @param alpha The value of the alpha used in construction
     * @param contructionMechanism The type of construction to be used
     * (GRASP_MAXQBFPT.STANDARD, GRASP_MAXQBFPT.REACTIVE e
     * GRASP_MAXQBFPT.SAMPLED_GREEDY).
     * @param firstImproving If the local search strategy will be First
     * Improving, otherwise it will be Best Improving.
     * @param timeLimite Timeout in minutes for the execution of the algorithm.
     * @param iterationsLimit Number of iterations without solution improvement
     * until considering the convergence of the algorithm. If a negative value
     * is used only the execution time will be considered as a stop criterion.
     * @param filename Local or complete path of the file with the coefficients
     * of that instance of the problem.
     * @throws IOException Generates an exception if the file with the
     * coefficients does not exist.
     */
    public GRASP_MAXQBFPT(Double alpha, int contructionMechanism, Boolean firstImproving, Integer timeLimite, Integer iterationsLimit, String filename) throws IOException {

        super(alpha, firstImproving, timeLimite, iterationsLimit, filename);
        this.contructionMechanism = contructionMechanism;
        this.bestSolConstHeurist = new Solution<>();

        if (contructionMechanism == REACTIVE) {
            generateAlphaList();
        } else if (contructionMechanism == SAMPLED_GREEDY) {
            sampleGreedyP = (int) (0.05 * ObjFunction.getDomainSize());
        }

        generateTripleElements();
        generateTriples();
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
            this.triples[u-1] = novaTripla;
        }
    }

    /**
     * The GRASP constructive heuristic, which is responsible for building a
     * feasible solution by selecting in a greedy-random fashion, candidate
     * elements to enter the solution.
     *
     * @return A feasible solution to the problem being minimized.
     */
    @Override
    public Solution<Integer> constructiveHeuristic() {
        Solution<Integer> partialSolution;

        switch (this.contructionMechanism) {
            case REACTIVE:
                selectAlpha();
                partialSolution = super.constructiveHeuristic();
                updateAlphasProbabilities(partialSolution);
                break;
            case SAMPLED_GREEDY:
                partialSolution = sampleGreedyConstruction();
                break;
            default:
                // Standard constructive
                partialSolution = super.constructiveHeuristic();
                break;
        }

        if (partialSolution.cost < this.bestSolConstHeurist.cost) {
            this.bestSolConstHeurist = new Solution<>(partialSolution);
        }

        return partialSolution;
    }

    /**
     * A GRASP CL generator for MAXQBFPT problem
     *
     * @return A list of candidates to partial solution
     */
    @Override
    public ArrayList<Integer> makeCL() {
        int n = ObjFunction.getDomainSize();
        ArrayList<Integer> _CL = new ArrayList<Integer>(n);

        for (TripleElement tripElem : this.tripleElements) {
            tripElem.setAvailable(true);
            tripElem.setSelected(false);
            _CL.add(tripElem.getIndex());
        }

        return _CL;
    }

    /**
     * The GRASP CL updater for MAXQBFPT problem
     *
     */
    @Override
    public void updateCL() {
        ArrayList<Integer> _CL = new ArrayList<Integer>();

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

    /*
     * Method that implements sampled greedy construction for MAXQBFPT problem
     * First it selects min{sampleGreedyParamenter, |feasibleCandidates|} random candidates
     * from all feasible candidates to construct a sampleGreedyCL.
     * Then 
     */
    private Solution<Integer> sampleGreedyConstruction() {

        incumbentSol = createEmptySol();
        incumbentCost = Double.POSITIVE_INFINITY;
        CL = makeCL();
        RCL = makeRCL();

        /* Main loop, which repeats until the stopping criteria is reached. */
        while (!constructiveStopCriteria()) {
            double minCost = Double.POSITIVE_INFINITY;
            Integer bestCandidate = -1;

            incumbentCost = ObjFunction.evaluate(incumbentSol);
            updateCL();

            if (CL.isEmpty()) {
                break;
            }

            /* Chose randomly min{sampleGreedyP,|CL|} candidates to constructi a new CL*/
            @SuppressWarnings("unchecked")
            ArrayList<Integer> sampledGreedyCL = (ArrayList<Integer>) CL.clone();
            Collections.shuffle(sampledGreedyCL);
            int minimumP = Math.min(this.sampleGreedyP, CL.size());
            for (int i = 0; i < minimumP; i++) {
                RCL.add(sampledGreedyCL.get(i));
            }

            /* Choose the best candidate from the RCL */
            for (Integer c : RCL) {
                Double newCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
                if (newCost < minCost) {
                    minCost = newCost;
                    bestCandidate = c;
                }
            }

            // Insert best candidate in partial solution
            CL.remove(bestCandidate);
            incumbentSol.add(bestCandidate);
            ObjFunction.evaluate(incumbentSol);
            RCL.clear();
        }

        return incumbentSol;
    }

    /**
     * Generates the list of possible Alphas for Reactive Construction.
     */
    private void generateAlphaList() {
        double INITIAL_VALUE = 0.01;
        double INCREMENT = 0.01;
        double FINAL_VALUE = 1D;

        this.reactiveAlphas = new ArrayList<>();

        for (double val = INITIAL_VALUE; val <= FINAL_VALUE; val += INCREMENT) {
            ReactiveAlpha alphaReativo = new ReactiveAlpha(val, 1D / (FINAL_VALUE / INCREMENT));
            this.reactiveAlphas.add(alphaReativo);
        }
    }

    /**
     * Selects an Alpha value from the list of possibilities to be used in the
     * construction, the selection takes into account the probability of each
     * Alpha.
     */
    private void selectAlpha() {
        Random random = new Random();
        double totalProb = 0D;
        double rand, sum;

        for (ReactiveAlpha alp : this.reactiveAlphas) {
            if (alp.getNumberUses() < 1) {
                this.alpha = alp.getVal();
                return;
            }

            totalProb += alp.getProb();
        }

        rand = random.nextDouble() * totalProb;

        sum = 0D;
        Collections.shuffle(this.reactiveAlphas);
        for (ReactiveAlpha alp : this.reactiveAlphas) {
            sum += alp.getProb();

            if (sum >= rand) {
                this.alpha = alp.getVal();
                break;
            }
        }

        // System.out.println("Selected Alpha = " + this.alpha);
    }

    /**
     * Updates the probabilities of all Alpha values.
     *
     * @param sol Best solution found so far.
     */
    private void updateAlphasProbabilities(Solution<Integer> sol) {
        // Updating used alpha now
        for (ReactiveAlpha alp : this.reactiveAlphas) {
            if (alp.getVal() == this.alpha) {
                alp.addUso(sol.cost);
                break;
            }
        }

        // Calculating new probabilities
        double qiSum = 0D;
        double bestCost_ = sol.cost;

        if (this.bestCost != null) {
            bestCost_ = this.bestCost;
        }

        for (ReactiveAlpha alp : this.reactiveAlphas) {
            qiSum += alp.calcQi(bestCost_);
        }

        for (ReactiveAlpha alp : this.reactiveAlphas) {
            alp.setProb(alp.calcQi(bestCost_) / qiSum);
        }
    }

    public Solution<Integer> getBestSolConstHeurist() {
        return bestSolConstHeurist;
    }

}
