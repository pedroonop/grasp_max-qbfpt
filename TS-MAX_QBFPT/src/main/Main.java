package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import problems.qbf.solvers.GRASP_MAXQBFPT;
import solutions.Solution;

/**
 * Class that executes the GRASP for MAXQBFPT problem
 *
 * Standard configuration: Alpha = 0.05 Construction mechanism = standard Local
 * search = first improvement
 */
public class Main {

    public static double ALPHA;
    public static int CONSTRUCTION_MECHANISM;
    public static boolean FIRST_IMPROVING;
    public static int TIME_LIMIT = 30; // In minutes
    public static int ITERATIONS_LIMIT = -1; // Iterations without improvement in incumbent (negative values for not using iterations limit)
    public static String outputCsv;

    public static final String[] FILES_LIST = new String[]{
        "instances/qbf020",
    	"instances/qbf040",
    	"instances/qbf060",
        "instances/qbf080",
        "instances/qbf100",
        "instances/qbf200",
        "instances/qbf400"
    };

    //Calls execution method with 5 different configurations
    public static void main(String[] args) throws IOException {

        outputCsv = "fileName,alpha,construction,localSearch,valueConstruction,valueGRASP\n";

        executeGRASP(0.05, GRASP_MAXQBFPT.STANDARD, true); //Standard configuration
        executeGRASP(0.10, GRASP_MAXQBFPT.STANDARD, true); //Standard with other alpha value
        executeGRASP(0.05, GRASP_MAXQBFPT.STANDARD, false); //Standard with best improving
        executeGRASP(0.05, GRASP_MAXQBFPT.REACTIVE, true); // Standard with reactive construction mechanism
        executeGRASP(0.05, GRASP_MAXQBFPT.SAMPLED_GREEDY, true); // Standard with sampled greedy construction mechanism

        saveOutput("output.csv", outputCsv);
    }

    private static void executeGRASP(double alpha, int constructionMechanism, boolean firstImproving) throws IOException {

        long beginTotalTime = System.currentTimeMillis();
        ALPHA = alpha;
        CONSTRUCTION_MECHANISM = constructionMechanism;
        FIRST_IMPROVING = firstImproving;

        // Iterating over files
        for (String arquivo : FILES_LIST) {

            //Print configurations of the execution
            System.out.println("Executing GRASP for file: " + arquivo);
            System.out.println("Configuration:");
            printConstructionMechanism();
            printAlpha();
            printLocalSearchStrategie();
            printStopCriterion();

            // Executing GRASP
            System.out.println("Execution:");

            long beginInstanceTime = System.currentTimeMillis();

            GRASP_MAXQBFPT grasp = new GRASP_MAXQBFPT(ALPHA, CONSTRUCTION_MECHANISM, FIRST_IMPROVING, TIME_LIMIT, ITERATIONS_LIMIT, arquivo);
            Solution<Integer> bestSolution = grasp.solve();
            System.out.println(" maxVal = " + bestSolution);
            System.out.println(" construction = " + grasp.getBestSolConstHeurist().cost);

            long endInstanceTime = System.currentTimeMillis();
            long totalInstanceTime = endInstanceTime - beginInstanceTime;
            System.out.println("Time = " + (double) totalInstanceTime / (double) 1000 + " seg");
            System.out.println("\n");

            outputCsv += arquivo + "," + ALPHA + "," + CONSTRUCTION_MECHANISM + "," + FIRST_IMPROVING + ","
                    + +grasp.getBestSolConstHeurist().cost + "," + bestSolution.cost + "\n";

        }

        // Calculating time of all executions
        long totalTime = System.currentTimeMillis() - beginTotalTime;

        System.out.println("Tempo execução todos arquivos: " + (totalTime / 1000D) + "seg \n"
                + "----------------------------------------------------- \n \n");
    }

    private static void printConstructionMechanism() {
        String resp = " Construction mechanism = ";

        if (CONSTRUCTION_MECHANISM == GRASP_MAXQBFPT.STANDARD) {
            resp += "Standard";
        }
        if (CONSTRUCTION_MECHANISM == GRASP_MAXQBFPT.REACTIVE) {
            resp += "Reactive";
        }
        if (CONSTRUCTION_MECHANISM == GRASP_MAXQBFPT.SAMPLED_GREEDY) {
            resp += "Sampled greedy";
        }

        System.out.println(resp);
    }

    private static void printAlpha() {
        if (CONSTRUCTION_MECHANISM == GRASP_MAXQBFPT.STANDARD) {
            System.out.println(" Alpha = " + ALPHA);
        }
    }

    private static void printLocalSearchStrategie() {
        String resp = " Local Search = ";

        if (FIRST_IMPROVING) {
            resp += "First Improving";
        } else {
            resp += "Best Improving";
        }

        System.out.println(resp);
    }

    private static void printStopCriterion() {
        String resp = " Stop Criterion = ";

        resp += TIME_LIMIT + " minutes";

        if (ITERATIONS_LIMIT > 0) {
            resp += " ou " + ITERATIONS_LIMIT + " iterations without new incumbent";
        }

        System.out.println(resp);
    }

    public static void saveOutput(String fileName, String content) {
        File dir;
        PrintWriter out;

        dir = new File("output");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            out = new PrintWriter(new File(dir, fileName));
            out.print(content);
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
