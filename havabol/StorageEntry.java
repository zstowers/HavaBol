package havabol;

public class StorageEntry {

	public String variableName;
	public String variableValue;
	public int dataType;
	
	
	public StorageEntry(String variableName, int dataType, String variableValue)
	{
		this.variableName = variableName;
		this.dataType = dataType;
		this.variableValue = variableValue;
	}


	@Override
	public String toString() {
		return "StorageEntry [variableName=" + variableName + ", variableValue=" + variableValue + ", dataType="
				+ dataType + "]";
	}
	
	
	
	
	
	
}
	
