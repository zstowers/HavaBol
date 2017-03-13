package havabol;

public class FloatEntry extends StorageEntry {

	public double variableValue;
	
	
	public FloatEntry(String variableName, int dataType, double variableValue) {
		
		super(variableName, dataType);
		this.variableValue = variableValue;
		
	}
	
	@Override 
	public void replaceValue(StorageEntry entry, String variableValue, boolean isNegative)
	{
		((FloatEntry)entry).variableValue = Numeric.getDoubleValue(variableValue);
		
		if(isNegative == true)
			((FloatEntry)entry).variableValue = -((FloatEntry)entry).variableValue;
		
		return;
	}

	@Override
	public String returnValueAsString()
	{
		return String.valueOf(variableValue);
	}


	@Override
	public String toString() {
		return "FloatEntry [variableValue=" + variableValue + ", variableName=" + variableName + ", dataType="
				+ dataType + "]";
	}


	

	
	

}
