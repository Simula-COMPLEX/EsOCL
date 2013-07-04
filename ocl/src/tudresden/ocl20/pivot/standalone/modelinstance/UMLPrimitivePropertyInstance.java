package tudresden.ocl20.pivot.standalone.modelinstance;

public class UMLPrimitivePropertyInstance extends AbstractUMLModelInstance {
	int type;
	int maxVlaue, minValue;

	public UMLPrimitivePropertyInstance(String name, String value, int type) {
		super(name, value);
		this.type = type;
		this.maxVlaue = 100;
		this.minValue = -100;
	}

	public int getType() {
		return type;
	}

	public int getMaxVlaue() {
		return maxVlaue;
	}

	public int getMinValue() {
		return minValue;
	}

}
