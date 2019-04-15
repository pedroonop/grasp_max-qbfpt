package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import problems.Evaluator;
import problems.qbf.QBF_Inverse;
import solutions.Solution;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class TS_MAXQBFPT_SORROGATE extends TS_MAXQBFPT {

    private final int PORCENTAGEM = 40;
    private boolean relax = false;

    /**
     * the objective function being optimized
     */
    protected Evaluator<Integer> relaxObjFunction;

    public TS_MAXQBFPT_SORROGATE(Integer tenure, String filename, Integer execTime, Integer conversionIte) throws IOException {
        super(tenure, filename, execTime, conversionIte);

        this.relaxObjFunction = new QBF_Inverse(filename);
    }

    @Override
    public Solution<Integer> neighborhoodMove() {
        Solution<Integer> sol;

        this.relax = true;

        sol = super.neighborhoodMove();

        this.relax = false;
        return sol;
    }

    @Override
    public void updateCL() {
        List<Double[]> candidatos;
        ArrayList<Integer> _CL = new ArrayList<>();

        super.updateCL();

        if (!this.relax) {
            return;
        }

        // Selecionando os 15% melhores na inserção pela função relaxada
        candidatos = new ArrayList<>();
        for (Integer candIn : CL) {
            Double[] cand = new Double[2];

            cand[0] = candIn.doubleValue();
            cand[1] = relaxObjFunction.evaluateInsertionCost(candIn, incumbentSol);

            candidatos.add(cand);
        }
        addBetter(_CL, candidatos);

        // Selecionando os 15% melhores na remoção pela função relaxada
        candidatos = new ArrayList<>();
        for (Integer candOut : CL) {
            Double[] cand = new Double[2];

            cand[0] = candOut.doubleValue();
            cand[1] = relaxObjFunction.evaluateRemovalCost(candOut, incumbentSol);

            candidatos.add(cand);
        }
        addBetter(_CL, candidatos);

        // Selecionando os 15% melhores na troca
        candidatos = new ArrayList<>();
        for (Integer candIn : CL) {
            for (Integer candOut : incumbentSol) {
                Double[] cand1 = new Double[2];
                Double[] cand2 = new Double[2];

                cand1[0] = candIn.doubleValue();
                cand2[0] = candOut.doubleValue();
                cand1[1] = cand2[1] = relaxObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);

                candidatos.add(cand1);
                candidatos.add(cand2);
            }
        }
        addBetter(_CL, candidatos);

        // Atualizando CL
        this.CL = _CL;

    }

    private void addBetter(ArrayList<Integer> _cl, List<Double[]> candidatos) {
        Collections.sort(candidatos, (Double[] o1, Double[] o2) -> o1[1].compareTo(o2[1]) * -1);

        for (int i = 0; i < (int) ((candidatos.size() / 100D) * PORCENTAGEM); i++) {
            if (!_cl.contains(candidatos.get(i)[0].intValue())) {
                _cl.add(candidatos.get(i)[0].intValue());
            }
        }

    }

    /**
     * A main method used for testing the TS metaheuristic.
     *
     */
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        TS_MAXQBFPT_SORROGATE tabusearch = new TS_MAXQBFPT_SORROGATE(5, "instances/qbf020", 30, 1000);

        Solution<Integer> bestSol = tabusearch.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");

    }

}
