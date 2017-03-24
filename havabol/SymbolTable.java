package havabol;

import java.util.HashMap;
import java.util.Hashtable;



/**
* SymbolTable.
*    Provides methods for maintaining symbol tables during compilation/execution.  
*    based on the source program, the Parser/Executor is responsible for populating
*    the actual Symbol Tables internal to the Symbol Table Class.
*    
*    The Scanner uses the Symbol Table Class' information:
*       Operand re-classification as a CONTROL (e.g., if, else, for, while) FUNCTION, or OPERATOR
*       Whether the identifier has been declared
*       Static Scope information
* <p>
**/
public class SymbolTable {
   /**
    * Actual internal table used to stored the
    * different types of symbol tables(global, user defined)
    **/
    public static Hashtable<String, HashMap<String, STEntry>> table = new Hashtable<>();
    
    static //Global symbol table
    HashMap<String, STEntry> symbolTable = new HashMap<>();
    
    int VAR_ARGS;

    public SymbolTable(){
        
        //Store the symbol table in the corresponding hash table, (and initialize it)..
        table.put( "global", symbolTable );
        initGlobal();
    }
/**
 * initGlobal.
*   Method solely used to create the hash map that is used
*   to hold the values (classifications of the tokens in a 
*   table.Inserts reserved symbol entries into the global symbol table.
* <p>
**/
    private void initGlobal(){
        //Add HavaBol reserved symbols
        //which are in the Global Symbol Table for every HavaBol Source.
        symbolTable.put( "def", new STControl("if", Token.CONTROL, Token.FLOW) );
        symbolTable.put( "enddef", new STControl("enddef", Token.CONTROL, Token.END) );
        symbolTable.put( "if", new STControl("if", Token.CONTROL, Token.FLOW) );
        symbolTable.put( "endif", new STControl("endif", Token.CONTROL, Token.END) );
        symbolTable.put( "else", new STControl("else", Token.CONTROL, Token.END) );
        symbolTable.put( "for", new STControl("for", Token.CONTROL, Token.FLOW) );
        symbolTable.put( "endfor", new STControl("endfor", Token.CONTROL, Token.END) );
        symbolTable.put( "while", new STControl("while", Token.CONTROL, Token.FLOW) );
        symbolTable.put( "endwhile", new STControl("endwhile", Token.CONTROL, Token.END) );
        symbolTable.put( "print", new STFunction("print", Token.FUNCTION, Token.VOID, Token.BUILTIN, VAR_ARGS) );
        symbolTable.put( "Int", new STControl("Int", Token.CONTROL, Token.DECLARE) );
        symbolTable.put( "Float", new STControl("Float", Token.CONTROL, Token.DECLARE) );
        symbolTable.put( "String", new STControl("String", Token.CONTROL, Token.DECLARE) );
        symbolTable.put( "Bool", new STControl("Bool", Token.CONTROL, Token.DECLARE) );
        symbolTable.put( "Date", new STControl("Date", Token.CONTROL, Token.DECLARE) );
        symbolTable.put( "LENGTH", new STFunction("LENGTH", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS) ); 
        symbolTable.put( "MAXLENGTH", new STFunction("MAXLENGTH", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS) ); 
        symbolTable.put( "SPACES", new STFunction("SPACES", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS) );  
        symbolTable.put( "ELEM", new STFunction("ELEM", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS) );  
        symbolTable.put( "MAXELEM", new STFunction("MAXELEM", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS) ); 
        symbolTable.put( "and", new STEntry("and", Token.OPERATOR) );
        symbolTable.put( "or", new STEntry("or", Token.OPERATOR) );
        symbolTable.put( "not", new STEntry("not", Token.OPERATOR) );
        symbolTable.put( "in", new STEntry("in", Token.OPERATOR) );
        symbolTable.put( "notin", new STEntry("notin", Token.OPERATOR) );
    }

/**
 * putSymbol.
 *  Stores the symbol and its corresponding entry in the global symbol table.
 *  or prints error message if hash-map already contains the entry
 *  value specified by the key.
 * <p>
**/
    void putSymbol(String symbol, STEntry entry){
        if( symbolTable.containsKey(symbol) )
            System.out.println("HashMap already contains the specified key");
        else
            symbolTable.put(symbol, entry);
    }
/**
 * getSymbol.
 *  Returns the symbol table entry for the given symbol.
 *  or null if the mapping of the value from the specified
 *  key cannot be found in the global symbol table.
 * <p>
 * 
 * @param       symbol to be tested in the table 
 * 
 * @return      null if the specified symbol does not contain a STEntry in the table.
 * @return      symbolTable.get(symbol), the corresponding STEntry in the table.
**/
    public static STEntry getSymbol(String symbol){
        if( symbolTable.containsKey(symbol) )
            return symbolTable.get(symbol);
       // else
            //System.out.println("HashMap does not contain the specified key");
        
        return null;
    }
    
    
   
    
}//EoC