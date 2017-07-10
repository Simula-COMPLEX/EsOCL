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

package no.simula.esocl.standalone.modelinstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class UMLObjectIns extends AbstUMLModelIns {

    private Map<String, Object> propertyMap = new HashMap<String, Object>();

    public UMLObjectIns(String qualifiedName, String value) {
        super(qualifiedName, value);
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
