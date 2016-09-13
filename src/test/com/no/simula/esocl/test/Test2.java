package no.simula.esocl.test;


import junit.framework.TestCase;
import no.simula.esocl.api.OCLSolver;
import no.simula.esocl.experiment.Result;
import org.junit.Test;

import java.io.File;

public class Test2 extends TestCase {
    @Test
    public void test1() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/Test2.uml";
        File constraint = new File("/Users/hammad/git/EsOCL/src/main/resources/constraints/ocltest2-1.ocl");
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

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/Test2.uml";
        File constraint = new File("/Users/hammad/git/EsOCL/src/main/resources/constraints/ocltest2-2.ocl");
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

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/Test2.uml";
        File constraint = new File("/Users/hammad/git/EsOCL/src/main/resources/constraints/ocltest2-3.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            System.out.println(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test4() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/Test2.uml";
        File constraint = new File("/Users/hammad/git/EsOCL/src/main/resources/constraints/ocltest2-4.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            System.out.println(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
