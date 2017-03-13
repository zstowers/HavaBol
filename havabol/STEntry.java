package havabol;

/**
* STEntry.
*   Class STEntry attributes.
*       symbol          string for the symbol
*       primClassif     primary classification of the symbol
* <p>
**/

public class STEntry {
	//String for the symbol and primary classification of the symbol 
	public String symbol;
	public int primClassif;
	
	public STEntry(String symbol, int primClassif)
	{
		//Initialize variables upon object creation 
		this.symbol = symbol;
		this.primClassif = primClassif;
	}
	
	
	
	public static int returnSubClass(STEntry entry)
	{
		if(entry instanceof STIdentifier)
			return ((STIdentifier) entry).dclType;
		else 
			return 0;
	}
	

	@Override
	public String toString() {
		return "STEntry [symbol=" + symbol + ", primClassif=" + primClassif + "]";
	}
	
	
}
