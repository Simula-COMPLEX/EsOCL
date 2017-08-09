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

import java.util.ArrayList;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class AVM extends Search {
    private double fitness = 0d;

    @Override
    public String[] getSolution(Problem problem) {
        Individual best = Individual.getRandomIndividual(problem);
        best.evaluate();
        this.increaseIteration();

        if (best.getFitness_value() == 0d)
            return best.getProblem().decoding(removeNegativeValue(best).getV());

        //init first generation
        while (!this.isStoppingCriterionFulfilled()) {
            Individual current = Individual.getRandomIndividual(problem);
            current.evaluate();
            this.increaseIteration();

            if (current.getFitness_value() == 0d)
                return current.getProblem().decoding(removeNegativeValue(current).getV());

            applyAVMSearch(current);

            if (current.getFitness_value() == 0d)
                return current.getProblem().decoding(removeNegativeValue(current).getV());

            if (current.getFitness_value() < best.getFitness_value()) {
                best.copyDataFrom(current);
                reportImprovement();
            }
        }
        fitness = best.getFitness_value();

        return best.getProblem().decoding(removeNegativeValue(best).getV());
    }

    private Individual removeNegativeValue(Individual best) {
        ArrayList<GeneValueScope> cons =
                best.getEecodedConstraints();

        for (int i = 0; i < best.getV().length; i++) {
            if (best.getV()[i] < cons.get(i).getMinValue())
                best.getV()[i] = cons.get(i).getMinValue();
            else if (best.getV()[i] > cons.get(i).getMaxValue())
                best.getV()[i] = cons.get(i).getMaxValue();
        }

        return best;
    }

    private void applyAVMSearch(Individual ind) {
        ArrayList<GeneValueScope> cons = ind.getEecodedConstraints();
        final int[] directions = new int[]{-1, +1};

        int last_improvement_index = -1;

        while (!this.isStoppingCriterionFulfilled()) {
            loop_on_variables:
            for (int i = 0; i < ind.getV().length; i++) {
                int index = (last_improvement_index + 1 + i) % ind.getV().length;

                //----------------------------------------------
                if (cons.get(index).getType() == Problem.CATEGORICAL_TYPE) {
                    //try all the other values

                    int current_value = ind.getV()[index];
                    double current_fitness = ind.getFitness_value();

                    boolean improved = false;

                    loop_on_values:
                    for (int value = cons.get(index).getMinValue();
                         value <= cons.get(index).getMaxValue();
                         value++) {
                        if (value == current_value)
                            continue;

                        ind.getV()[index] = value;

                        ind.evaluate();
                        this.increaseIteration();

                        if (ind.getFitness_value() == 0d)
                            return;

                        //is it better?
                        if (ind.getFitness_value() < current_fitness) {
                            improved = true;
                            break loop_on_values;
                        } else {
                            //undo the change
                            ind.setFitness_value(current_fitness);
                            ind.getV()[index] = current_value;
                        }
                    }


                    //it was possible to improve fitness by changing the value at v[index]
                    if (improved) {
                        last_improvement_index = index;
                        break loop_on_variables;
                    }
                }
                //----------------------------------------------
                else //Numerical
                {
                    boolean improved = true;

                    while (improved) {
                        improved = false;

                        for (int d : directions) {
                            //exploratory search
                            double current_fitness = ind.getFitness_value();

                            ind.getV()[index] = ind.getV()[index] + d;

                            ind.evaluate();
                            this.increaseIteration();

                            if (ind.getFitness_value() == 0d)
                                return;

                            //is it better?
                            if (ind.getFitness_value() < current_fitness) {
                                current_fitness = ind.getFitness_value();
                                improved = true;
                            } else {
                                //undo change
                                ind.getV()[index] = ind.getV()[index] - d;
                                ind.setFitness_value(current_fitness);
                            }

                            if (improved) {
                                //exploration in one direction was successful

                                int delta = 2;

                                while (!this.isStoppingCriterionFulfilled()) {
                                    current_fitness = ind.getFitness_value();

                                    ind.getV()[index] = ind.getV()[index] + (d * delta);

                                    ind.evaluate();
                                    this.increaseIteration();

                                    if (ind.getFitness_value() == 0d)
                                        return;

                                    //is it better?
                                    if (ind.getFitness_value() < current_fitness) {
                                        delta = delta * 2;
                                    } else {
                                        //undo change
                                        ind.getV()[index] = ind.getV()[index] - (d * delta);

                                        ind.setFitness_value(current_fitness);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public String getShortName() {
        return "AVM";
    }

    @Override
    public double getFitness() {
        return fitness;
    }

}
