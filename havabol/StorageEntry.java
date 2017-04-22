package havabol;

import java.util.ArrayList;

public class StorageEntry {

	public String variableName;
	public String variableValue;
	public int dataType;
	public ArrayList<ResultValue> valueList;
	
	
	public StorageEntry(String variableName, int dataType, String variableValue, ArrayList<ResultValue> valueList)
	{
		this.variableName = variableName;
		this.dataType = dataType;
		this.variableValue = variableValue;
		this.valueList = valueList;
	}


	@Override
	public String toString() {
		return "StorageEntry [variableName=" + variableName + ", variableValue=" + variableValue + ", dataType="
				+ dataType + ", valueList=" + valueList + "]";
	}
	
	
	
	
	
	
}
	
