package havabol;

/**
 * STControl.
 *  Subclass STControl attributes.
 *      subClassif     subClassification (flow, end, declare)
 * 
 * @author donte_000
 * <p>
 */

public class STControl extends STEntry {
	//Subclassification of the given token 
	public int subClassif;
	
	public STControl(String symbol, int primaryClassif, int subClassif)
	{
		//Constructor for variable initializing 
		super(symbol, primaryClassif);
		this.subClassif = subClassif;
	}
	

}
