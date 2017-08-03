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

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class GeneValueScope {

    public int type;  //thye value of the type is consistent with the value defined in Problem
    private int minValue;
    private int maxValue;
    private EncodedConstraintType encodedConstraintType;

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public EncodedConstraintType getEncodedConstraintType() {
        return encodedConstraintType;
    }

    public void setEncodedConstraintType(EncodedConstraintType encodedConstraintType) {
        this.encodedConstraintType = encodedConstraintType;
    }

    enum EncodedConstraintType {String, Double}

}
