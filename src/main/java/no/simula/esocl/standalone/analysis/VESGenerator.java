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

package no.simula.esocl.standalone.analysis;

import no.simula.esocl.ocl.distance.ValueElement4Search;
import org.apache.log4j.Logger;
import org.dresdenocl.essentialocl.expressions.impl.PropertyCallExpImpl;
import org.dresdenocl.essentialocl.types.SetType;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Class;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Enumeration;
import org.dresdenocl.metamodels.uml2.internal.model.UML2PrimitiveType;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.pivotmodel.Property;
import org.dresdenocl.pivotmodel.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class VESGenerator {

    private static Logger logger = Logger.getLogger(VESGenerator.class);

    /**
     * this list is initialized for recording the needed attribute information from .uml model
     */
    private List<ValueElement4Search> initialVesForSearchList = new ArrayList<>();


    private Map<String, List<ValueElement4Search>> valueElement4SearchByClass = new HashMap<>();

    /**
     * enumeration types in the .uml file
     */

    private List<UML2Enumeration> enumerationList = new ArrayList<>();

    /**
     * OCL constraints
     */
    private Constraint constraint = null;


    public VESGenerator(Constraint constraint) {
        this.constraint = constraint;
    }


    public List<ValueElement4Search> buildInitialVes() {
        logger.debug("");
        logger.debug("*******************Initial the Value Element 4 Search List *******************");

        // OCLExpUtility.INSTANCE.printOclClause4Depth(cons.getSpecification());

        List<PropertyCallExpImpl> propertyCallExps = OCLExpUtility.INSTANCE.getPropertyInCons(constraint);

        for (PropertyCallExpImpl propertyCallExp : propertyCallExps) {
            ValueElement4Search valueElement4Search = new ValueElement4Search();

            UML2Class sourceClass = (UML2Class) propertyCallExp.getSourceType();
            valueElement4Search.setSourceClass(sourceClass.getQualifiedName());

            Property property = propertyCallExp.getReferredProperty();
            Type propertyType = property.getType();
            valueElement4Search.setProperty(property);


            if (propertyType instanceof UML2PrimitiveType || propertyType instanceof UML2Enumeration) {

                valueElement4SearchPrimitiveOrEnumeration(valueElement4Search, sourceClass.getQualifiedName(), property.getQualifiedName(), propertyType);


            } else if (propertyType instanceof SetType || propertyType instanceof UML2Class) {

                valueElement4SearchSetOrClass(valueElement4Search, sourceClass, property, propertyType);
            }


            initialVesForSearchList.add(valueElement4Search);
            List<ValueElement4Search> existVesList = valueElement4SearchByClass.get(sourceClass.getQualifiedName());
            if (existVesList != null) {
                existVesList.add(valueElement4Search);
            } else {
                List<ValueElement4Search> valueElement4SearchList = new ArrayList<>();
                valueElement4SearchList.add(valueElement4Search);
                valueElement4SearchByClass.put(sourceClass.getQualifiedName(), valueElement4SearchList);
            }
        }

        return initialVesForSearchList;
    }


    private void valueElement4SearchPrimitiveOrEnumeration(ValueElement4Search valueElement4Search, String sourceClass, String property, Type propertyType) {
        valueElement4Search.setDestinationClass(sourceClass);
        valueElement4Search.setAttributeName(property);
        valueElement4Search.setType(value4PPType(propertyType));


        // if the type of property is Enumeration, we should  configure the type name.
        if (propertyType instanceof UML2Enumeration) {
            valueElement4Search.setEnumType(propertyType.getQualifiedName());
            enumerationList.add((UML2Enumeration) propertyType);
        }


        // configure the min or max value of each type
        switch (value4PPType(propertyType)) {
            case 0: {
                valueElement4Search.setMinValue(-100);
                valueElement4Search.setMaxValue(100);
                break;
            }
            case 1: {
                valueElement4Search.setMinValue(0);
                if (valueElement4Search.getEnumType() == null) {
                    valueElement4Search.setMaxValue(1);
                } else {
                    UML2Enumeration enumType = (UML2Enumeration) propertyType;
                    valueElement4Search.setMaxValue(enumType.getOwnedLiteral().size() - 1);
                }
                break;
            }
            case 2: {
                valueElement4Search.setMinValue(1);
                valueElement4Search.setMaxValue(100);
                break;
            }
            case 3: {
                valueElement4Search.setMinValue(0);
                valueElement4Search.setMaxValue(100);
                break;
            }
        }


        logger.debug("Adding a VES:: ClassName= " + sourceClass
                + " Attribute Name= " + property
                + " Attribute Type= " + propertyType.getName());

    }


    private void valueElement4SearchSetOrClass(ValueElement4Search valueElement4Search, UML2Class sourceClass, Property property, Type propertyType) {
        valueElement4Search.setAttributeName(property.getQualifiedName());
        valueElement4Search.setType(0);

        if (propertyType instanceof SetType) {
            Type elementType = ((SetType) propertyType).getElementType();
            // the type of property is association
            if (elementType instanceof UML2Class) {
                valueElement4Search.setDestinationClass(elementType.getQualifiedName());
            } else {
                // the type of property is primitivetype
                valueElement4Search.setDestinationClass(sourceClass.getQualifiedName());
            }
        } else {
            valueElement4Search.setDestinationClass(propertyType.getQualifiedName());
        }


        int LowValue = 0;

        try {
            LowValue = Integer.valueOf(Utility.INSTANCE
                    .getLowAndUpperValueForProperty(property.getOwningType()
                            .getName(), property.getName(), "model")[0]);


        } catch (NumberFormatException e) {

        }
        valueElement4Search.setMinValue(LowValue);


        String upperValue = Utility.INSTANCE
                .getLowAndUpperValueForProperty(property.getOwningType()
                        .getName(), property.getName(), "model")[1];
        if (upperValue == null || upperValue.isEmpty() || upperValue.equals("*")) {
            valueElement4Search.setMaxValue(100);
        } else {
            valueElement4Search.setMaxValue(Integer.valueOf(upperValue));
        }

        if (!valueElement4Search.getSourceClass().equals(valueElement4Search.getDestinationClass())) {
            valueElement4Search.setValue(""
                    + Utility.INSTANCE.getFixedNumberOfCardinality(valueElement4Search));
        }


        logger.debug("Adding a VES:: ClassName= " + sourceClass.getQualifiedName()
                + " Attribute Name= " + property.getName()
                + " Attribute Type= " + propertyType.getName());

        logger.debug("VES Min " + valueElement4Search.getMinValue() + " VES Max " + valueElement4Search.getMaxValue());


    }


    /**
     * obtain the integer label of each type
     *
     * @param type {@link Type}  type of property
     * @return int integer code
     */

    private int value4PPType(Type type) {
        if (type instanceof UML2PrimitiveType) {
            String typeName = ((UML2PrimitiveType) type).getKind().getName();
            switch (typeName) {
                case "Integer":
                    return 0;
                case "Boolean":
                    return 1;
                case "String":
                    return 2;
                case "Real":
                    return 3;
            }
        } else if (type instanceof UML2Enumeration) {
            return 1;
        }
        return -1;
    }

    /**
     * find the UML2Enumeration from the enumerationList based on the type name
     *
     * @param enumerationName Enumeration Name
     * @return UML2Enumeration
     */

    public UML2Enumeration getEnumeration(String enumerationName) {
        for (UML2Enumeration enumeration : this.enumerationList) {
            if (enumeration.getQualifiedName().equals(enumerationName))
                return enumeration;
        }
        return null;
    }

    public List<ValueElement4Search> getValueElement4SearchByClass(String className) {
        return valueElement4SearchByClass.get(className);
    }

    public ValueElement4Search getValueElement4Search(List<ValueElement4Search> valueElement4SearchList, String attrName) {
        for (ValueElement4Search ves : valueElement4SearchList) {
            if (ves.getAttributeName().equals(attrName))
                return ves;
        }
        return null;
    }


    public List<ValueElement4Search> getInitialVesForSearchList() {
        return initialVesForSearchList;
    }

    public void setInitialVesForSearchList(List<ValueElement4Search> initialVesForSearchList) {
        this.initialVesForSearchList = initialVesForSearchList;
    }

    public Map<String, List<ValueElement4Search>> getValueElement4SearchByClass() {
        return valueElement4SearchByClass;
    }

    public void setValueElement4SearchByClass(Map<String, List<ValueElement4Search>> valueElement4SearchByClass) {
        this.valueElement4SearchByClass = valueElement4SearchByClass;
    }

    public List<UML2Enumeration> getEnumerationList() {
        return enumerationList;
    }

    public void setEnumerationList(List<UML2Enumeration> enumerationList) {
        this.enumerationList = enumerationList;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }
}
