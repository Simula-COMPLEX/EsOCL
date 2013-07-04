package tudresden.ocl20.pivot.standalone.modelinstance;

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

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new AbstractUMLModelInstance(this.name, this.value);
	}

}
