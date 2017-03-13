package havabol;

public class IntegerEntry extends StorageEntry {

	public int variableValue;
	
	public IntegerEntry(String variableName, int dataType, int variableValue)
	{
		super(variableName, dataType);
		this.variableValue = variableValue;
	}
	
	@Override
	public void replaceValue(StorageEntry entry, String variableValue, boolean isNegative)
	{
		((IntegerEntry)entry).variableValue = Numeric.getIntegerValue(variableValue);
		
		if(isNegative == true)
			((IntegerEntry)entry).variableValue = -((IntegerEntry)entry).variableValue;
			
		return;
	}
	
	@Override
	public String returnValueAsString()
	{
		
		return String.valueOf(variableValue);
	}


	@Override
	public String toString() {
		return "IntegerEntry [variableValue=" + variableValue + ", variableName=" + variableName + ", dataType="
				+ dataType + "]";
	}

}
