package alternativeConstruction;

/**
 *
 *
 * Defines an Alpha object to be used in Reactive Construction. Saves the Alpha
 * value along with its probability of being selected.
 */
public class ReactiveAlpha {

    /**
     * Alpha value.
     */
    private final double val;

    /**
     * Probability of this Alpha being selected.
     */
    private double prob;

    /**
     * Number of times this Alpha was used in construction.
     */
    private int numberUses;

    /**
     * Sum of costs for all solutions using this Alpha.
     */
    private double totalCost;

    /**
     * Class builder, responsible for setting the value of Alpha and its initial
     * probability.
     *
     * @param val Alpha value.
     * @param prob Alpha's initial probability is selected.
     */
    public ReactiveAlpha(double val, double prob) {
        this.val = val;
        this.prob = prob;
        this.numberUses = 0;
        this.totalCost = 0D;
    }

    /**
     * Returns the value of this Alpha.
     *
     */
    public double getVal() {
        return val;
    }

    /**
     * Returns the probability that this Alpha value will be selected for the
     * Construction.
     */
    public double getProb() {
        return prob;
    }

    /**
     * Returns the number of solutions in which this Alpha value was used.
     *
     * @return The number of times this Alpha was used.
     */
    public int getNumberUses() {
        return numberUses;
    }

    /**
     * It indicates that this value of Alpha was used in the construction of
     * another solution.
     *
     * @param cost Cost of the new solution in which this Alpha value was used.
     */
    public void addUso(double cost) {
        this.numberUses++;
        this.totalCost += cost;
    }

    /**
     * Updates the probability value that this Alpha has to be selected.
     *
     * @param prob New probability of this Alpha being selected.
     */
    public void setProb(double prob) {
        if (prob > 0) {
            this.prob = prob;
        }
    }

    /**
     * Calculate the Qi of this Alpha, this value is used to calculate the new
     * probabilities of all Alpha candidate values. Calculated by formula Qi =
     * Z*\/Ai
     *
     * @param bestCost Cost of the best solution ever found so far.
     * @return The Qi value of this Alpha.
     */
    public double calcQi(double bestCost) {
        double average = bestCost;

        if (this.numberUses > 0) {
            average = this.totalCost / this.numberUses;
        } else {
            return 0D;
        }

        return bestCost / average;
    }

}
