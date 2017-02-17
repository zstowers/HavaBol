package havabol;

import java.util.*;

public class SymbolTable {

	public HashMap<String, STEntry> symbolTable = new HashMap<String, STEntry>();
	
	public SymbolTable () {
		
		
		
		initGlobal();
	}
	
	/**
	 * 
	 */
	public void initGlobal()
	{
		symbolTable.put("def", new STControl("def", Token.CONTROL, Token.FLOW));
		symbolTable.put("enddef", new STControl("enddef", Token.CONTROL, Token.FLOW));
		symbolTable.put("if", new STControl("if", Token.CONTROL, Token.FLOW));
		symbolTable.put("endif", new STControl("endif", Token.CONTROL, Token.END));
		symbolTable.put("else", new STControl("else", Token.CONTROL, Token.END));
		symbolTable.put("for", new STControl("for", Token.CONTROL, Token.FLOW));
		symbolTable.put("endfor", new STControl("endfor", Token.CONTROL, Token.END));
		symbolTable.put("while", new STControl("while", Token.CONTROL, Token.FLOW));
		symbolTable.put("endwhile", new STControl("endwhile", Token.CONTROL, Token.END));
		symbolTable.put("print", new STFunction("print", Token.FUNCTION, Token.VOID, Token.BUILTIN));
		symbolTable.put("Int", new STControl("Int", Token.CONTROL, Token.DECLARE));
		symbolTable.put("Float", new STControl("Float", Token.CONTROL, Token.DECLARE));
		symbolTable.put("String", new STControl("String", Token.CONTROL, Token.DECLARE));
		symbolTable.put("Bool", new STControl("Bool", Token.CONTROL, Token.DECLARE));
		symbolTable.put("Date", new STControl("Date", Token.CONTROL, Token.DECLARE));
		symbolTable.put("LENGTH", new STFunction("LENGTH", Token.FUNCTION, Token.INTEGER, Token.BUILTIN));
		symbolTable.put("MAXLENGTH", new STFunction("MAXLENGTH", Token.FUNCTION, Token.INTEGER, Token.BUILTIN));
		symbolTable.put("SPACES", new STFunction("SPACES", Token.FUNCTION, Token.INTEGER, Token.BUILTIN));
		symbolTable.put("ELEM", new STFunction("ELEM", Token.FUNCTION, Token.INTEGER, Token.BUILTIN));
		symbolTable.put("MAXELEM", new STFunction("MAXELEM", Token.FUNCTION, Token.INTEGER, Token.BUILTIN));
		symbolTable.put("and", new STEntry("and", Token.OPERATOR));
		symbolTable.put("or", new STEntry("or", Token.OPERATOR));
		symbolTable.put("not", new STEntry("not", Token.OPERATOR));
		symbolTable.put("in", new STEntry("in", Token.OPERATOR));
		symbolTable.put("notin", new STEntry("notin", Token.OPERATOR));
		symbolTable.put("T", new STEntry("T", Token.OPERAND));
		symbolTable.put("F", new STEntry("F", Token.OPERAND));
	}
	
	/**
	 * 
	 * @param symbol
	 * @return
	 */
	public STEntry getSymbol(String symbol)
	{
		if(symbolTable.containsKey(symbol))
			return symbolTable.get(symbol);
		else
			return null;
	}
	
	
	/**
	 * 
	 * @param symbol
	 * @param entry
	 */
	public void putSymbol(String symbol, STEntry entry)
	{
		if(symbolTable.containsKey(symbol))
			System.out.println("HashMap already contains the specified key");
		else
			symbolTable.put(symbol, entry);
	}
	
	
}
