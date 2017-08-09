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

            if (population[i].getFitness_value() < best) {
                best = population[i].getFitness_value();
                reportImprovement();

                if (best == 0d) {
                    fitness = best;
                    return population[i].getProblem().decoding(population[i].getV());
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

            for (int i = 0; i < father.getV().length; i++) {
                //System.out.print(", "+father.getV()[i]);
            }

            //	System.out.println("**************** Mother: "+ mother );
            for (int i = 0; i < father.getV().length; i++) {
                //System.out.print(", "+mother.getV()[i]);
            }

            Individual[] offspring = xover(father, mother);

            mutateAndEvaluateOffspring(offspring[0]);
            if (offspring[0].getFitness_value() == 0d) {
                fitness = offspring[0].getFitness_value();
                return offspring[0].getProblem().decoding(offspring[0].getV());
            }

            mutateAndEvaluateOffspring(offspring[1]);
            if (offspring[1].getFitness_value() == 0d) {
                fitness = offspring[0].getFitness_value();
                return offspring[1].getProblem().decoding(offspring[1].getV());
            }

            double off_min = Math.min(offspring[0].getFitness_value(), offspring[1].getFitness_value());
            double par_min = Math.min(father.getFitness_value(), mother.getFitness_value());

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

        fitness = population[0].getFitness_value();

        //return the best, ie minimal similarity
        Arrays.sort(population);
        return population[0].getProblem().decoding(population[0].getV());
    }

    public void mutateAndEvaluateOffspring(Individual ind) {
        ArrayList<GeneValueScope> cons = ind.getEecodedConstraints();

        final double p = 1d / (double) ind.getV().length;

        for (int i = 0; i < ind.getV().length; i++) {
            if (p >= RandomGenerator.getGenerator().nextDouble()) {
                final int min = cons.get(i).getMinValue();
                final int max = cons.get(i).getMaxValue();
                final int dif = max - min;

                if (cons.get(i).getType() == Problem.CATEGORICAL_TYPE) {
                    ind.getV()[i] = min + RandomGenerator.getGenerator().nextInt(dif + 1);
                } else {
                    final double step = (double) dif / 100d; // 1%
                    int sign = RandomGenerator.getGenerator().nextBoolean() ? -1 : 1;

                    final int delta = sign * (1 + (int) (step * RandomGenerator.getGenerator().nextDouble()));

                    int k = ind.getV()[i] + delta;

                    if (k > max)
                        k = max;
                    if (k < min)
                        k = min;

                    ind.getV()[i] = k;
                }
            }
        }

        ind.evaluate();
        this.increaseIteration();
    }

    protected Individual[] xover(Individual a, Individual b) {
        Individual[] offspring = new Individual[]{new Individual(a.getProblem()), new Individual(b.getProblem())};

        offspring[0].setV(new int[a.getV().length]);
        offspring[1].setV(new int[b.getV().length]);

        //k is the splitting point. it cannot be the first, because otherwise the offspring
        //d be copies of the parents
        int k;

        //shall we apply xover?
        //	System.out.print("************"+offspring[0].getV().length);

        if (RandomGenerator.getGenerator().nextDouble() < xover_rate)
            k = 1 + RandomGenerator.getGenerator().nextInt(offspring[0].getV().length);
        else
            k = offspring[0].getV().length; // that means no splitting point

        for (int i = 0; i < k; i++) {
            offspring[0].getV()[i] = a.getV()[i];
            offspring[1].getV()[i] = b.getV()[i];
        }

        for (int i = k; i < offspring[0].getV().length; i++) {
            //note the inverted order
            offspring[0].getV()[i] = b.getV()[i];
            offspring[1].getV()[i] = a.getV()[i];
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