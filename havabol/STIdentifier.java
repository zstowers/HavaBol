package havabol;

/**
* STIdentifier.
*  Subclass STIdentifier attributes.
*       dclType     declaration type (Int, Float, String, Bool, Date)
*       structure   data structure (primitive, fixed array, unbound array)
*       parm        parameter type (not a parm, by reference, by value)
*       nonLocal    nonLocal base Address Ref (0 - local, 1 - surrounding, ..., k - surrounding, 99 - global)
*
* @author donte_000
*/
public class STIdentifier extends STEntry {
	
	
	public int dclType;
	public int nonLocal;
	String structure;
	String parm;
	
	public STIdentifier(String symbol, int primClassif, String structure, String parm, int dclType, int nonLocal)
	{
		super(symbol, primClassif);
		this.dclType = dclType;
		this.structure = structure;
		this.nonLocal = nonLocal;
	}
	
	

}