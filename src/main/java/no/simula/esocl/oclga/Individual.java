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
    private int[] values;
    private double fitnessValue;
    private Problem problem;

    public Individual(Problem p) {
        problem = p;
    }

    /*
     * Random generate the individual according to the problem
     * Invoke the RandomGenerator function to generate the corresponding number
     */
    public static Individual getRandomIndividual(Problem p) {
        Individual individual = new Individual(p);
        randomGenerateValue(individual);
        individual.evaluate();
        return individual;
    }


    private static void randomGenerateValue(Individual ind) {
        //compute the length of individual
        ind.values = new int[ind.problem.getGeneConstraints().size()];

        for (int i = 0; i < ind.problem.getGeneConstraints().size(); i++) {
            GeneValueScope geneValue = ind.problem.getGeneConstraints().get(i);
            ind.values[i] = randomGenerateValue(geneValue);
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
        return Double.compare(this.fitnessValue, other.fitnessValue);
    }

    public void evaluate() {

        fitnessValue = problem.getFitness(problem.decoding(this.values));
    }

    public ArrayList<GeneValueScope> getEecodedConstraints() {
        return this.problem.getGeneConstraints();
    }


    public void copyDataFrom(Individual other) {
        this.problem = other.problem;

        System.arraycopy(other.values, 0, this.values, 0, values.length);

        this.fitnessValue = other.fitnessValue;
    }


    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}