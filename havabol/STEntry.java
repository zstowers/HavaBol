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

	@Override
	public String toString() {
		return "STEntry [symbol=" + symbol + ", primClassif=" + primClassif + "]";
	}
	
	
}
