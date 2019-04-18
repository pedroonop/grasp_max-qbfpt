package problems.qbf;

import java.io.IOException;

/**
 *
 *
 */
public class QBF_Relax extends QBF {

    protected double[] sumCoefs;

    public QBF_Relax(String filename) throws IOException {
        super(filename);

        sumCoefs();
    }

    protected void sumCoefs() {
        this.sumCoefs = new double[this.size];

        for (int x1 = 0; x1 < this.size; x1++) {
            this.sumCoefs[x1] = 0D;

            for (int x2 = 0; x2 < this.size; x2++) {
                this.sumCoefs[x1] += A[x2][x1];
            }
        }
    }

    @Override
    public Double evaluateQBF() {
        Double val = 0D;

        for (int i = 0; i < this.variables.length; i++) {
            val += this.variables[i] * this.sumCoefs[i];
        }

        return val;
    }

    @Override
    protected Double evaluateContributionQBF(int i) {

        return this.sumCoefs[i];
    }

}
