package havabol;

public class IntegerEntry extends StorageEntry {

	public int variableValue;
	
	public IntegerEntry(String variableName, int dataType, int variableValue)
	{
		super(variableName, dataType);
		this.variableValue = variableValue;
	}
	
	public void replaceValue(IntegerEntry entry, int intValue)
	{
		entry.variableValue = intValue;
		return;
	}


	@Override
	public String toString() {
		return "IntegerEntry [variableValue=" + variableValue + ", variableName=" + variableName + ", dataType="
				+ dataType + "]";
	}

}
