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
    private  double fitness = 0d;

    @Override
    public String[] getSolution(Problem problem) {
        Individual best = Individual.getRandomIndividual(problem);
        best.evaluate();
        this.increaseIteration();

        if (best.fitness_value == 0d)
            return best.problem.decoding(best.v);


        //init first generation
        while (!this.isStoppingCriterionFulfilled()) {
            Individual current = Individual.getRandomIndividual(problem);
            current.evaluate();
            this.increaseIteration();

            if (current.fitness_value == 0d) {
                fitness = current.fitness_value;
                return current.problem.decoding(current.v);
            }

            if (current.fitness_value < best.fitness_value) {
                best.copyDataFrom(current);
                reportImprovement();
            }
        }

        fitness = best.fitness_value;
        return best.problem.decoding(best.v);
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
