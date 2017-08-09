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

    /**
     * value of the type is consistent with the value defined in Problem
     */
    public int type;
    private int minValue;
    private int maxValue;
    private EncodedConstraintType encodedConstraintType;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    enum EncodedConstraintType {String, Double}

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

    public EncodedConstraintType getEncodedConstraintType() {
        return encodedConstraintType;
    }

    public void setEncodedConstraintType(EncodedConstraintType encodedConstraintType) {
        this.encodedConstraintType = encodedConstraintType;
    }
}
