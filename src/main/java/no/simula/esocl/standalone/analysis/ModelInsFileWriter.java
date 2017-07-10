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

import no.simula.esocl.standalone.modelinstance.UMLAttributeIns;
import no.simula.esocl.standalone.modelinstance.UMLObjectIns;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dresdenocl.pivotmodel.Enumeration;
import org.dresdenocl.pivotmodel.Type;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class ModelInsFileWriter {

    private static int ins_index = 0, slot_index = 0, value_index = 0, spec_index = 0;
    private Document doc;
    private Element rootElement;
    private List<UMLObjectIns> umlObjectInsList;

    private Map<UMLObjectIns, String> insMap = new HashMap<UMLObjectIns, String>();

    public ModelInsFileWriter(List<UMLObjectIns> umlObjectInsList) {
        this.doc = DocumentHelper.createDocument();
        this.rootElement = this.doc.addElement("root");
        this.umlObjectInsList = umlObjectInsList;
    }

    public Element createInstanceSpecification(String instanceName,
                                               String instanceID, String classifiedID) {
        Element insSpecElement = this.rootElement.addElement("packagedElement");
        insSpecElement.addAttribute("xmi:type", "uml:InstanceSpecification");
        insSpecElement.addAttribute("xmi:id", instanceID);
        insSpecElement.addAttribute("name", instanceName);
        insSpecElement.addAttribute("classifier", classifiedID);
        return insSpecElement;
    }

    public void createSlot(Element fatherElement, String slotID,
                           String definingFeatureID, String valueID, String value,
                           String instanceID, String type) {
        Element slotElement = fatherElement.addElement("slot");
        slotElement.addAttribute("xmi:id", slotID);
        slotElement.addAttribute("definingFeature", definingFeatureID);
        Element valueElement = slotElement.addElement("value");
        valueElement.addAttribute("xmi:id", valueID);
        if (type == null) {
            valueElement.addAttribute("xmi:type", "uml:InstanceValue");
            valueElement.addAttribute("instance", instanceID);
        } else {
            if (type.equals("Integer")) {
                valueElement.addAttribute("xmi:type", "uml:LiteralInteger");
                valueElement.addAttribute("value", value);
            } else if (type.equals("String") || type.equals("Real")) {
                valueElement.addAttribute("xmi:type", "uml:OpaqueExpression");
                Element bodyElement = valueElement.addElement("body");
                bodyElement.setText(value);
            } else if (type.equals("Boolean")) {
                valueElement.addAttribute("xmi:type", "uml:LiteralBoolean");
                valueElement.addAttribute("value", value);
            }
        }

    }

    public void createSlotFromUMLAttr(Element fatherElement, Object attr) {
        String slotID = "SLOT_ID" + slot_index;
        if (attr instanceof UMLAttributeIns) {
            String definingFeatureID = Utility.INSTANCE.getElementID(
                    ((UMLAttributeIns) attr).getClassName(),
                    ((UMLAttributeIns) attr).getName());
            String valueID = "VALUE_ID_" + value_index;
            Type type = ((UMLAttributeIns) attr).getType();
            if (type instanceof Enumeration) {
                createSpecification(fatherElement, "SPEC_ID_" + spec_index,
                        ((UMLAttributeIns) attr).getValue());
                spec_index++;
            } else {
                createSlot(fatherElement, slotID, definingFeatureID, valueID,
                        ((UMLAttributeIns) attr).getValue(), null,
                        type.getName());
                slot_index++;
                value_index++;
            }

        } else {
            createSlot(fatherElement, slotID, null, null, null,
                    this.insMap.get((UMLObjectIns) attr), null);
            slot_index++;
        }
    }

    public void createSpecification(Element fatherElement, String specID,
                                    String value) {
        Element specificaionElement = fatherElement.addElement("specification");
        specificaionElement.addAttribute("xmi:id", specID);
        specificaionElement.addAttribute("xmi:type", "uml:OpaqueExpression");
        Element bodyElement = this.doc.addElement("body");
        bodyElement.setText(value);
        specificaionElement.add(bodyElement);
    }

    public void writeToFile(String filePath) {
        for (UMLObjectIns uoi : this.umlObjectInsList) {
            this.insMap.put(uoi, "INS_ID_" + ins_index);
            ins_index++;
        }
        for (UMLObjectIns uoi : this.umlObjectInsList) {
            Element insSpecElement = createInstanceSpecification(
                    uoi.getQualifiedName() + "_" + this.insMap.get(uoi),
                    this.insMap.get(uoi),
                    Utility.INSTANCE.getElementID(uoi.getName(), null));
            Map<String, Object> propertyMap = uoi.getPropertyMap();
            for (String attrName : propertyMap.keySet()) {
                Object attr = propertyMap.get(attrName);
                if (attr instanceof Collection) {
                    Object[] attrs = ((Collection) attr).toArray();
                    for (Object object : attrs) {
                        createSlotFromUMLAttr(insSpecElement, object);
                    }
                } else
                    createSlotFromUMLAttr(insSpecElement, attr);

            }
        }
        XMLWriter writer = null;
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            writer = new XMLWriter(new FileWriter(filePath), format);
            writer.write(this.doc);
            writer.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
