package no.simula.esocl.standalone.modelinstance;

import java.util.ArrayList;
import java.util.List;

public class AbstUMLModelIns {

    String qualifiedName;
    String value;

    public AbstUMLModelIns(String qualifiedName, String value) {
        this.qualifiedName = qualifiedName;
        this.value = value;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return new AbstUMLModelIns(this.qualifiedName, this.value);
    }

    public List<String> getQualifiedNameList() {
        List<String> qualifiedNameList = new ArrayList<String>();
        String[] qualifiedNames = this.qualifiedName.split("::");
        for (String qualifiedName : qualifiedNames) {
            qualifiedNameList.add(qualifiedName);
        }
        return qualifiedNameList;

    }

    public String getName() {
        String[] qualifiedNames = this.qualifiedName.split("::");
        return qualifiedNames[qualifiedNames.length - 1];
    }

}
