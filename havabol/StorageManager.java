package havabol;

import java.util.HashMap;
import java.util.HashMap;
import java.util.Objects;

/**
 * StorageManager
 * 		New storage manager class which handles :
 * 			Storing variable values 
 * 			Accessing variable values 
 * 			Errors due to invalid statement declarations 
 * 		<p>
 * 
 */

public class StorageManager {
	public static int ndx = 0, i = 0;
	public static String symbol;
	public static STEntry entry;
	public static Parser parse;
	public static ResultValue res;
	
	Token token = new Token();
	
	//ResultValue symbol table 
	public static HashMap<String, ResultValue> resultTable = new HashMap<>();
	public static HashMap<String, StorageEntry> storageManager = new HashMap<>();

	public StorageManager () {
		
	}



/**
 * accessVariable. 
 * 		Used to access the declared variable in the global symbol table.
 * 		If the variable is not declared the value returned for the entry 
 * 		is null
 * <p>
 * 
 * @param		symbol 	string to be searched in the symbol table 
 * @return 		null if the symbol is not found in the table 
 * @return 		entry from the symbol table for the corresponding symbol 
 */

	public static STEntry accessVariable(String symbol)
	{
		if((entry = SymbolTable.getSymbol(symbol)) == null)
			return null;
		
		return entry;
	}
	
	
	/**
	 *  isDeclared
	 *  	Used to check if the given symbol is declared or not 
	 *  	(found in the global symbol table).
	 *  <p>
	 *  
	 *  @param	symbol to be searched in the global symbol table for 
	 * 			declaration purposes 
	 *  @return	true if the symbol is declared 
	 *  @return false if the symbol is not declared  		
	 */
	public static boolean isDeclared(String symbol)
	{
		if(SymbolTable.getSymbol(symbol) != null)
			return true;
		else
			return false;
	}
	
	
	/**
	 * addVariable 
	 * 		Used to check if the given symbol is declared or not 
	 * 		(found in the global symbol table)
	 * <p>
	 * 
	 * @param 	name of the variable to be stored in the storageManager
	 * @param	entry of the corresponding variable to be stored in the 
	 * 			storageManager 
	 */
	public void addVariable(String name, StorageEntry entry)
	{
			// add the declared variable to the storage manager 
			storageManager.put(name, entry);
	}
	
	
	/**
	 *  changeValue
	 *  	Used to change the declared symbol's value with another
	 *  	value of the same type 
	 *  <p>
	 *  
	 *  @param key to be searched in the storageManager HashMap 
	 *  @param entry to be stored with the key in the storageManager HashMap
	 *  
	 *  @return false if no entry can be stored 
	 *  @return true if an entry can be stored 
	 */
	public boolean changeValue(String key, String value)
	{
		StorageEntry entry = null;
		
		if(StorageManager.isDeclared(key) == false)
		{
			System.out.println("Error, the variable \"" + key + "\", in which "
					+ "you are trying to change the value is not declared in the global"
					+ "symbol table");
			return false;
		}
		
		else
		{
			entry = getStorageEntry(key);
			entry.variableValue = value;
			return true;
		}
	}
	
	/**
	 * getStorageEntry
	 * 		Used to get the StorageEntry of the corresponding key 
	 * <p>
	 * 
	 * @param key to be searched in the storageManager HashMap 
	 * @return the corresponding StorageEntry of the specified key in the storageManager 
	 * 		   HashMap 
	 * @return null if the key does not have a corresponding StorageEntry 
	 */
	public static StorageEntry getStorageEntry(String key)
	{
		if(storageManager.containsKey(key))
			return (StorageEntry) storageManager.get(key);
		else
			return null;
	}
	
	
	/**
	 * Returns the datatype of the variable 
	 * @param key	The variable that we are searching for 
	 * @return	The data type of the variable as an integer
	 */
	public static int getDataType(String key)
	{
		StorageEntry entry = getStorageEntry(key);
		return entry.dataType;
	}
	
	
	
	
}


