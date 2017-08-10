/* ****************************************************************************
 * Copyright (c) 2017 Simula Research Laboratory AS.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Shaukat Ali  shaukat@simula.no
 **************************************************************************** */

package no.simula.esocl.ocl.distance;

import no.simula.esocl.oclga.Search;
import no.simula.esocl.standalone.analysis.BoundValueOCLExpUtility;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */

public class SearchRunner {
    private static Logger logger = Logger.getLogger(SearchRunner.class);

    private static int boundValueStratergy = 0;

    public Result runSearch(SolveProblem problem, Search search) throws Exception {

        if (boundValueStratergy == 0) {
            Result result = searchProcess(problem, search);
            // This class will store the final model instance
            DisplayResult.resultList = new ArrayList<>();
            DisplayResult.resultList.add(problem.getUmlModelInsGenerator().getUmlObjectInsList());

            return result;
        } else {
            /*
             * if we confirm the number of comparison expression, we can calculate the times for
             * running the search process
             */
            int iterations = BoundValueOCLExpUtility.INSTANCE.buildIndexArray4Bound(problem.getConstraint());
            // it stores the type information of bound value for each comparison expression
            DisplayResult.boundValueTypes = BoundValueOCLExpUtility.INSTANCE.getTypeArray();
            DisplayResult.resultList = new ArrayList<>();


            List<Result> results = new ArrayList<>();
            for (int i = 0; i < iterations; i++) {
                // modify the right part value of comparison expression
                BoundValueOCLExpUtility.INSTANCE.generateBoundValue(i);

                results.add(searchProcess(problem, search));
                // restore the right part value of comparison expression
                BoundValueOCLExpUtility.INSTANCE.restoreOriginalValue();
                DisplayResult.resultList.add(problem.getUmlModelInsGenerator().getUmlObjectInsList());
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
        result.setStringSolutions(xp.getStringInstances());

        if (!result.getStringSolutions().isEmpty()) {
            result.setStringSolution(result.getStringSolutions().get(result.getStringSolutions().size() - 1));
        }


        result.setSolutionObjects(xp.getObjects());
        if (!result.getSolutionObjects().isEmpty()) {
            result.setSolutionObject(result.getSolutionObjects().get(result.getSolutionObjects().size() - 1));
        }


        return result;
    }


}
