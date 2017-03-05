package havabol;

public class StringEntry extends StorageEntry {

public String variableValue;
	
	public StringEntry(String variableName, int dataType, String variableValue)
	{
		super(variableName, dataType);
		this.variableValue = variableValue;
	}
	
	public void replaceValue(StringEntry entry, String variableValue)
	{
		entry.variableValue = variableValue;
		return;
	}

	@Override
	public String toString() {
		return "StringEntry [variableValue=" + variableValue + ", variableName=" + variableName + ", dataType=" + dataType + "]";
	}
	
	

}
