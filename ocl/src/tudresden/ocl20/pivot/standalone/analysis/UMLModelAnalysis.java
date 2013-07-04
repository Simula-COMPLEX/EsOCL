package tudresden.ocl20.pivot.standalone.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.management.ListenerNotFoundException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Node;

import tudresden.ocl20.pivot.model.IModel;
import tudresden.ocl20.pivot.model.ModelAccessException;
import tudresden.ocl20.pivot.modelinstance.IModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.exception.PropertyAccessException;
import tudresden.ocl20.pivot.modelinstancetype.exception.PropertyNotFoundException;
import tudresden.ocl20.pivot.modelinstancetype.exception.TypeNotFoundInModelException;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceCollection;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceElement;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.modelinstancetype.xml.internal.modelinstance.XmlModelInstance;
import tudresden.ocl20.pivot.pivotmodel.Property;
import tudresden.ocl20.pivot.pivotmodel.Type;
import tudresden.ocl20.pivot.standalone.modelinstance.AbstractUMLModelInstance;
import tudresden.ocl20.pivot.standalone.modelinstance.RuntimeModelInstance;
import tudresden.ocl20.pivot.standalone.modelinstance.UMLObjectInstance;
import tudresden.ocl20.pivot.standalone.modelinstance.UMLPrimitivePropertyInstance;

public class UMLModelAnalysis {

	Document umlModelDoc;

	public UMLModelAnalysis(File umlModelFile) {
		SAXReader sax = new SAXReader();
		try {
			this.umlModelDoc = sax.read(umlModelFile);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IModelInstance getModelInstance(IModel model) {
		List<UMLObjectInstance> objects = new ArrayList<UMLObjectInstance>();
		UMLObjectInstance customer = new UMLObjectInstance("Customer", null);
		UMLPrimitivePropertyInstance age = new UMLPrimitivePropertyInstance(
				"age", "24", 1);
		UMLPrimitivePropertyInstance name = new UMLPrimitivePropertyInstance(
				"name", "BB", 1);
		customer.addProperty("age", age);
		customer.addProperty("name", name);
		UMLObjectInstance card_1 = new UMLObjectInstance("CustomerCard", null);
		UMLPrimitivePropertyInstance printedName_1 = new UMLPrimitivePropertyInstance(
				"printedName", "AA", 1);
		UMLPrimitivePropertyInstance valid_1 = new UMLPrimitivePropertyInstance(
				"valid", "true", 1);
		UMLObjectInstance validFrom_1 = new UMLObjectInstance("Date", null);
		UMLObjectInstance validThru_1 = new UMLObjectInstance("Date", null);
		card_1.addProperty("validFrom", validFrom_1);
		card_1.addProperty("validThru", validThru_1);
		card_1.addProperty("valid", valid_1);
		card_1.addProperty("printedName", printedName_1);
		card_1.addProperty("owner", customer);
		UMLObjectInstance card_2 = new UMLObjectInstance("CustomerCard", null);
		UMLObjectInstance validFrom_2 = new UMLObjectInstance("Date", null);
		UMLObjectInstance validThru_2 = new UMLObjectInstance("Date", null);
		card_2.addProperty("validFrom", validFrom_2);
		card_2.addProperty("validThru", validThru_2);
		UMLPrimitivePropertyInstance printedName_2 = new UMLPrimitivePropertyInstance(
				"printedName", "AA", 1);
		UMLPrimitivePropertyInstance valid_2 = new UMLPrimitivePropertyInstance(
				"valid", "false", 1);
		card_2.addProperty("valid", valid_2);
		card_2.addProperty("printedName", printedName_2);
		card_2.addProperty("owner", customer);
		List<AbstractUMLModelInstance> cards = new ArrayList<AbstractUMLModelInstance>();
		cards.add(card_1);
		cards.add(card_2);
		customer.addProperty("cards", cards);
		objects.add(customer);
		objects.add(card_1);
		objects.add(card_2);
		objects.add(validFrom_1);
		objects.add(validThru_1);
		objects.add(validFrom_2);
		objects.add(validThru_2);

		RuntimeModelInstance instance = new RuntimeModelInstance(model, objects);
		return instance;
	}

	public List<String> getClassNames() {
		List<String> classNames = new ArrayList<String>();
		List<Element> classNodes = this.umlModelDoc
				.selectNodes("//packagedElement[@xmi:type='uml:Class']");
		for (Element element : classNodes) {
			classNames.add(element.attributeValue("name"));
		}
		return classNames;
	}

	public static void main(String[] args) {
		UMLModelAnalysis analysis = new UMLModelAnalysis(new File(
				"resources/model/royalsandloyals.uml"));
		analysis.getClassNames();
	}

}
