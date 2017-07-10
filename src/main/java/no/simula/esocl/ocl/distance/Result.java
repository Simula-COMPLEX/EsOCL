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

    private String constraint;
    private Boolean result;
    private String algo;
    private Integer iternations;
    private String solution;
    private List<IModelInstanceObject> objects;
    private IModelInstanceObject object;
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

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public Integer getIternations() {
        return iternations;
    }

    public void setIternations(Integer iternations) {
        this.iternations = iternations;
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

    public List<IModelInstanceObject> getObjects() {
        return objects;
    }

    public void setObjects(List<IModelInstanceObject> objects) {
        this.objects = objects;
    }

    public IModelInstanceObject getObject() {
        return object;
    }

    public void setObject(IModelInstanceObject object) {
        this.object = object;
    }
}
