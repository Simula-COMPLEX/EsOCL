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
import java.util.Arrays;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class SSGA extends Search {
    double fitness = 0d;
    private int population_size;
    private double xover_rate;

    public SSGA() {
        population_size = 100;
        xover_rate = 0.75;
    }

    public SSGA(int population_size, double xover_rate) {
        this.population_size = population_size;
        this.xover_rate = xover_rate;
    }

    public static int rankSelectionIndex(double rank_bias, int length) {
        //see Whitley
        //http://citeseer.ist.psu.edu/whitley89genitor.html

        double r = RandomGenerator.getGenerator().nextDouble();
        double d = rank_bias - Math.sqrt((rank_bias * rank_bias) - (4.0 * (rank_bias - 1.0) * r));
        d = d / 2.0 / (rank_bias - 1.0);

        //d = 1.0 - d; // to do that if we want to have Maximisation

        int index = (int) (length * d);
        return index;
    }

    public String[] getSolution(Problem problem) {
        double best = Double.MAX_VALUE;

        Individual[] population = new Individual[population_size];

        //init first generation
        for (int i = 0; i < population.length && !this.isStoppingCriterionFulfilled(); i++) {
            population[i] = Individual.getRandomIndividual(problem);
            this.increaseIteration();

            if (population[i].getFitnessValue() < best) {
                best = population[i].getFitnessValue();
                reportImprovement();

                if (best == 0d) {
                    fitness = best;
                    return population[i].getProblem().decoding(population[i].getValues());
                }
            }
        }


        while (!this.isStoppingCriterionFulfilled()) {
            //for rank selection, I need the population sorted
            Arrays.sort(population);

            int x = rankSelectionIndex(1.5, population.length);
            int y = rankSelectionIndex(1.5, population.length);

            //avoid to pick up same parents
            if (y == x) {
                if (y == population.length - 1)
                    y--;
                else
                    y++;
            }

            Individual father = population[x];
            Individual mother = population[y];

            //generate and mutate offspring


            //	System.out.println("**************** Father: "+ father );

            for (int i = 0; i < father.getValues().length; i++) {
                //System.out.print(", "+father.getValues()[i]);
            }

            //	System.out.println("**************** Mother: "+ mother );
            for (int i = 0; i < father.getValues().length; i++) {
                //System.out.print(", "+mother.getValues()[i]);
            }

            Individual[] offspring = xover(father, mother);

            mutateAndEvaluateOffspring(offspring[0]);
            if (offspring[0].getFitnessValue() == 0d) {
                fitness = offspring[0].getFitnessValue();
                return offspring[0].getProblem().decoding(offspring[0].getValues());
            }

            mutateAndEvaluateOffspring(offspring[1]);
            if (offspring[1].getFitnessValue() == 0d) {
                fitness = offspring[0].getFitnessValue();
                return offspring[1].getProblem().decoding(offspring[1].getValues());
            }

            double off_min = Math.min(offspring[0].getFitnessValue(), offspring[1].getFitnessValue());
            double par_min = Math.min(father.getFitnessValue(), mother.getFitnessValue());

            //accept new offspring if one of them is better/equal than  the best parent
            if (off_min <= par_min) {
                population[x] = offspring[0];
                population[y] = offspring[1];
            }

            if (off_min < best) {
                best = off_min;
                reportImprovement();
            }
        }

        fitness = population[0].getFitnessValue();

        //return the best, ie minimal similarity
        Arrays.sort(population);
        return population[0].getProblem().decoding(population[0].getValues());
    }

    public void mutateAndEvaluateOffspring(Individual ind) {
        ArrayList<GeneValueScope> cons = ind.getEecodedConstraints();

        final double p = 1d / (double) ind.getValues().length;

        for (int i = 0; i < ind.getValues().length; i++) {
            if (p >= RandomGenerator.getGenerator().nextDouble()) {
                final int min = cons.get(i).getMinValue();
                final int max = cons.get(i).getMaxValue();
                final int dif = max - min;

                if (cons.get(i).getType() == Problem.CATEGORICAL_TYPE) {
                    ind.getValues()[i] = min + RandomGenerator.getGenerator().nextInt(dif + 1);
                } else {
                    final double step = (double) dif / 100d; // 1%
                    int sign = RandomGenerator.getGenerator().nextBoolean() ? -1 : 1;

                    final int delta = sign * (1 + (int) (step * RandomGenerator.getGenerator().nextDouble()));

                    int k = ind.getValues()[i] + delta;

                    if (k > max)
                        k = max;
                    if (k < min)
                        k = min;

                    ind.getValues()[i] = k;
                }
            }
        }

        ind.evaluate();
        this.increaseIteration();
    }

    protected Individual[] xover(Individual a, Individual b) {
        Individual[] offspring = new Individual[]{new Individual(a.getProblem()), new Individual(b.getProblem())};

        offspring[0].setValues(new int[a.getValues().length]);
        offspring[1].setValues(new int[b.getValues().length]);

        //k is the splitting point. it cannot be the first, because otherwise the offspring
        //d be copies of the parents
        int k;

        //shall we apply xover?
        //	System.out.print("************"+offspring[0].getValues().length);

        if (RandomGenerator.getGenerator().nextDouble() < xover_rate)
            k = 1 + RandomGenerator.getGenerator().nextInt(offspring[0].getValues().length);
        else
            k = offspring[0].getValues().length; // that means no splitting point

        for (int i = 0; i < k; i++) {
            offspring[0].getValues()[i] = a.getValues()[i];
            offspring[1].getValues()[i] = b.getValues()[i];
        }

        for (int i = k; i < offspring[0].getValues().length; i++) {
            //note the inverted order
            offspring[0].getValues()[i] = b.getValues()[i];
            offspring[1].getValues()[i] = a.getValues()[i];
        }


        return offspring;
    }

    @Override
    public String getShortName() {
        return "SSGA";
    }

    @Override
    public double getFitness() {
        return fitness;
    }
}