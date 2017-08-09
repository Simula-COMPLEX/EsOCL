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

        if (current.getFitness_value() == 0d)
            return current.getProblem().decoding(current.getV());

        Individual tmp = Individual.getRandomIndividual(problem);

        //init first generation
        while (!this.isStoppingCriterionFulfilled()) {
            tmp.copyDataFrom(current);
            mutateAndEvaluateOffspring(tmp);

            if (tmp.getFitness_value() == 0d)
                return tmp.getProblem().decoding(tmp.getV());

            if (tmp.getFitness_value() <= current.getFitness_value()) {
                current.copyDataFrom(tmp);

                if (tmp.getFitness_value() < current.getFitness_value())
                    reportImprovement();
            }
        }

        fitness = current.getFitness_value();
        return current.getProblem().decoding(current.getV());
    }

    @Override
    public String getShortName() {
        return "OpOEA";
    }
}
