package com.simula.esocl.standalone.analysis;

import org.dresdenocl.metamodels.uml2.internal.model.UML2Model;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.internal.impl.PackageImpl;
import com.simula.esocl.standalone.modelinstance.UMLNonAssPropIns;
import com.simula.esocl.standalone.modelinstance.UMLObjectIns;
import  org.eclipse.uml2.uml.Enumeration;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class UMLModelInsGenerator {

    /**
     * singleton instance
     */
    private static UMLModelInsGenerator instance;
    public static UMLModelInsGenerator INSTANCE = instance();
    //property array
    UMLNonAssPropIns[] array_properties;
    //generated class instances
    List<UMLObjectIns> objects;
    //enumeration types in the .uml file
    List<Enumeration> enumTypes;

    private static UMLModelInsGenerator instance() {

        if (instance == null) {
            instance = new UMLModelInsGenerator();
        }
        return instance;
    }

    /**
     * reassign the value of each instance
     *
     * @param values
     * @return
     */
    public List<UMLObjectIns> getModelInstance(int[] values) {
        for (int i = 0; i < array_properties.length; i++) {
            /**transfer the int value to each instance*/
            if (array_properties[i].getType() == 0) {
                array_properties[i].setValue("" + values[i]);
            } else if (array_properties[i].getType() == 1) {
                if (array_properties[i].getEnumType() == null) {
                    if (values[i] == 0)
                        array_properties[i].setValue("false");
                    else
                        array_properties[i].setValue("true");
                } else {
                    Enumeration enumType = getEnumeration(array_properties[i]
                            .getEnumType());
                    String lieralName = enumType.getOwnedLiterals()
                            .get(values[i]).getName();
                    array_properties[i].setValue(lieralName);
                }
            }
            //System.out.println(array_properties[i].getName() + "::" + array_properties[i].getValue());
        }
        return objects;
    }

    public UMLNonAssPropIns[] getProperties(UML2Model model) {
        /**initial the model instance list*/
        objects = new ArrayList<UMLObjectIns>();

        Resource resource=null;
        try {
            Field field = model.getClass().getDeclaredField("resource");
            field.setAccessible(true);
            resource= (Resource)field.get(model);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        PackageImpl rootObject = (PackageImpl) resource.getContents().get(0);

        buildModelInstance(rootObject);

        Collection<UMLNonAssPropIns> properties = new HashSet<UMLNonAssPropIns>();
        for (UMLObjectIns uoi : objects) {
            properties.addAll(uoi.getPrimitivePropertyCollection());
        }
        int i = 0;
        array_properties = new UMLNonAssPropIns[properties.size()];
        for (UMLNonAssPropIns property : properties) {
            array_properties[i++] = property;
        }
        return array_properties;
    }

    public UMLNonAssPropIns[] getProperties(String inputModelPath) {
        /**initial the model instance list*/
        objects = new ArrayList<UMLObjectIns>();

        /******** parse the .uml model for generating the instances automatically ***/
        Resource resource = new XMIResourceImpl(
                URI.createFileURI(inputModelPath));
        Map<String, String> options = new HashMap<String, String>();
        options.put(XMIResource.OPTION_ENCODING, "UTF-8");
        try {
            resource.load(options);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PackageImpl rootObject = (PackageImpl) resource.getContents().get(0);
        /*******************************************************************/
        buildModelInstance(rootObject);

		/*
         * UMLObjectIns customer = new UMLObjectIns("Customer", null);
		 * UMLNonAssPropIns age = new UMLNonAssPropIns("age", "", 0);
		 * customer.addProperty("age", age); objects.add(customer);
		 */
        /*** generate the property array based on the properties of instances */
        Collection<UMLNonAssPropIns> properties = new HashSet<UMLNonAssPropIns>();
        for (UMLObjectIns uoi : objects) {
            properties.addAll(uoi.getPrimitivePropertyCollection());
        }
        int i = 0;
        array_properties = new UMLNonAssPropIns[properties.size()];
        for (UMLNonAssPropIns property : properties) {
            array_properties[i++] = property;
        }
        return array_properties;
    }

    public void buildModelInstance(PackageImpl rootObject) {
        List<org.eclipse.uml2.uml.Class> classList = new ArrayList<org.eclipse.uml2.uml.Class>();
        List<Association> assList = new ArrayList<Association>();
        enumTypes = new ArrayList<Enumeration>();
        EList<org.eclipse.uml2.uml.Element> pElements = rootObject
                .allOwnedElements();
        //identify the CLASS, ASSOCIATION and ENUMERATION
        for (org.eclipse.uml2.uml.Element element : pElements) {
            if (element instanceof org.eclipse.uml2.uml.Class) {
                classList.add((org.eclipse.uml2.uml.Class) element);
            } else if (element instanceof Association) {
                assList.add((Association) element);
            } else if (element instanceof Enumeration) {
                enumTypes.add((Enumeration) element);
            }
        }

        for (Class modelClass : classList) {
            buildUMLObjectInstance(modelClass);
        }

        //build the class instances based on the associations
        for (Association ass : assList) {
            List<UMLObjectIns> uoiList_2 = new ArrayList<UMLObjectIns>();
            List<org.eclipse.uml2.uml.Property> ends = ass.getMemberEnds();
            //two ends of ass
            String endType_1 = ((Class) ends.get(0).getType()).getName();
            String endType_2 = ((Class) ends.get(1).getType()).getName();
            Class cla_1 = getClass(classList, endType_1);
            UMLObjectIns uoi_1 = buildUMLObjectInstance(cla_1);
            Class cla_2 = getClass(classList, endType_2);
            // Please modify the iteration time!!!!!!!
            for (int i = 0; i < 1; i++) {
                UMLObjectIns uoi_2 = buildUMLObjectInstance(cla_2);
                //add the ass property
                uoi_2.addProperty(ends.get(0).getName(), uoi_1);
                uoiList_2.add(uoi_2);
            }
            uoi_1.addProperty(ends.get(1).getName(), uoiList_2);

        }
    }

    /**
     * obtain the int label of each type
     *
     * @param type
     * @return
     */
    public int value4PPType(org.eclipse.uml2.uml.Type type) {
        if (type instanceof PrimitiveType) {
            String[] tempStrs = type.toString().split("#");
            String tempTypeValue = tempStrs[tempStrs.length - 1];
            String typeValue = tempTypeValue.substring(0,
                    tempTypeValue.length() - 1);
            if (typeValue.equals("Integer"))
                return 0;
            else
                return 1;
        } else if (type instanceof Enumeration) {
            return 1;
        }
        return -1;
    }

    public Class getClass(List<org.eclipse.uml2.uml.Class> classList,
                          String className) {
        for (Class cla : classList) {
            if (cla.getName().equals(className))
                return cla;
        }
        return null;
    }

    public Enumeration getEnumeration(String enumName) {
        for (Enumeration enumeration : enumTypes) {
            if (enumeration.getName().equals(enumName))
                return enumeration;
        }
        return null;
    }

    /**
     * build the class instance based on the class of .uml file
     *
     * @param cla
     * @return
     */
    public UMLObjectIns buildUMLObjectInstance(Class cla) {
        UMLObjectIns uoi = new UMLObjectIns(cla.getName(), null);
        for (org.eclipse.uml2.uml.Property pp : cla.getAllAttributes()) {
            if (pp.getType() instanceof PrimitiveType
                    || pp.getType() instanceof Enumeration) {
                //build the property instance
                UMLNonAssPropIns uppi = new UMLNonAssPropIns(pp.getName(), "",
                        value4PPType(pp.getType()));
                // if the type of property is Enumeration, we should configure the type name.
                if (pp.getType() instanceof Enumeration) {
                    uppi.setEnumType(((Enumeration) pp.getType()).getName());
                }


                //configure the min or max value of each type
                switch (value4PPType(pp.getType())) {
                    case 0:
                        uppi.setMinValue(-100);
                        uppi.setMaxValue(200);
                        break;
                    case 1:
                        uppi.setMinValue(0);
                        if (uppi.getEnumType() == null) {
                            uppi.setMaxValue(1);
                        } else {
                            Enumeration enumType = getEnumeration(((Enumeration) pp
                                    .getType()).getName());
                            uppi.setMaxValue(enumType.getOwnedLiterals().size() - 1);
                        }

                        break;
                }


                uoi.addProperty(pp.getName(), uppi);
            }
        }
        objects.add(uoi);
        return uoi;
    }
}