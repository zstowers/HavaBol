package havabol;

import java.io.*;
import java.util.Map;


public class HavaBol {

	public static void main(String[] args) throws IOException {
		
		
		SymbolTable globalSymbolTable; 
	    Scanner scan;	
	    Parser parser;
	    StorageManager storageManager;
	    long startTime = System.currentTimeMillis();
		long endTime;
		long totalTime;
		
		try
		{
			//Print a column heading 
		//	System.out.printf("%-11s %-12s %s\n"
		//			, "primClassif"
		//			, "subClassif"
		//			, "tokenStr");
			
			globalSymbolTable = new SymbolTable();
			storageManager = new StorageManager();
		    
			scan = new Scanner(args[0], globalSymbolTable);
			parser = new Parser(scan, globalSymbolTable, storageManager);
			
			
			for(int i = 0; i < 61 ; i++)
			{
				parser.statement();
				//scan.currentToken.printToken();
			}
			//scan.currentToken.printToken();
			//while (! scan.getNext().isEmpty())
			//{
				//Call hexPrint if the token is a string literal, otherwise, call printToken 
			//	if(scan.currentToken.subClassif == 5)
			//		scan.currentToken.hexPrint();
			//	else
			//		scan.currentToken.printToken();
			//}
			
			HavabolUtilities.printSymbolTable(parser.symbolTable.symbolTable);
			HavabolUtilities.printStorage(parser.storageManager.storageManager);
			
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
