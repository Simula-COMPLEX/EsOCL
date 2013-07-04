package tudresden.ocl20.pivot.standalone.modelinstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UMLObjectInstance extends AbstractUMLModelInstance {

	public UMLObjectInstance(String name, String value) {
		super(name, value);
		// TODO Auto-generated constructor stub
	}

	private Map<String, Object> propertyList = new HashMap<String, Object>();

	public Object getPropertyList(String propertyName) {
		return propertyList.get(propertyName);
	}

	public void addProperty(String propertyName, Object umlProperty) {
		propertyList.put(propertyName, umlProperty);
	}

}
