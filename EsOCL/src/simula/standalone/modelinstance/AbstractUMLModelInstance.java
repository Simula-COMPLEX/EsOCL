package simula.standalone.modelinstance;

public class AbstractUMLModelInstance {

	String name;
	String value;

	public AbstractUMLModelInstance(String name, String value) {
		this.name =  name;
		this.value = value;
	}
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	

	public void setValue(String value) {
		this.value = value;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new AbstractUMLModelInstance(this.name, this.value);
	}

}
