package com.simula.esocl.standalone.modelinstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UMLObjectIns extends AbstUMLModelIns {

    private Map<String, Object> propertyMap = new HashMap<String, Object>();

    public UMLObjectIns(String name, String value) {
        super(name, value);
        // TODO Auto-generated constructor stub
    }

    public Object getPropertyObject(String propertyName) {
        return propertyMap.get(propertyName);
    }

    public void addProperty(String propertyName, Object umlProperty) {
        propertyMap.put(propertyName, umlProperty);
    }

    public Collection<UMLNonAssPropIns> getPrimitivePropertyCollection() {
        Collection<Object> properties = propertyMap.values();
        Collection<UMLNonAssPropIns> primitiveProperties = new HashSet<UMLNonAssPropIns>();
        for (Object object : properties) {
            if (object instanceof UMLNonAssPropIns) {
                primitiveProperties.add((UMLNonAssPropIns) object);
            }
        }
        return primitiveProperties;
    }

}