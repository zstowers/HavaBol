package havabol;

public class StorageEntry {

	public String variableName;
	//public String variableValue;
	public int dataType;
	
	public StorageEntry(String variableName, int dataType)
	{
		
		this.variableName = variableName;
		//this.variableValue = variableValue;
		this.dataType = dataType;
		
	}
	
	
	
	

	
	public String toString() {
		return "StorageEntry [variableName=" + variableName + ", dataType="
				+ dataType + "]";
	}
	
	
}
