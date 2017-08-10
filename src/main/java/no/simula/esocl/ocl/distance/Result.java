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

import org.dresdenocl.modelinstance.IModelInstance;

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
     * Number of iteration used to find stringSolution
     */
    private Integer iterations;
    /**
     * All Solution Object
     */
    private List<IModelInstance> solutionObjects;
    /**
     * Object with final stringSolution
     */
    private IModelInstance solutionObject;
    /**
     * final Solution variables
     */
    private String stringSolution;
    /**
     * All Solution variables
     */
    private List<String> stringSolutions;


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

    public String getStringSolution() {
        return stringSolution;
    }

    public void setStringSolution(String stringSolution) {
        this.stringSolution = stringSolution;
    }

    public List<String> getStringSolutions() {
        return stringSolutions;
    }

    public void setStringSolutions(List<String> stringSolutions) {
        this.stringSolutions = stringSolutions;
    }

    public List<IModelInstance> getSolutionObjects() {
        return solutionObjects;
    }

    public void setSolutionObjects(List<IModelInstance> solutionObjects) {
        this.solutionObjects = solutionObjects;
    }

    public IModelInstance getSolutionObject() {
        return solutionObject;
    }

    public void setSolutionObject(IModelInstance solutionObject) {
        this.solutionObject = solutionObject;
    }
}
