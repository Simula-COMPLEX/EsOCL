package no.simula.esocl.ocl.distance;

import org.dresdenocl.pivotmodel.Property;

public class ValueElement4Search {

    // the class that has the attribute
    String sourceClass;
    // the associate class
    String destinationClass;
    // the name of attribute or association
    String attributeName;
    // the name of enumeration type
    String enumType;
    // the concrete value of element
    String value;

    Property property;

    int maxValue, minValue;
    // the int label of type
    int type;

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
        // TODO Auto-generated method stub
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
