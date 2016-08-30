package com.simula.esocl.standalone.modelinstance;

public class UMLNonAssPropIns extends AbstUMLModelIns {
    int type;
    int maxValue, minValue;
    String enumType;

    public UMLNonAssPropIns(String name, String value, int type) {
        super(name, value);
        this.type = type;
    }


    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getEnumType() {
        return enumType;
    }

    public void setEnumType(String enumType) {
        this.enumType = enumType;
    }

    public int getType() {
        return type;
    }

    public int getMaxVlaue() {
        return maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

}
