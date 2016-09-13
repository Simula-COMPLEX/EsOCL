package no.simula.esocl.oclga;

import java.util.ArrayList;


public class AVM_NoNegative extends Search {

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

            if (current.fitness_value == 0d)
                return current.problem.decoding(current.v);

            applyAVMSearch(current);

            if (current.fitness_value == 0d)
                return current.problem.decoding(current.v);

            if (current.fitness_value < best.fitness_value) {
                best.copyDataFrom(current);
                reportImprovement();
            }
        }

        return best.problem.decoding(best.v);
    }

    protected void applyAVMSearch(Individual ind) {
        ArrayList<GeneValueScope> cons = ind.getEecodedConstraints();
        final int[] directions = new int[]{-1, +1};

        int last_improvement_index = -1;

        while (!this.isStoppingCriterionFulfilled()) {
            loop_on_variables:
            for (int i = 0; i < ind.v.length; i++) {
                int index = (last_improvement_index + 1 + i) % ind.v.length;

                //----------------------------------------------
                if (cons.get(index).getType() == Problem.CATEGORICAL_TYPE) {
                    //try all the other values
                    System.out.println("CATEGORICAL_TYPE" + ind.v[index]);
                    int current_value = ind.v[index];
                    double current_fitness = ind.fitness_value;

                    boolean improved = false;

                    loop_on_values:
                    for (int value = cons.get(index).getMinValue();
                         value <= cons.get(index).getMaxValue();
                         value++) {
                        if (value == current_value)
                            continue;

                        ind.v[index] = value;

                        ind.evaluate();
                        this.increaseIteration();

                        if (ind.fitness_value == 0d)
                            return;

                        //is it better?
                        if (ind.fitness_value < current_fitness) {
                            improved = true;
                            break loop_on_values;
                        } else {
                            //undo the change
                            ind.fitness_value = current_fitness;
                            ind.v[index] = current_value;
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
                            double current_fitness = ind.fitness_value;

                            int valueTmp = ind.v[index] + d;

                            if (valueTmp > cons.get(index).getMinValue() &&
                                    valueTmp < cons.get(index).getMaxValue()) {
                                ind.v[index] = valueTmp;
                                ind.fitness_value = current_fitness;
                            }

                            ind.evaluate();
                            this.increaseIteration();

                            if (ind.fitness_value == 0d)
                                return;

                            //is it better?
                            if (ind.fitness_value < current_fitness) {
                                current_fitness = ind.fitness_value;
                                improved = true;
                            } else {
                                //undo change
                                int valueTmp4 = ind.v[index] - d;

                                if (valueTmp4 > cons.get(index).getMinValue() &&
                                        valueTmp4 < cons.get(index).getMaxValue()) {
                                    ind.v[index] = valueTmp4;
                                    ind.fitness_value = current_fitness;
                                }
                            }

                            if (improved) {
                                //exploration in one direction was successful

                                int delta = 2;

                                while (!this.isStoppingCriterionFulfilled()) {
                                    current_fitness = ind.fitness_value;

                                    int valueTmp1 = ind.v[index] + (d * delta);

                                    if (valueTmp1 > cons.get(index).getMinValue() &&
                                            valueTmp1 < cons.get(index).getMaxValue()) {
                                        ind.v[index] = ind.v[index] + (d * delta);
                                        System.out.println("@@@@@@@@@@@@@@@" + ind.v[index]);
                                    }
                                    ind.evaluate();
                                    this.increaseIteration();

                                    if (ind.fitness_value == 0d)
                                        return;

                                    //is it better?
                                    if (ind.fitness_value < current_fitness) {
                                        delta = delta * 2;
                                    } else {
                                        //undo change
                                        int valueTmp3 = ind.v[index] - (d * delta);

                                        System.out.println("!!!!!!!!!!!!!!" + valueTmp3);
                                        if (valueTmp3 > cons.get(index).getMinValue() &&
                                                valueTmp3 < cons.get(index).getMaxValue()) {
                                            ind.v[index] = valueTmp3;
                                            ind.fitness_value = current_fitness;
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
}
