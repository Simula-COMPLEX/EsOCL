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

public class Individual implements Comparable<Individual> {

    /**
     * Array is used to store the value of the constraints, this value is generated according to the array valueofconstraints[][]
     * This array is also used to represent the chromosome
     */
    private int[] v;
    private double fitness_value;
    private Problem problem;

    public Individual(Problem p) {
        problem = p;
    }

    /*
     * Random generate the individual according to the problem
     * Invoke the RandomGenerator function to generate the corresponding number
     */
    public static Individual getRandomIndividual(Problem p) {
        Individual ind = new Individual(p);
        randomGenerateValue(ind);
        ind.evaluate();
        return ind;
    }


    private static void randomGenerateValue(Individual ind) {
        //compute the length of individual
        ind.v = new int[ind.problem.getGeneConstraints().size()];

        for (int i = 0; i < ind.problem.getGeneConstraints().size(); i++) {
            GeneValueScope geneValue = ind.problem.getGeneConstraints().get(i);
            ind.v[i] = randomGenerateValue(geneValue);
        }

    }

    /*
    0 represent types of int
    1 represent types of boolean and enumeration
    2 represent types of string
    3 represent types of double
    */
    public static int randomGenerateValue(GeneValueScope constraint) {
        int value;
        int min = constraint.getMinValue();
        int max = constraint.getMaxValue();
        int type = constraint.getType();

        int scople = max - min;
        int k = RandomGenerator.getGenerator().nextInt(scople + 1);
        value = min + k;

        return value;
    }

    public int compareTo(Individual other) {
        return Double.compare(this.fitness_value, other.fitness_value);
    }

    public void evaluate() {

        fitness_value = problem.getFitness(problem.decoding(this.v));
    }

    public ArrayList<GeneValueScope> getEecodedConstraints() {
        return this.problem.getGeneConstraints();
    }


    public void copyDataFrom(Individual other) {
        this.problem = other.problem;

        System.arraycopy(other.v, 0, this.v, 0, v.length);

        this.fitness_value = other.fitness_value;
    }


    public int[] getV() {
        return v;
    }

    public void setV(int[] v) {
        this.v = v;
    }

    public double getFitness_value() {
        return fitness_value;
    }

    public void setFitness_value(double fitness_value) {
        this.fitness_value = fitness_value;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}