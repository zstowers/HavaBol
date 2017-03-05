package havabol;

import java.util.HashMap;

public class StorageManager {
	
	
	//private String name;
	//private String value;
	HashMap<String, StorageEntry> storageManager = new HashMap();
	
	public StorageManager ()
	{

	}
	
	
	public void addVariable(String name,  StorageEntry entry)
	{
		// if it is already there, replace the value 
		
		//If not, need to add it 
		storageManager.put(name, entry);
	}
	
	//public void changeValue(String key, String newValue)
	//{
	//	StorageEntry entry = getStorageEntry(key);
	//	entry.variableValue = newValue;
	//	storageManager.put(key, entry);
		
	//}
	
	public StorageEntry getStorageEntry(String key)
	{
		
		if(storageManager.containsKey(key))
			return (StorageEntry) storageManager.get(key);
		
		else 
			return null;
		
	}
	
	
	

}
