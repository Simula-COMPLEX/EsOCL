package no.simula.esocl.test;

import no.simula.esocl.ocl.distance.Result;
import no.simula.esocl.solver.OCLSolver;

public class EsOCL {

    public static void main(String args[]) throws Exception {
        String UMLModelPath = "src/main/resources/model/Test2.uml";
        String constraint = "" +
                "package Test2 " +
                "context A inv initial: self.b->select(b|b.x > 90)->size() > 4 and self.b->select(b|b.x > 90)->exists(b|b.x=92) " +
                "endpackage";
        OCLSolver oclSolver = new OCLSolver();
        Result result = oclSolver.solveConstraint(UMLModelPath, constraint, new int[]{OCLSolver.AVM, OCLSolver.OpOEA}, 5000);
        oclSolver.printResults(result);
    }
}



