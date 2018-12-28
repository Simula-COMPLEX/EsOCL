package simula.oclga;

public class GeneValueScope {

	public int minValue;
	
	public int maxValue;
	
	enum EncodedConstraintType {String, Double};
	
	public EncodedConstraintType encodedConstraintType;

	public int type;  //thye value of the type is consistent with the value defined in Problem
	
	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public EncodedConstraintType getEncodedConstraintType() {
		return encodedConstraintType;
	}

	public void setEncodedConstraintType(EncodedConstraintType encodedConstraintType) {
		this.encodedConstraintType = encodedConstraintType;
	}

}
