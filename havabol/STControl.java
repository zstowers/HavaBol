package havabol;

public class STControl extends STEntry {

	public int subClassif;
	
	public STControl(String symbol, int primaryClassif, int subClassif)
	{
		super(symbol, primaryClassif);
		this.subClassif = subClassif;
	}
	

}
