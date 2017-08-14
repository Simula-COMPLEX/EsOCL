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

        if (current.getFitnessValue() == 0d)
            return current.getProblem().decoding(current.getValues());

        Individual tmp = Individual.getRandomIndividual(problem);

        //init first generation
        while (!this.isStoppingCriterionFulfilled()) {
            tmp.copyDataFrom(current);
            mutateAndEvaluateOffspring(tmp);

            if (tmp.getFitnessValue() == 0d)
                return tmp.getProblem().decoding(tmp.getValues());

            if (tmp.getFitnessValue() <= current.getFitnessValue()) {
                current.copyDataFrom(tmp);

                if (tmp.getFitnessValue() < current.getFitnessValue())
                    reportImprovement();
            }
        }

        fitness = current.getFitnessValue();
        return current.getProblem().decoding(current.getValues());
    }

    @Override
    public String getShortName() {
        return "OpOEA";
    }
}
