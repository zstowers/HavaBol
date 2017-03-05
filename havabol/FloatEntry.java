package havabol;

public class FloatEntry extends StorageEntry {

	public double variableValue;
	
	
	public FloatEntry(String variableName, int dataType, double variableValue) {
		
		super(variableName, dataType);
		this.variableValue = variableValue;
		
	}
	
	public void replaceValue(FloatEntry entry, double doubleValue)
	{
		entry.variableValue = doubleValue;
		return;
	}


	@Override
	public String toString() {
		return "FloatEntry [variableValue=" + variableValue + ", variableName=" + variableName + ", dataType="
				+ dataType + "]";
	}


	

	
	

}
