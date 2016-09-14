package no.simula.esocl.test;


import no.simula.esocl.api.OCLSolver;
import no.simula.esocl.experiment.Result;
import org.junit.Test;

import java.io.File;

public class Test1 {
    @Test
    public void test1() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/Test.uml";
        File constraint = new File("/Users/hammad/git/EsOCL/src/main/resources/constraints/ocltest-1.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            System.out.println(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test2() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/Test.uml";
        File constraint = new File("/Users/hammad/git/EsOCL/src/main/resources/constraints/ocltest-2.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            System.out.println(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test3() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/Test.uml";
        File constraint = new File("/Users/hammad/git/EsOCL/src/main/resources/constraints/ocltest-3.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            System.out.println(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
