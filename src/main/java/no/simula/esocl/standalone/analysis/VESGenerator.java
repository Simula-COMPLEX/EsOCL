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

    private Map<String, List<ValueElement4Search>> iniVesGroupByClassMap = new HashMap<>();
    /**
     * enumeration types in the .uml file
     */
    private List<UML2Enumeration> enumerationList = new ArrayList<>();
    /**
     * OCL constraints parsed from .ocl file
     */
    private Constraint constraint = null;


    public VESGenerator(Constraint constraint) {
        this.constraint = constraint;
        // logger.setLevel(Level.OFF);
        // PropertyConfigurator.configure("Eslog4j.properties");
    }

    public List<ValueElement4Search> getInitialVesForSearchList() {
        return initialVesForSearchList;
    }

    public Map<String, List<ValueElement4Search>> getIniVesGroupByClassMap() {
        return iniVesGroupByClassMap;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public List<ValueElement4Search> buildInitialVes() {
        logger.debug("*******************Initial the VES array*******************");
        // OCLExpUtility.INSTANCE.printOclClause4Depth(cons.getSpecification());
        List<PropertyCallExpImpl> propCallList = OCLExpUtility.INSTANCE
                .getPropertyInCons(this.constraint);
        for (PropertyCallExpImpl propCall : propCallList) {
            UML2Class vesCla = (UML2Class) propCall.getSourceType();
            Property p = propCall.getReferredProperty();
            ValueElement4Search ves = new ValueElement4Search();
            ves.setProperty(p);
            ves.setSourceClass(vesCla.getQualifiedName());
            if (p.getType() instanceof UML2PrimitiveType
                    || p.getType() instanceof UML2Enumeration) {
                logger.debug("Add a VES:: ClassName= "
                        + vesCla.getQualifiedName() + " AttrName= "
                        + p.getName() + " AttrType= " + p.getType().getName());
                ves.setDestinationClass(vesCla.getQualifiedName());
                ves.setAttributeName(p.getQualifiedName());
                ves.setType(value4PPType(p.getType()));
                // if the type of property is Enumeration, we should
                // configure the type name.
                if (p.getType() instanceof UML2Enumeration) {
                    ves.setEnumType(((UML2Enumeration) p.getType())
                            .getQualifiedName());
                    this.enumerationList.add((UML2Enumeration) p.getType());
                }
                // configure the min or max value of each type
                switch (value4PPType(p.getType())) {
                    case 0:
                        ves.setMinValue(-100);
                        ves.setMaxValue(100);
                        break;
                    case 1:
                        ves.setMinValue(0);
                        if (ves.getEnumType() == null) {
                            ves.setMaxValue(1);
                        } else {
                            UML2Enumeration enumType = (UML2Enumeration) p
                                    .getType();
                            ves.setMaxValue(enumType.getOwnedLiteral().size() - 1);
                        }
                        break;
                    case 2:
                        ves.setMinValue(1);
                        ves.setMaxValue(100);
                        break;
                    case 3:
                        ves.setMinValue(0);
                        ves.setMaxValue(100);
                        break;
                }
            }// end if
            else if (p.getType() instanceof SetType
                    || p.getType() instanceof UML2Class) {
                logger.debug("Add a VES:: ClassName= "
                        + vesCla.getQualifiedName() + " AttrName= "
                        + p.getName() + " AttrType= " + p.getType().getName());
                if (p.getType() instanceof SetType) {
                    Type elementType = ((SetType) p.getType()).getElementType();
                    // the type of property is association
                    if (elementType instanceof UML2Class)
                        ves.setDestinationClass(elementType.getQualifiedName());
                    else
                        // the type of property is primitivetype
                        ves.setDestinationClass(vesCla.getQualifiedName());
                } else
                    ves.setDestinationClass(p.getType().getQualifiedName());
                ves.setAttributeName(p.getQualifiedName());
                ves.setType(0);
                int LowValue = 0;
                String upperValue = "";
                try {
                    LowValue = Integer.valueOf(Utility.INSTANCE
                            .getLowAndUpperValueForProperty(p.getOwningType()
                                    .getName(), p.getName(), "model")[0]);

                    upperValue = Utility.INSTANCE
                            .getLowAndUpperValueForProperty(p.getOwningType()
                                    .getName(), p.getName(), "model")[1];
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    // by default value is zero
                }
                ves.setMinValue(LowValue);
                if (upperValue == null || upperValue.isEmpty() || upperValue.equals("*"))
                    ves.setMaxValue(100);
                else
                    ves.setMaxValue(Integer.valueOf(upperValue));
                if (!ves.getSourceClass().equals(ves.getDestinationClass()))
                    ves.setValue(""
                            + Utility.INSTANCE.getFixedNumberOfCardinality(ves));
                logger.debug("ves min " + ves.getMinValue() + " ves max " + ves.getMaxValue());

            }
            this.initialVesForSearchList.add(ves);
            List<ValueElement4Search> existVesList = this.iniVesGroupByClassMap
                    .get(vesCla.getQualifiedName());
            if (existVesList != null) {
                existVesList.add(ves);
            } else {
                List<ValueElement4Search> vesList = new ArrayList<ValueElement4Search>();
                vesList.add(ves);
                this.iniVesGroupByClassMap.put(vesCla.getQualifiedName(),
                        vesList);


            }
        }

        return initialVesForSearchList;
    }

    /**
     * obtain the integer label of each type
     * @param type  {@link Type}  type of property
     * @return int integer code
     */

    public int value4PPType(Type type) {
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

    public List<ValueElement4Search> getVesList4Class(String className) {
        return this.iniVesGroupByClassMap.get(className);
    }

    public ValueElement4Search getVes(List<ValueElement4Search> vesList,
                                      String attrName) {
        for (ValueElement4Search ves : vesList) {
            if (ves.getAttributeName().equals(attrName))
                return ves;
        }
        return null;
    }

}
