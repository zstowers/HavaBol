package havabol;

import java.util.ArrayList;
import java.util.HashMap;
/**
* STFunction.
*  Subclass STFunction attributes.
*       returnType          Return data type (Int, Float, String, Bool, Date, Void)
*       definedBy           What defined it (user, builtin)
*       numArgs             The number of arguments.  For variable length, VAR_ARGS.
*       parmList            reference to an ArrayList of formal parameters
*       userSymbolTable     reference to the function's symbol table if it is a user-defined function
*       userTable           reference to the global hash table that stores every user function's symbol table
*                           with the user functions' symbol
* @author donte_000
* <p>
**/
public class STFunction extends STEntry{
    
    //Above attibutes described in above program header.
    int returnType, definedBy, numArgs, varLength, j = 0;
    String list;
    ArrayList<String> parmList = new ArrayList<String>();
    
    //User defined symbol table reference variable
    HashMap<String, STEntry> userSymbolTable;

    //Builtin and user defined functions constructor
    public STFunction(String symbol, int primClassif, int returnType, int definedBy, int numArgs, String... list){
       
        //Constructor for variable initializing
        super(symbol, primClassif);
        this.returnType = returnType;
        this.definedBy = definedBy;
        
        //For variable length, VAR_ARGS.
        this.numArgs = numArgs;
        
        if(definedBy == Token.USER){
            userSymbolTable = new HashMap<>();
            // Store the user's symbol table in the global static HashTable (table) 
            // with the name of the user defined function...
            SymbolTable.table.put(symbol, userSymbolTable);
            
            for(String str : list){
                parmList.add(str);
                System.out.println(parmList.get(j));
                j++;
            }
        }
    }
            
/**
 * putUserSymbol.
 *  Stores the symbol and its corresponding entry in the user
 *  defined symbol table or prints error message if hash-map
 *  already contains the entry value specified by the key.
 * <p>
 * 
 * @param   symbol to be tested before putting in the user symbol table
 * @param   entry, the STEntry value to be in the symbol table with the corresponding symbol
**/
    void putUserSymbol(String symbol, STEntry entry){
        if( userSymbolTable.containsKey(symbol) )
            System.out.println("User symbol table already contains the specified key");
        else
            userSymbolTable.put(symbol, entry);
    }
/**
 * getUserSymbol.
 *  Returns the symbol table entry for the given symbol in the user
 *  symbol table or null if the mapping of the value from the specified
 *  key cannot be found.
 * <p>
 * 
 * @param       symbol to be tested in the table 
 * 
 * @return      null if the specified symbol does not contain a STEntry in the user table.
 * @return      userSymbolTable.get(symbol), the corresponding STEntry in the user table.
**/
    STEntry getUserSymbol(String symbol){
        if( userSymbolTable.containsKey(symbol) )
            return userSymbolTable.get(symbol);
        else
            System.out.println("User symbol table does not contain the specified key");
        
        return null;
    }   
}