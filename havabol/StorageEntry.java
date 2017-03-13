package havabol;

public abstract class StorageEntry {

	public String variableName;
	//public String variableValue;
	public int dataType;
	
	
	public StorageEntry(String variableName, int dataType)
	{
		
		this.variableName = variableName;
		//this.variableValue = variableValue;
		this.dataType = dataType;
		
	}
	
	
	
	
	public abstract void replaceValue(StorageEntry entry, String variableValue, boolean isNegative);
	
	
	public abstract String returnValueAsString();

	

	
	public String toString() {
		return "StorageEntry [variableName=" + variableName + ", dataType="
				+ dataType + "]";
	}


	
	
	
	
}
