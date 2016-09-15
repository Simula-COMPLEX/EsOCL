package no.simula.esocl.standalone.modelinstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UMLObjectIns extends AbstUMLModelIns {

    private Map<String, Object> propertyMap = new HashMap<String, Object>();

    public UMLObjectIns(String qualifiedName, String value) {
        // The name is qualified name
        super(qualifiedName, value);
        // TODO Auto-generated constructor stub
    }

    public Object getPropertyObject(String propertyName) {
        return propertyMap.get(propertyName);
    }

    public void addProperty(String propertyName, Object umlProperty) {
        propertyMap.put(propertyName, umlProperty);
    }


    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }

    public Collection<UMLAttributeIns> getPrimitivePropertyCollection() {
        Collection<Object> properties = propertyMap.values();
        Collection<UMLAttributeIns> primitiveProperties = new HashSet<UMLAttributeIns>();
        for (Object object : properties) {
            if (object instanceof UMLAttributeIns) {
                primitiveProperties.add((UMLAttributeIns) object);
            } else if (object instanceof Collection) {
                if (((Collection) object).toArray()[0] instanceof UMLAttributeIns)
                    primitiveProperties.addAll((Collection) object);
            }
        }
        return primitiveProperties;
    }

    public String getAttributeNames() {
        String name = "";
        for (String attrName : propertyMap.keySet()) {
            Object object = propertyMap.get(attrName);
            if (object instanceof UMLAttributeIns) {
                name = attrName + " multiplicity: 1 ";
            } else if (object instanceof Collection) {
                name = attrName + " multiplicity: " + ((Collection) object).size() + " ";
            }
        }
        return name;
    }


}
