package com.simula.esocl.oclga;

import java.io.File;

public interface Problem {
    public static final int NUMERICAL_TYPE = 0;
    public static final int CATEGORICAL_TYPE = 1;

    /*
     * matrix N*3 - N is the number of variables - 3 values: min, max, type -
     * type is 0 if numeric, otherwise 1
     */
    // protected abstract int[][] getConstraints_internal();
    public int[][] getConstraints();

    /*
     * array of N variables
     */
    public double getFitness(int[] v);

    public void processProblem(File inputModel);

}
