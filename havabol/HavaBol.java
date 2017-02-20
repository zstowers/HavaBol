package havabol;

import java.io.*;


public class HavaBol {

	public static void main(String[] args) throws IOException {
		
		
		SymbolTable globalSymbolTable; 
	    Scanner scan;	
		long startTime = System.currentTimeMillis();
		long endTime;
		long totalTime;
		
		try
		{
			//Print a column heading 
			System.out.printf("%-11s %-12s %s\n"
					, "primClassif"
					, "subClassif"
					, "tokenStr");
			
			globalSymbolTable = new SymbolTable();
		    scan = new Scanner(args[0], globalSymbolTable);
			
			
			
		
			
			while (! scan.getNext().isEmpty())
			{
				//Call hexPrint if the token is a string literal, otherwise, call printToken 
				if(scan.currentToken.subClassif == 5)
					scan.currentToken.hexPrint();
				else
					scan.currentToken.printToken();
			}
			
			endTime = System.currentTimeMillis();
			totalTime = (endTime - startTime) / 1000;
			
			
			System.out.println("BUILD SUCCESSFUL (total time: "+ totalTime + " seconds)");
		
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
