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

package no.simula.esocl.ocl.distance;

import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;

import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class Result {

    /**
     * OCL constraint
     */
    private String constraint;
    /**
     * Search Result
     */
    private Boolean result;
    /**
     * Search Algorithms which find result
     */
    private String searchAlgorithms;
    /**
     * Number of iteration used to find solution
     */
    private Integer iterations;
    /**
     * All Solution Object
     */
    private List<IModelInstanceObject> solutionObjects;
    /**
     * Object with final solution
     */
    private IModelInstanceObject finalSolutionObject;
    /**
     * final Solution variables
     */
    private String solution;
    /**
     * All Solution variables
     */
    private List<String> solutions;


    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getSearchAlgorithms() {
        return searchAlgorithms;
    }

    public void setSearchAlgorithms(String searchAlgorithms) {
        this.searchAlgorithms = searchAlgorithms;
    }

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<String> solutions) {
        this.solutions = solutions;
    }

    public List<IModelInstanceObject> getSolutionObjects() {
        return solutionObjects;
    }

    public void setSolutionObjects(List<IModelInstanceObject> solutionObjects) {
        this.solutionObjects = solutionObjects;
    }

    public IModelInstanceObject getFinalSolutionObject() {
        return finalSolutionObject;
    }

    public void setFinalSolutionObject(IModelInstanceObject finalSolutionObject) {
        this.finalSolutionObject = finalSolutionObject;
    }
}
