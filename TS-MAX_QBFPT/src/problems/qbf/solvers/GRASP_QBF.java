package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import localSearch.LocalSearchOperation;
import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.QBF_Inverse;
import solutions.Solution;

/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 *
 * @author ccavellucci, fusberti
 */
public class GRASP_QBF extends AbstractGRASP<Integer> {

    protected boolean firstImproving = false;

    /**
     * Constructor for the GRASP_QBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param alpha The GRASP greediness-randomness parameter (within the range
     * [0,1])
     * @param firstImproving
     * @param tempoExecucao
     * @param iteraConvengencia
     * @param filename Name of the file for which the objective function
     * parameters should be read.
     * @throws IOException necessary for I/O operations.
     */
    public GRASP_QBF(Double alpha, Boolean firstImproving, Integer tempoExecucao, Integer iteraConvengencia, String filename) throws IOException {
        super(new QBF_Inverse(filename), alpha, tempoExecucao, iteraConvengencia);

        this.firstImproving = firstImproving;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeCL()
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

    /*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeRCL()
     */
    @Override
    public ArrayList<Integer> makeRCL() {

        ArrayList<Integer> _RCL = new ArrayList<Integer>();

        return _RCL;

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#updateCL()
     */
    @Override
    public void updateCL() {
        // do nothing since all elements off the solution are viable candidates.
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

    /**
     * {@inheritDoc}
     *
     * The local search operator developed for the QBF objective function is
     * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
     */
    @Override
    public Solution<Integer> localSearch() {

        Random rand = new Random();
        Double minDeltaCost;
        LocalSearchOperation melhorVizinho = null;

        do {
            minDeltaCost = Double.POSITIVE_INFINITY;
            updateCL();

            ArrayList<LocalSearchOperation> listaVizinhanca = new ArrayList<>();

            // Adicionando toda vizinhança de inclusão de elementos
            for (Integer candIn : CL) {
                listaVizinhanca.add(new LocalSearchOperation(LocalSearchOperation.INSERT, candIn));
            }

            // Adicionando toda vizinhança de remoção de elementos
            for (Integer candOut : incumbentSol) {
                listaVizinhanca.add(new LocalSearchOperation(LocalSearchOperation.REMOVE, candOut));
            }

            // Adicionando toda vizinhança de troca de elementos
            for (Integer candIn : CL) {
                for (Integer candOut : incumbentSol) {
                    listaVizinhanca.add(new LocalSearchOperation(candOut, candIn));
                }
            }

            // Com a lista de todos os vizinhos que devem ser visitados os visita de forma aleatória e salva o melhor
            while (!listaVizinhanca.isEmpty()) {
                // Escolhe de forma aleatória qual o próximo vizinho a ser visitado
                int ind = rand.nextInt(listaVizinhanca.size());

                double deltaCost = Double.POSITIVE_INFINITY;
                LocalSearchOperation vizinho = listaVizinhanca.get(ind);
                if (vizinho.isInsertion()) {
                    deltaCost = ObjFunction.evaluateInsertionCost(vizinho.getElement(), incumbentSol);
                }
                if (vizinho.isRemoval()) {
                    deltaCost = ObjFunction.evaluateRemovalCost(vizinho.getElement(), incumbentSol);
                }
                if (vizinho.isExchange()) {
                    deltaCost = ObjFunction.evaluateExchangeCost(vizinho.getInElement(), vizinho.getOutElement(), incumbentSol);
                }

                // Removendo esse vizinho para não passar mais por ele
                listaVizinhanca.remove(ind);

                // Se esse vizinho é o melhor até agora
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    melhorVizinho = vizinho;

                    // Se for fist improving basta dar um break aqui
                    if (this.firstImproving) {
                        break;
                    }
                }
            }

            // Implement the best move, if it reduces the solution cost.
            if (minDeltaCost < -Double.MIN_VALUE && melhorVizinho != null) {
                if (melhorVizinho.isInsertion()) {
                    incumbentSol.add(melhorVizinho.getElement());
                    CL.remove(melhorVizinho.getElement());
                }

                if (melhorVizinho.isRemoval()) {
                    incumbentSol.remove(melhorVizinho.getElement());
                    CL.add(melhorVizinho.getElement());
                }

                if (melhorVizinho.isExchange()) {
                    incumbentSol.add(melhorVizinho.getInElement());
                    CL.remove(melhorVizinho.getInElement());

                    incumbentSol.remove(melhorVizinho.getOutElement());
                    CL.add(melhorVizinho.getOutElement());
                }

                ObjFunction.evaluate(incumbentSol);
            }
        } while (minDeltaCost < -Double.MIN_VALUE);

        return null;
    }

    /**
     * A main method used for testing the GRASP metaheuristic.
     *
     */
    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GRASP_QBF grasp = new GRASP_QBF(0.05, false, 30, 100, "instances/qbf020");

        Solution<Integer> bestSol = grasp.solve();

        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");

    }

}
