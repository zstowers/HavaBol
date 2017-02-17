package havabol;

import java.util.ArrayList;

public class STFunction extends STEntry{

	public int returnType;
	public int definedBy;
	public int numArgs;
	public ArrayList<String> parmList;
	public SymbolTable symbolTable;
	
	public STFunction(String symbol, int primaryClassif, int returnType, int definedBy)
	{
		super(symbol, primaryClassif);
		this.returnType = returnType;
		this.definedBy = definedBy;
	}
}
