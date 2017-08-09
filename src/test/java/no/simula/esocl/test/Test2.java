package no.simula.esocl.test;


import no.simula.esocl.ocl.distance.Result;
import no.simula.esocl.solver.OCLSolver;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test2 {
    @Test
    public void test1() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-1.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test2() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-2.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test3() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-3.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test4() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-4.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test5() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-5.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test6() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-6.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test7() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-7.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test8() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-8.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test9() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-9.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test10() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-10.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test11() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-11.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test12() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-12.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test13() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-13.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test14() {

        String inputModelPath = "src/main/resources/model/Test2.uml";
        File constraint = new File("src/main/resources/constraints/ocltest2-14.ocl");
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"OpOEA"}, 5000);
            oclSolver.printResults(result);
            Assert.assertTrue(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
