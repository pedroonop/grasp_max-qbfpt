package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import problems.Evaluator;
import problems.qbf.QBF_Relax_Inverse;
import solutions.Solution;

/**
 *
 *
 */
public class TS_MAXQBFPT_SORROGATE extends TS_MAXQBFPT {

    /**
     * Constant that defines the percentage of possible neighbors that will be
     * selected to apply the actual objective function.
     */
    private final int PORCENTAGEM = 20;

    /**
     * Flag that defines whether to use the relaxed function.
     */
    private boolean relax = false;

    /**
     * The objective relax function.
     */
    protected Evaluator<Integer> relaxObjFunction;

    /**
     * Constructor of the class that implements Tabu Search with Surrogate
     * strategy.
     *
     * @param tenure Tabu list size.
     * @param filename Local or complete path of the file with the coefficients
     * of that instance of the problem.
     * @param execTime Timeout in minutes for the execution of the algorithm.
     * @param conversionIte Number of iterations without solution improvement
     * until considering the convergence of the algorithm. If a negative value
     * is used only the execution time will be considered as a stop criterion.
     * @throws IOException Generates an exception if the file with the
     * coefficients does not exist.
     */
    public TS_MAXQBFPT_SORROGATE(Integer tenure, String filename, Integer execTime, Integer conversionIte) throws IOException {
        super(tenure, filename, execTime, conversionIte);

        this.relaxObjFunction = new QBF_Relax_Inverse(filename);
    }

    /**
     * It implements the local search among the neighbors, but applying the
     * Surrogate strategy.
     *
     * @return the solution found.
     */
    @Override
    public Solution<Integer> neighborhoodMove() {
        Solution<Integer> sol;

        this.relax = true;

        sol = super.neighborhoodMove();

        this.relax = false;
        return sol;
    }

    /**
     * Updates the CL with only the best neighbors applied to the relaxed
     * objective function.
     */
    @Override
    public void updateCL() {
        List<Double[]> candidatos;
        ArrayList<Integer> _CL = new ArrayList<>();

        super.updateCL();

        if (!this.relax) {
            return;
        }

        // Selecionando os melhores na inserção pela função relaxada
        candidatos = new ArrayList<>();
        for (Integer candIn : CL) {
            Double[] cand = new Double[2];

            cand[0] = candIn.doubleValue();
            cand[1] = relaxObjFunction.evaluateInsertionCost(candIn, incumbentSol);

            candidatos.add(cand);
        }
        addBetter(_CL, candidatos);

        // Selecionando os melhores na remoção pela função relaxada
        candidatos = new ArrayList<>();
        for (Integer candOut : CL) {
            Double[] cand = new Double[2];

            cand[0] = candOut.doubleValue();
            cand[1] = relaxObjFunction.evaluateRemovalCost(candOut, incumbentSol);

            candidatos.add(cand);
        }
        addBetter(_CL, candidatos);

        // Selecionando os melhores na troca
        candidatos = new ArrayList<>();
        for (Integer candIn : CL) {
            Double d;
            Double[] cand1 = new Double[2];
            cand1[0] = candIn.doubleValue();
            cand1[1] = Double.MAX_VALUE;

            for (Integer candOut : incumbentSol) {
                d = relaxObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);

                if (d < cand1[1]) {
                    cand1[1] = d;
                }
            }

            candidatos.add(cand1);
        }
        addBetter(_CL, candidatos);

        // Atualizando CL
        this.CL = _CL;

    }

    /**
     * Adds the best candidates on the list if they are not on the list.
     *
     * @param _cl new CL.
     * @param candidatos candidates to join CL.
     */
    private void addBetter(ArrayList<Integer> _cl, List<Double[]> candidatos) {
        Integer novoValor;
        Collections.sort(candidatos, (Double[] o1, Double[] o2) -> o1[1].compareTo(o2[1]));

        int tamanho = (int) ((candidatos.size() / 100D) * PORCENTAGEM);

        for (int i = 0; i < tamanho; i++) {
            novoValor = candidatos.get(i)[0].intValue();

            if (!_cl.contains(novoValor)) {
                _cl.add(novoValor);
            }
        }

    }

    /**
     * A main method used for testing the TS metaheuristic.
     *
     */
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        TS_MAXQBFPT_SORROGATE tabusearch = new TS_MAXQBFPT_SORROGATE(Integer.parseInt(args[0]), "instances/qbf" + args[1], 30, 1000000);

        Solution<Integer> bestSol = tabusearch.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");

    }

}
