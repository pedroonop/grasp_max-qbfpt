package localSearch;

/**
 * Class that defines whether this step of the local search is insertion,
 * removal, or exchange. It also defines which elements will participate.
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 * @author Felipe de Carvalho Pereira [felipe.pereira@students.ic.unicamp.br]
 */
public class LocalSearchOperation {

    // Types of local search operation
    public static final int INSERT = 1;
    public static final int REMOVE = 2;
    public static final int EXCHANGE = 3;

    /**
     * Operation type.
     */
    private int type;

    /**
     * Element used for insertion or removal.
     */
    private Integer element;

    /**
     * Element that comes out in an exchange.
     */
    private Integer outElement;

    /**
     * Element that goes into an exchange.
     */
    private Integer inElement;

    /**
     * Creates an insert or remove operation.
     *
     * @param type Type of operation (LocalSearchOperation.INSERT,
     * LocalSearchOperation.REMOVE and LocalSearchOperation.EXCHANGE).
     * @param element Element that comes out or goes in.
     */
    public LocalSearchOperation(int type, Integer element) {
        this.type = type;
        this.element = element;
    }

    /**
     * Builder for a exchange operation.
     *
     * @param elementoSai Element coming out.
     * @param elementoEntra Element that enters.
     */
    public LocalSearchOperation(Integer elementoSai, Integer elementoEntra) {
        this.type = EXCHANGE;
        this.outElement = elementoSai;
        this.inElement = elementoEntra;
    }

    /**
     * Checks whether the operation is insertion.
     *
     * @return True if it is an insert operation.
     */
    public boolean isInsertion() {
        return this.type == INSERT;
    }

    /**
     * Checks if it is a removal operation.
     *
     * @return True if it is a removal operation.
     */
    public boolean isRemoval() {
        return this.type == REMOVE;
    }

    /**
     * Checks if it is a exchange operation.
     *
     * @return True if it is an exchange operation.
     */
    public boolean isExchange() {
        return this.type == EXCHANGE;
    }

    /**
     * Returns the element that is inserted or removed in an insert or remove
     * operation.
     *
     * @return The element that is inserted or removed.
     */
    public Integer getElement() {
        return element;
    }

    /**
     * Returns the element that exits in a exchange operation.
     *
     * @return The Element that comes out.
     */
    public Integer getOutElement() {
        return outElement;
    }

    /**
     * Retorna The element that goes into an exchange operation.
     *
     * @return The element that enters.
     */
    public Integer getInElement() {
        return inElement;
    }

}
