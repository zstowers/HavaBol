package havabol;

public class StringEntry extends StorageEntry {

public String variableValue;
	
	public StringEntry(String variableName, int dataType, String variableValue)
	{
		super(variableName, dataType);
		this.variableValue = variableValue;
	}
	
	@Override
	public void replaceValue(StorageEntry entry, String variableValue, boolean isNegative)
	{
		//entry.variableValue = variableValue;
		((StringEntry)entry).variableValue = variableValue;
		return;
	}
	
	@Override
	public String returnValueAsString()
	{
		return variableValue;
	}

	@Override
	public String toString() {
		return "StringEntry [variableValue=" + variableValue + ", variableName=" + variableName + ", dataType=" + dataType + "]";
	}
	
	

}
