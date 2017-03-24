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
			globalSymbolTable = new SymbolTable();
			storageManager = new StorageManager();
		    
			scan = new Scanner(args[0], globalSymbolTable);
			parser = new Parser(scan, globalSymbolTable, storageManager);
			
			
			parser.statements();
				

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
