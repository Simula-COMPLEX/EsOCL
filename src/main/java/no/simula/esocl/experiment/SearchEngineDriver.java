package no.simula.esocl.experiment;

import no.simula.esocl.ocl.distance.DisplayResult;
import no.simula.esocl.ocl.distance.SolveProblem;
import no.simula.esocl.ocl.distance.ValueElement4Search;
import no.simula.esocl.oclga.*;
import no.simula.esocl.standalone.analysis.OCLExpUtility;
import no.simula.esocl.standalone.analysis.Utility;
import no.simula.esocl.standalone.modelinstance.UMLObjectIns;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchEngineDriver {
    public static int boundValueStratergy = 0;
    static Logger logger = Logger.getLogger(SearchEngineDriver.class);
    String inputModelPath;
    File inputOclConstraintsPath;
    String constraint;
    int searchAlgorithm = 0;
    Integer iterations;
    Search[] s = new Search[]{new AVM(),
            new SSGA(100, 0.75), new OpOEA(),
            new RandomSearch()};

    public SearchEngineDriver() {
        OCLExpUtility.INSTANCE = new OCLExpUtility();
        Utility.INSTANCE = new Utility();
        DisplayResult.boundValueTypes = null;
        DisplayResult.resultList = null;
    }


    public Result solveConstraint(String inputModel, File constraint, int searchAlgorithm, int boundValueStratergy, Integer iterations) throws Exception {
        this.inputModelPath = inputModel;
        this.searchAlgorithm = searchAlgorithm;
        this.boundValueStratergy = boundValueStratergy;
        this.inputOclConstraintsPath = constraint;
        this.iterations = iterations;

        String[] inputProfilePaths = {};
        SolveProblem xp = new SolveProblem(inputModelPath,
                inputProfilePaths, constraint);
        return runSearch(xp);
    }

    public Result solveConstraint(String inputModel, String constraint, int searchAlgorithm, int boundValueStratergy, Integer iterations) throws Exception {
        this.inputModelPath = inputModel;
        this.searchAlgorithm = searchAlgorithm;
        this.boundValueStratergy = boundValueStratergy;
        this.constraint = constraint;
        this.iterations = iterations;

        String[] inputProfilePaths = {};
        SolveProblem xp = new SolveProblem(inputModelPath,
                inputProfilePaths, constraint);
        return runSearch(xp);
    }


    /**
     * for kunming
     *
     * @param assgnedValue4Attribute
     * @param OptimizedValueofAttributes
     * @return
     */
    public boolean getOptimizedValueofAttributes(SolveProblem xp,
                                                 ValueElement4Search[] assgnedValue4Attribute,
                                                 ValueElement4Search[] OptimizedValueofAttributes) {
        boolean isSolved = false;
        String[] inputProfilePaths = {};
        xp.processProblem(assgnedValue4Attribute, OptimizedValueofAttributes);
        if (this.boundValueStratergy == 0) {
            isSolved = searchProcess(xp).getResult();
            DisplayResult.resultList = new ArrayList<List<UMLObjectIns>>();
            DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
                    .getUmlObjectInsList());
        } else {
            int iterateTime = OCLExpUtility.INSTANCE.buildIndexArray4Bound(xp
                    .getConstraint());
            DisplayResult.boundValueTypes = OCLExpUtility.INSTANCE
                    .getTypeArray();
            DisplayResult.resultList = new ArrayList<List<UMLObjectIns>>();
            for (int i = 0; i < iterateTime; i++) {
                OCLExpUtility.INSTANCE.generateBoundValue(i);
                isSolved = searchProcess(xp).getResult();
                OCLExpUtility.INSTANCE.restoreOriginalValue();
                DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
                        .getUmlObjectInsList());
            }
        }
        xp.getAssignVlue();
        return isSolved;
    }

    public Result runSearch(SolveProblem xp) throws Exception {
        Result result = null;

        xp.processProblem();
        if (this.boundValueStratergy == 0) {
            result = searchProcess(xp);
            // This class will store the final model instance
            DisplayResult.resultList = new ArrayList<List<UMLObjectIns>>();
            DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
                    .getUmlObjectInsList());
        } else {
            /**
             * if we confirm the number of comparison expression, we can calculate the times for
             * running the search process
             */
            int iterateTime = OCLExpUtility.INSTANCE.buildIndexArray4Bound(xp
                    .getConstraint());
            // it stores the type information of bound value for each comparison expression
            DisplayResult.boundValueTypes = OCLExpUtility.INSTANCE
                    .getTypeArray();
            DisplayResult.resultList = new ArrayList<List<UMLObjectIns>>();
            for (int i = 0; i < iterateTime; i++) {
                // modify the right part value of comparison expression
                OCLExpUtility.INSTANCE.generateBoundValue(i);
                result = searchProcess(xp);
                // restore the right part value of comparison expression
                OCLExpUtility.INSTANCE.restoreOriginalValue();
                DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
                        .getUmlObjectInsList());
            }

        }


        return result;
    }

    public Result searchProcess(SolveProblem xp) {
        Search sv = s[this.searchAlgorithm];
        sv.setMaxIterations(iterations);
        Search.validateConstraints(xp);
        String[] value = sv.search(xp);

        for (String str : value) {
            logger.debug(str);
        }

        boolean found = xp.getFitness(value) == 0d;

        int steps = sv.getIteration();
        List<String> solutions = xp.getSolutions();

        Result result = new Result();
        result.setResult(found);
        result.setAlgo(sv.getShortName());
        result.setIternations(steps);
        result.setSolutions(solutions);
        if (!solutions.isEmpty()) {
            result.setSolution(solutions.get(solutions.size() - 1));
        }

        logger.debug("Constraint: "+constraint);
        logger.debug("Algo: " + result.getAlgo());
        logger.debug("Result: " + result.getResult());
        logger.debug("Iterations:" + result.getIternations());
        logger.debug("Solution: " + result.getSolution());



        System.out.println("Constraint: "+constraint);
        System.out.println("Algo: " + result.getAlgo());
        System.out.println("Result: " + result.getResult());
        System.out.println("Iterations:" + result.getIternations());
        if (result.getSolution()!=null){
            System.out.println("Solution: ");
            for (String solution:result.getSolution().split(",")) {
                System.out.println(solution);
            }
        }



        return result;
    }
}
