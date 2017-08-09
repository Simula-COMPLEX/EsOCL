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
public class RandomSearch extends Search {
    private double fitness = 0d;

    @Override
    public String[] getSolution(Problem problem) {
        Individual best = Individual.getRandomIndividual(problem);
        best.evaluate();
        this.increaseIteration();

        if (best.getFitness_value() == 0d)
            return best.getProblem().decoding(best.getV());


        //init first generation
        while (!this.isStoppingCriterionFulfilled()) {
            Individual current = Individual.getRandomIndividual(problem);
            current.evaluate();
            this.increaseIteration();

            if (current.getFitness_value() == 0d) {
                fitness = current.getFitness_value();
                return current.getProblem().decoding(current.getV());
            }

            if (current.getFitness_value() < best.getFitness_value()) {
                best.copyDataFrom(current);
                reportImprovement();
            }
        }

        fitness = best.getFitness_value();
        return best.getProblem().decoding(best.getV());
    }

    @Override
    public String getShortName() {
        return "RS";
    }

    @Override
    public double getFitness() {
        return fitness;
    }
}
