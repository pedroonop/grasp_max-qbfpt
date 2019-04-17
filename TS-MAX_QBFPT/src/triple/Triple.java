package triple;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object of this class represents a prohibited triple for MAXQBFPT problem.
 * 
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 * @author Felipe de Carvalho Pereira [felipe.pereira@students.ic.unicamp.br]
 */
public class Triple {

    public final ArrayList<TripleElement> elements;

    public Triple(TripleElement te1, TripleElement te2, TripleElement te3) {
        this.elements = new ArrayList<TripleElement>(Arrays.asList(te1, te2, te3));
    }

    public ArrayList<TripleElement> getElements() {
        return elements;
    }

    public void printTriple() {
        System.out.print("[" + elements.get(0).getIndex() + ", ");
        System.out.print(elements.get(1).getIndex() + ", ");
        System.out.println(elements.get(2).getIndex() + "]");
    }
}
