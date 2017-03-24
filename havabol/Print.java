package havabol;

/**
 * A "print" statement, converts the result to a string, and displays it to 
 * the user.
 *<p>
 */

//assumes ResultValue toString() has been created 
public class Print {
	
	private ResultValue expression;
	
	
	public Print(ResultValue expression)
	{
		this.expression = expression;
		execute();
	}
	
	
	public void execute()
	{
		System.out.println(expression.toString());
	}

}
