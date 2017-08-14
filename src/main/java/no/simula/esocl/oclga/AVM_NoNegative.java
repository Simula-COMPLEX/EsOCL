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
public class AVM_NoNegative extends Search {
    double fitness = 0d;

    @Override
    public String[] getSolution(Problem problem) {
        Individual best = Individual.getRandomIndividual(problem);
        best.evaluate();
        this.increaseIteration();

        if (best.getFitnessValue() == 0d)
            return best.getProblem().decoding(best.getValues());

        //init first generation
        while (!this.isStoppingCriterionFulfilled()) {
            Individual current = Individual.getRandomIndividual(problem);
            current.evaluate();
            this.increaseIteration();

            if (current.getFitnessValue() == 0d)
                return current.getProblem().decoding(current.getValues());

            applyAVMSearch(current);

            if (current.getFitnessValue() == 0d)
                return current.getProblem().decoding(current.getValues());

            if (current.getFitnessValue() < best.getFitnessValue()) {
                best.copyDataFrom(current);
                reportImprovement();
            }
        }

        fitness = best.getFitnessValue();
        return best.getProblem().decoding(best.getValues());
    }

    private void applyAVMSearch(Individual ind) {
        ArrayList<GeneValueScope> cons = ind.getEecodedConstraints();
        final int[] directions = new int[]{-1, +1};

        int last_improvement_index = -1;

        while (!this.isStoppingCriterionFulfilled()) {
            loop_on_variables:
            for (int i = 0; i < ind.getValues().length; i++) {
                int index = (last_improvement_index + 1 + i) % ind.getValues().length;

                //----------------------------------------------
                if (cons.get(index).getType() == Problem.CATEGORICAL_TYPE) {
                    //try all the other values
                    System.out.println("CATEGORICAL_TYPE" + ind.getValues()[index]);
                    int current_value = ind.getValues()[index];
                    double current_fitness = ind.getFitnessValue();

                    boolean improved = false;

                    loop_on_values:
                    for (int value = cons.get(index).getMinValue();
                         value <= cons.get(index).getMaxValue();
                         value++) {
                        if (value == current_value)
                            continue;

                        ind.getValues()[index] = value;

                        ind.evaluate();
                        this.increaseIteration();

                        if (ind.getFitnessValue() == 0d)
                            return;

                        //is it better?
                        if (ind.getFitnessValue() < current_fitness) {
                            improved = true;
                            break loop_on_values;
                        } else {
                            //undo the change
                            ind.setFitnessValue(current_fitness);
                            ind.getValues()[index] = current_value;
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
                            double current_fitness = ind.getFitnessValue();

                            int valueTmp = ind.getValues()[index] + d;

                            if (valueTmp > cons.get(index).getMinValue() &&
                                    valueTmp < cons.get(index).getMaxValue()) {
                                ind.getValues()[index] = valueTmp;
                                ind.setFitnessValue(current_fitness);
                            }

                            ind.evaluate();
                            this.increaseIteration();

                            if (ind.getFitnessValue() == 0d)
                                return;

                            //is it better?
                            if (ind.getFitnessValue() < current_fitness) {
                                current_fitness = ind.getFitnessValue();
                                improved = true;
                            } else {
                                //undo change
                                int valueTmp4 = ind.getValues()[index] - d;

                                if (valueTmp4 > cons.get(index).getMinValue() &&
                                        valueTmp4 < cons.get(index).getMaxValue()) {
                                    ind.getValues()[index] = valueTmp4;
                                    ind.setFitnessValue(current_fitness);
                                }
                            }

                            if (improved) {
                                //exploration in one direction was successful

                                int delta = 2;

                                while (!this.isStoppingCriterionFulfilled()) {
                                    current_fitness = ind.getFitnessValue();

                                    int valueTmp1 = ind.getValues()[index] + (d * delta);

                                    if (valueTmp1 > cons.get(index).getMinValue() &&
                                            valueTmp1 < cons.get(index).getMaxValue()) {
                                        ind.getValues()[index] = ind.getValues()[index] + (d * delta);
                                        System.out.println("@@@@@@@@@@@@@@@" + ind.getValues()[index]);
                                    }
                                    ind.evaluate();
                                    this.increaseIteration();

                                    if (ind.getFitnessValue() == 0d)
                                        return;

                                    //is it better?
                                    if (ind.getFitnessValue() < current_fitness) {
                                        delta = delta * 2;
                                    } else {
                                        //undo change
                                        int valueTmp3 = ind.getValues()[index] - (d * delta);

                                        System.out.println("!!!!!!!!!!!!!!" + valueTmp3);
                                        if (valueTmp3 > cons.get(index).getMinValue() &&
                                                valueTmp3 < cons.get(index).getMaxValue()) {
                                            ind.getValues()[index] = valueTmp3;
                                            ind.setFitnessValue(current_fitness);
                                            break;
                                        }
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
