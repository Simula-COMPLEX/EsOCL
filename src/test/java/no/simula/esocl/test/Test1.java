package no.simula.esocl.test;


import no.simula.esocl.ocl.distance.Result;
import no.simula.esocl.solver.OCLSolver;
import org.junit.Test;

import java.io.File;

public class Test1 {
    @Test
    public void test1() {

        String inputModelPath = "src/main/resources/model/Test.uml";
        File constraint = new File("src/main/resources/constraints/ocltest-1.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new int[]{OCLSolver.AVM, OCLSolver.OpOEA}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test2() {

        String inputModelPath = "src/main/resources/model/Test.uml";
        File constraint = new File("src/main/resources/constraints/ocltest-2.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new int[]{OCLSolver.AVM, OCLSolver.OpOEA}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test3() {

        String inputModelPath = "src/main/resources/model/Test.uml";
        File constraint = new File("src/main/resources/constraints/ocltest-3.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new int[]{OCLSolver.AVM, OCLSolver.OpOEA}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
