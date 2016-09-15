package no.simula.esocl.standalone.modelinstance;

import org.dresdenocl.pivotmodel.Type;

public class UMLAttributeIns extends AbstUMLModelIns {

    private Type type;

    public UMLAttributeIns(String name, String value) {
        super(name, value);

    }


    public Type getType() {
        return type;
    }


    public void setType(Type type) {
        this.type = type;
    }


    public String getClassName() {
        String[] qualifiedNames = this.qualifiedName.split("::");
        return qualifiedNames[qualifiedNames.length - 2];
    }

}
