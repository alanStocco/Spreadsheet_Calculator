//package com.Redmart.Alan.Spreadsheet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/* Analysis
 * A Spreadsheet is composed by n(columns) * n(rows) Cells
 * So in a matrix of Cells
 * A Cell have:
 *   - a "name" example A1, A2, B1 that identify the position on the coordinates in the Spreadsheet 
 *   - a value:   
 * A Cell X have a cylic dependencies if refer to another Cell Y  and Cell Y refer to Cell X(or 
 * refer to Cell Z that refer to Cell X also not directly)
 * So we can split the Cells in two groups depending of how is the value:
 * The value of a Cell can contains:
 *         - numbers (ex 2)
 *         - operators (ex +)
 *         - reference to the value of other Cells (ex A3)
 * So we can create a Map(ex HashMap) with key the name of the Cell, ex A1, and value the Cells used/referenced
 * And another Map with key the name of the Cell and value the "plain" value of the cell.
 * And the beginning only the direct Cell then also the indirect when calculate
 * So if the size of the second Map is not equal the size of all the Cells in Spreadsheet we have a cylic dependencies       
 * 
 * We should manage RPN notation
 * And these operators: +,-./,*
 * And then ++ and --
 * 
 * For negative value  if find a - should check if after there is a number
 * For ++ and -- when we find a + or - should check if after there is another operator
 * */


public class Spreadsheet {

	private int nColumns;
	private int mRows;
	private Cell[][] cells;
	boolean circularDependent;
	private final HashMap<String, LinkedHashSet<String>> dependenciesMap;
	private final HashMap<String, Double> numericValuesMap;
	private Scanner inputSc;	
	//private LinkedHashSet<Cell> unresolved;  //way to solve abandoned for a faster one

	public Spreadsheet() {		
		inputSc = new Scanner(System.in);
		cells = new Cell[1][1];
		circularDependent = false;
		dependenciesMap = new HashMap<String, LinkedHashSet<String>>();
		numericValuesMap = new HashMap<String, Double>();
	}
	
	public static void main(String[] args) {
		Spreadsheet spreadsheet = new Spreadsheet();
		// read and evaluate input
		spreadsheet.readInput();
		// evaluate spreadsheet
		spreadsheet.evaluate();			
	}
	
	private void evaluate() {		
		for (int i = 0; i < mRows; i++) {
			for (int j = 0; j < nColumns; j++) {
				Cell currentCell = cells[i][j];
				evaluateCell(currentCell, null);			
			}
		}
        System.out.println(nColumns + " " + mRows);
		for (int i = 0; i < mRows; i++) {
			for (int j = 0; j < nColumns; j++) {				
				System.out.println( String.format("%.5f", cells[i][j].getVal() ) );                			}
		}
		
	}
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
        
	public Double evaluateCell(Cell currentCell, Set<Cell> currentStack) {
		// currentStack used for check cyclics
		if(currentStack == null)
			currentStack = new LinkedHashSet<Cell>();		 
		Double finalVal = null;
        String cellRefName = "";
		if(currentCell.getVal() != null) {
			finalVal = currentCell.getVal();
		}else if( !currentStack.contains(currentCell) ){
			currentStack.add(currentCell);			
			// replace the cell names with values 									
            List<String> listRef = new ArrayList<String>(currentCell.getReferences());
            while(currentCell.getVal() == null){
                for (String temp : listRef) {
                        cellRefName =  temp;
				        currentCell.refreshInsideContent(cellRefName, evaluateCell(getCell(cellRefName),currentStack));				    		  		                    
                    }                                                    
		    }	
            finalVal = currentCell.getVal();
		}else { 
			System.out.println("Cyclic(dependencies detected. Cell postion: " +currentCell.getCellName()+ " and content: "+currentCell.getContent());
			System.exit(1);
		}		
		return finalVal;		
	}
	
	 private int getNumberFromChar(char c) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return alphabet.indexOf(c);
    }

    
	private Cell getCell(String cellRefName) {        
		int i = 0;
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(cellRefName);
		int n = 0;			
		if(m.find())			
			n = Integer.parseInt(m.group())-1;				            
        i = getNumberFromChar(cellRefName.charAt(0));
		
		return cells[i][n];
	}

	//Read input
	public void readInput() {
		try {
			String[] dimensions;
			// Matrix dimensions
			if (inputSc.hasNextLine()) {
				dimensions = inputSc.nextLine().split(" ");
			    nColumns = Integer.parseInt(dimensions[0]);
				mRows = Integer.parseInt(dimensions[1]);				
			}								
			int countColumns , countRows, countTotal;			
			countColumns = countRows = countTotal = 0;			
			cells = new Cell[mRows][nColumns];
		
			while (countTotal < nColumns*mRows) {
				// line what is write inside the Cell
				String line = inputSc.nextLine().trim().toUpperCase();		
				Cell cell = new Cell(countRows+1, countColumns+1, line);								
				cells[countRows][countColumns] = cell;
				// If the cell contains a reference to another Cell add it to dependenciesMap				
				if (!cell.haveReferences()) {					
					numericValuesMap.put(cell.getCellName(), cell.getVal());	
				}else {
					dependenciesMap.put(cell.getCellName(), cell.getReferences()); // not used, changed way to solve
				}	
				countTotal++;
				countColumns++;
				if(countColumns == nColumns) {
					countColumns = 0;
					countRows++;
				}
			}
			inputSc.close();			
		} catch(NumberFormatException e){
	    	System.out.println("Invalid size in input.");			
			System.exit(1);
	    } catch (RuntimeException re) {
			System.out.println("Error occurred in while reading values");
			System.out.println(re);
			System.exit(1);
		} finally {
			inputSc.close();
		}
	}
	
	/*
	public boolean haveCyclic() {		
		return circularDependent;
	}*/
	
}
