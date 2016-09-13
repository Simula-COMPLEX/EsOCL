package no.simula.esocl.standalone.analysis;

import no.simula.esocl.ocl.distance.ValueElement4Search;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dresdenocl.essentialocl.expressions.impl.PropertyCallExpImpl;
import org.dresdenocl.essentialocl.types.SetType;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Class;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Enumeration;
import org.dresdenocl.metamodels.uml2.internal.model.UML2PrimitiveType;
import org.dresdenocl.model.IModel;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.pivotmodel.Property;
import org.dresdenocl.pivotmodel.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VESGenerator {
    /**
     * this list is initialized for recording the needed attribute information from .uml model
     */
    List<ValueElement4Search> initialVesForSearchList = new ArrayList<ValueElement4Search>();

    Map<String, List<ValueElement4Search>> iniVesGroupByClassMap = new HashMap<String, List<ValueElement4Search>>();
    /**
     * enumeration types in the .uml file
     */
    List<UML2Enumeration> enumerationList = new ArrayList<UML2Enumeration>();
    /**
     * OCL constraints parsed from .ocl file
     */
    Constraint constraint = null;

    /**
     * generate the initial veslist base on the .uml model
     *
     * @param model
     * @return
     */

    Logger logger = Logger.getLogger("bar");

    public VESGenerator(Constraint constraint) {
        this.constraint = constraint;
        logger.setLevel(Level.OFF);
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

    public List<ValueElement4Search> buildInitialVes(IModel model) {
        System.err
                .println("*******************Initial the VES array*******************");
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
                logger.info("Add a VES:: ClassName= "
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
                logger.info("Add a VES:: ClassName= "
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ves.setMinValue(LowValue);
                if (upperValue.equals("*"))
                    ves.setMaxValue(100);
                else
                    ves.setMaxValue(Integer.valueOf(upperValue));
                if (!ves.getSourceClass().equals(ves.getDestinationClass()))
                    ves.setValue(""
                            + Utility.INSTANCE.getFixedNumberOfCardinality(ves));
                System.out
                        .println(ves.getMinValue() + "  " + ves.getMaxValue());

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
     * obtain the int label of each type
     *
     * @param type
     * @return
     */
    public int value4PPType(Type type) {
        if (type instanceof UML2PrimitiveType) {
            String typeValue = ((UML2PrimitiveType) type).getKind().getName();
            if (typeValue.equals("Integer"))
                return 0;
            else if (typeValue.equals("Boolean"))
                return 1;
            else if (typeValue.equals("String"))
                return 2;
            else if (typeValue.equals("Real"))
                return 3;
        } else if (type instanceof UML2Enumeration) {
            return 1;
        }
        return -1;
    }

    /**
     * find the UML2Enumeration from the this.enumerationList based on the type name
     *
     * @param enumName
     * @return
     */
    public UML2Enumeration getEnumeration(String enumName) {
        for (UML2Enumeration enumeration : this.enumerationList) {
            if (enumeration.getQualifiedName().equals(enumName))
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
