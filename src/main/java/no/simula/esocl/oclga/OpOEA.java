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

package no.simula.esocl.oclga;


/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class OpOEA extends SSGA {
    public OpOEA() {
    }

    @Override
    public String[] getSolution(Problem problem) {
        Individual current = Individual.getRandomIndividual(problem);
        current.evaluate();
        this.increaseIteration();

        if (current.fitness_value == 0d)
            return current.problem.decoding(current.v);

        Individual tmp = Individual.getRandomIndividual(problem);

        //init first generation
        while (!this.isStoppingCriterionFulfilled()) {
            tmp.copyDataFrom(current);
            mutateAndEvaluateOffspring(tmp);

            if (tmp.fitness_value == 0d)
                return tmp.problem.decoding(tmp.v);

            if (tmp.fitness_value <= current.fitness_value) {
                current.copyDataFrom(tmp);

                if (tmp.fitness_value < current.fitness_value)
                    reportImprovement();
            }
        }

        fitness = current.fitness_value;
        return current.problem.decoding(current.v);
    }

    @Override
    public String getShortName() {
        return "OpOEA";
    }
}
