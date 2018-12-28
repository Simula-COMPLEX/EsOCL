package simula.standalone.modelinstance;

import tudresden.ocl20.pivot.pivotmodel.Type;

public class UMLAttributeIns extends AbstUMLModelIns {

	private Type type;
	
	public UMLAttributeIns(String name, String value) {
		super(name, value);

	}
	
	

	public Type getType() {
		return type;
	}



	public void setType(Type type) {
		this.type = type;
	}



	public String getClassName() {
		String[] qualifiedNames = this.qualifiedName.split("::");
		return qualifiedNames[qualifiedNames.length - 2];
	}

}
