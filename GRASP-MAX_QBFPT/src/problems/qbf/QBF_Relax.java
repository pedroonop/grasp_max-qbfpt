package problems.qbf;

import java.io.IOException;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class QBF_Relax extends QBF {

    private double[] sumCoefs;

    public QBF_Relax(String filename) throws IOException {
        super(filename);

        sumCoefs();
    }

    private void sumCoefs() {
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

        return variables[i] * this.sumCoefs[i];
    }
    
//    @Override
//    public Double evaluateExchangeQBF(int in, int out) {
//
//        Double sum = 0.0;
//
//        if (in == out) {
//            return 0.0;
//        }
//        if (variables[in] == 1) {
//            return evaluateRemovalQBF(out);
//        }
//        if (variables[out] == 0) {
//            return evaluateInsertionQBF(in);
//        }
//
//        sum += evaluateContributionQBF(in);
//        sum -= evaluateContributionQBF(out);
//        sum -= (A[in][out] + A[out][in]);
//
//        return sum;
//    }

}
