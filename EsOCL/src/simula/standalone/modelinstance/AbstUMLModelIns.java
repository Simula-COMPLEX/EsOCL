package simula.standalone.modelinstance;

public class AbstUMLModelIns {

	String name;
	String value;

	public AbstUMLModelIns(String name, String value) {
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
		return new AbstUMLModelIns(this.name, this.value);
	}

}
