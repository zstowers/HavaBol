package havabol;

public class BooleanEntry extends StorageEntry {

	public String variableValue;
	
	public BooleanEntry(String variableName, int dataType, String variableValue) 
	{
		super(variableName, dataType);
		this.variableValue = variableValue;
		
		
	}

	@Override
	public void replaceValue(StorageEntry entry, String variableValue, boolean isNegative) {
		// TODO Auto-generated method stub
		 
			 this.variableValue = variableValue;
		 
	}

	
	@Override
	public String returnValueAsString() {
		// TODO Auto-generated method stub
		return String.valueOf(variableValue);
	}
	
	
}
