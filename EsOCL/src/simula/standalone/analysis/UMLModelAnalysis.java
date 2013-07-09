package simula.standalone.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.management.ListenerNotFoundException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.PackageImpl;
import org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl;
import org.eclipse.uml2.uml.internal.impl.UMLPackageImpl;
import org.w3c.dom.Node;

import simula.standalone.modelinstance.AbstractUMLModelInstance;
import simula.standalone.modelinstance.RuntimeModelInstance;
import simula.standalone.modelinstance.UMLObjectInstance;
import simula.standalone.modelinstance.UMLPrimitivePropertyInstance;
import tudresden.ocl20.pivot.metamodels.uml2.internal.model.UML2TypePrimitiveType;
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
import tudresden.ocl20.pivot.standalone.metamodel.UMLMetamodel;

public class UMLModelAnalysis {

	UMLPrimitivePropertyInstance[] array_properties;

	List<UMLObjectInstance> objects;

	/** singleton instance */
	private static UMLModelAnalysis instance;

	public static UMLModelAnalysis INSTANCE = instance();

	private static UMLModelAnalysis instance() {

		if (instance == null) {
			instance = new UMLModelAnalysis();
		}
		return instance;
	}

	public List<UMLObjectInstance> getModelInstance(int[] values) {
		for (int i = 0; i < array_properties.length; i++) {
			// only deal with the int number
			if (array_properties[i].getType() == 0)
				array_properties[i].setValue("" + values[i]);
		}
		return objects;
	}

	public UMLPrimitivePropertyInstance[] getProperties(String inputModelPath) {
		objects = new ArrayList<UMLObjectInstance>();
		Resource resource = new XMIResourceImpl(
				URI.createFileURI("resources/model/royalsandloyals.uml"));
		Map<String, String> options = new HashMap<String, String>();
		options.put(XMIResource.OPTION_ENCODING, "UTF-8");
		try {
			resource.load(options);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PackageImpl rootObject = (PackageImpl) resource.getContents().get(0);
//		buildModelInstance(rootObject);

		UMLObjectInstance customer = new UMLObjectInstance("Customer", null);
		UMLPrimitivePropertyInstance age = new UMLPrimitivePropertyInstance(
				"age", "", 0);
		customer.addProperty("age", age);
		objects.add(customer);
		Collection<UMLPrimitivePropertyInstance> properties = new HashSet<UMLPrimitivePropertyInstance>();

		for (UMLObjectInstance uoi : objects) {
			properties.addAll(uoi.getPrimitivePropertyCollection());
		}
		int i = 0;
		array_properties = new UMLPrimitivePropertyInstance[properties.size()];
		for (UMLPrimitivePropertyInstance property : properties) {
			array_properties[i++] = property;
		}
		return array_properties;
	}

	public void buildModelInstance(PackageImpl rootObject) {
		List<org.eclipse.uml2.uml.Class> classList = new ArrayList<org.eclipse.uml2.uml.Class>();
		List<Association> assList = new ArrayList<Association>();
		List<Enumeration> enumList = new ArrayList<Enumeration>();

		EList<org.eclipse.uml2.uml.Element> pElements = rootObject
				.allOwnedElements();
		for (org.eclipse.uml2.uml.Element element : pElements) {
			if (element instanceof org.eclipse.uml2.uml.Class) {
				classList.add((org.eclipse.uml2.uml.Class) element);
			} else if (element instanceof Association) {
				assList.add((Association) element);
			} else if (element instanceof Enumeration) {
				enumList.add((Enumeration) element);
			}
		}

		for (Association ass : assList) {
			List<UMLObjectInstance> uoiList_2 = new ArrayList<UMLObjectInstance>();
			List<org.eclipse.uml2.uml.Property> ends = ass.getMemberEnds();
			String endType_1 = ((Class) ends.get(0).getType()).getName();
			String endType_2 = ((Class) ends.get(1).getType()).getName();
			Class cla_1 = getClass(classList, endType_1);
			UMLObjectInstance uoi_1 = buildUMLObjectInstance(cla_1);
			Class cla_2 = getClass(classList, endType_2);
			// Please modify the iteration time!!!!!!!
			for (int i = 0; i < 1; i++) {
				UMLObjectInstance uoi_2 = buildUMLObjectInstance(cla_2);
				uoi_2.addProperty(ends.get(0).getName(), uoi_1);
				uoiList_2.add(uoi_2);
			}
			uoi_1.addProperty(ends.get(1).getName(), uoiList_2);

		}
	}

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

	public List<UMLObjectInstance> getUMLObjectInstances(String className) {
		List<UMLObjectInstance> classList = new ArrayList<UMLObjectInstance>();
		for (UMLObjectInstance uoi : objects) {
			if (uoi.getName().equals(className))
				classList.add(uoi);
		}
		return classList;
	}

	public Class getClass(List<org.eclipse.uml2.uml.Class> classList,
			String className) {
		for (Class cla : classList) {
			if (cla.getName().equals(className))
				return cla;
		}
		return null;
	}

	public Enumeration getEnumeration(List<Enumeration> enumList,
			String enumName) {
		for (Enumeration enumeration : enumList) {
			if (enumeration.getName().equals(enumName))return enumeration;
		}
		return null;
	}

	public UMLObjectInstance buildUMLObjectInstance(Class cla) {
		UMLObjectInstance uoi = new UMLObjectInstance(cla.getName(), null);
		for (org.eclipse.uml2.uml.Property pp : cla.getAllAttributes()) {
			if (pp.getType() instanceof PrimitiveType
					|| pp.getType() instanceof Enumeration) {
				System.out.println(pp.getType().toString());
				UMLPrimitivePropertyInstance uppi = new UMLPrimitivePropertyInstance(
						pp.getName(), "", value4PPType(pp.getType()));
				uoi.addProperty(pp.getName(), uppi);
			}
		}
		objects.add(uoi);
		return uoi;
	}
}
