package problems.qbf;

import java.io.IOException;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class QBF_Relax_Inverse extends QBF_Relax {

    /**
     * Constructor for the QBF_Inverse class.
     *
     * @param filename Name of the file for which the objective function
     * parameters should be read.
     * @throws IOException Necessary for I/O operations.
     */
    public QBF_Relax_Inverse(String filename) throws IOException {
        super(filename);
    }


    /* (non-Javadoc)
	 * @see problems.qbf.QBF#evaluate()
     */
    @Override
    public Double evaluateQBF() {
        return -super.evaluateQBF();
    }

    /* (non-Javadoc)
	 * @see problems.qbf.QBF#evaluateInsertion(int)
     */
    @Override
    public Double evaluateInsertionQBF(int i) {
        return -super.evaluateInsertionQBF(i);
    }

    /* (non-Javadoc)
	 * @see problems.qbf.QBF#evaluateRemoval(int)
     */
    @Override
    public Double evaluateRemovalQBF(int i) {
        return -super.evaluateRemovalQBF(i);
    }

    /* (non-Javadoc)
	 * @see problems.qbf.QBF#evaluateExchange(int, int)
     */
    @Override
    public Double evaluateExchangeQBF(int in, int out) {
        return -super.evaluateExchangeQBF(in, out);
    }

}
