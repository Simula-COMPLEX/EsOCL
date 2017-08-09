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

import org.dresdenocl.pivotmodel.Property;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class ValueElement4Search {

    /**
     * class that has the attribute
     */
    private String sourceClass;
    /**
     * associate class
     */
    private String destinationClass;
    /**
     * name of attribute or association
     */
    private String attributeName;
    /**
     * the name of enumeration type
     */
    private String enumType;

    /**
     * concrete value of element
     */
    private String value;

    private Property property;

    private int maxValue, minValue;
    /**
     * int label of type
     */
    private int type;

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    public String getDestinationClass() {
        return destinationClass;
    }

    public void setDestinationClass(String destinationClass) {
        this.destinationClass = destinationClass;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getEnumType() {
        return enumType;
    }

    public void setEnumType(String enumType) {
        this.enumType = enumType;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public ValueElement4Search createNewInstance() {

        ValueElement4Search newVes = new ValueElement4Search();
        newVes.setSourceClass(this.sourceClass);
        newVes.setDestinationClass(this.destinationClass);
        newVes.setAttributeName(this.attributeName);
        newVes.setEnumType(this.enumType);
        newVes.setType(this.type);
        newVes.setMinValue(this.minValue);
        newVes.setMaxValue(this.maxValue);
        newVes.setProperty(this.property);
        return newVes;
    }

}
