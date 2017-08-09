package no.simula.esocl.ocl.distance;

import no.simula.esocl.oclga.Search;
import no.simula.esocl.standalone.analysis.OCLExpUtility;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SearchRunner {
    private static int boundValueStratergy = 0;
    private static Logger logger = Logger.getLogger(SearchRunner.class);


    public Result runSearch(SolveProblem problem, Search search) throws Exception {

        problem.processProblem();

        if (boundValueStratergy == 0) {
            Result result = searchProcess(problem, search);
            // This class will store the final model instance
            DisplayResult.resultList = new ArrayList<>();
            DisplayResult.resultList.add(problem.getUmlModelInsGenerator()
                    .getUmlObjectInsList());

            return result;
        } else {
            /*
             * if we confirm the number of comparison expression, we can calculate the times for
             * running the search process
             */
            int iterations = OCLExpUtility.INSTANCE.buildIndexArray4Bound(problem
                    .getConstraint());
            // it stores the type information of bound value for each comparison expression
            DisplayResult.boundValueTypes = OCLExpUtility.INSTANCE
                    .getTypeArray();
            DisplayResult.resultList = new ArrayList<>();


            List<Result> results = new ArrayList<>();
            for (int i = 0; i < iterations; i++) {
                // modify the right part value of comparison expression
                OCLExpUtility.INSTANCE.generateBoundValue(i);

                results.add(searchProcess(problem, search));

                // restore the right part value of comparison expression
                OCLExpUtility.INSTANCE.restoreOriginalValue();
                DisplayResult.resultList.add(problem.getUmlModelInsGenerator()
                        .getUmlObjectInsList());
            }


            for (Result result : results) {
                if (result.getResult()) {
                    return result;
                }
            }


        }


        return null;
    }

    private Result searchProcess(SolveProblem xp, Search sv) {


        Search.validateConstraints(xp);
        String[] value = sv.search(xp);

        for (String str : value) {
            logger.debug(str);
        }

        boolean found = sv.getFitness() == 0d;


        if (!found) {
            found = xp.getFitness(value) == 0d;
        }

        int steps = sv.getIteration();


        Result result = new Result();
        if (xp.getConstraint() != null) {
            result.setConstraint(xp.getConstraint().getSpecification().getBody());
        }
        result.setResult(found);
        result.setSearchAlgorithms(sv.getShortName());
        result.setIterations(steps);
        result.setSolutions(xp.getSolutions());
        if (!result.getSolutions().isEmpty()) {
            result.setSolution(result.getSolutions().get(result.getSolutions().size() - 1));
        }


        result.setSolutionObjects(xp.getObjects());
        if (!result.getSolutionObjects().isEmpty()) {
            result.setFinalSolutionObject(result.getSolutionObjects().get(result.getSolutionObjects().size() - 1));
        }


        return result;
    }


}
