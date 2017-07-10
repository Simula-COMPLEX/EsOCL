package no.simula.esocl.test;


import no.simula.esocl.ocl.distance.Result;
import no.simula.esocl.solver.OCLSolver;
import org.junit.Test;

public class Test4 {
    @Test
    public void test1() {


        System.out.println(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/RoyalAndLoyal.uml";
        String constraint = "context LoyaltyAccount inv:  self.points >  0  implies  self.transactions->exists(t | t.points >  0  ) ";


        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test2() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/RoyalAndLoyal.uml";
        String constraint = "context ProgramPartner inv:  self.deliveredServices->collect(de | de.transactions)->select(tr | tr.oclIsTypeOf(Earning)  ) ->collect(tr | tr.points ) ->sum()  <  10000 ";


        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test3() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/RoyalAndLoyal.uml";
        String constraint = "context Service inv:  self.pointsEarned >  0  implies  not ( self.pointsBurned =  0  ) ";

        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test4() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/RoyalAndLoyal.uml";
        String constraint = "";

        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test5() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/RoyalAndLoyal.uml";
        String constraint = "context ProgramPartner inv:  self.deliveredServices->collect(de | de.transactions)->select(tr | tr.oclIsTypeOf(Earning)  ) ->collect(tr | tr.points ) ->sum()  <  10000 ";

        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test6() {

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/RoyalAndLoyal.uml";
        String constraint = "context Membership inv: self.programs.partners->forAll(p | p.deliveredServices->forAll(d | d.pointsEarned = 0 ) ) implies self.account->isEmpty() = true";

        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test7() {


        System.out.println(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        String inputModelPath = "/Users/hammad/git/EsOCL/src/main/resources/model/RoyalAndLoyal.uml";
        String constraint = "context LoyaltyAccount inv:  self.points >  2  and self.points <  5 ";


        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            oclSolver.printResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
