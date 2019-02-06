//package com.Redmart.Alan.Spreadsheet;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Cell {

	private final int row;
	private final int col;
	private String inside;
	private final LinkedHashSet<String> referencesOtherCells;
	private final Pattern cellNamePattern = Pattern.compile("([A-Z]+)(\\d+)");
	private Double value ;	

	public Cell(int row, int column, String line) {
		this.row = row;
		this.col = column;
		this.inside = line;
		this.referencesOtherCells = new LinkedHashSet<String>();
		this.value = null;			
		Matcher matcher = cellNamePattern.matcher(inside);		
		// Check if the Cell content have the name of another Cells, ex A1, B1 ecc		
		try {
			if (matcher.find()) {				
				this.setReferences();
			}else {				
				this.setVal();
			}
		}catch (RuntimeException re) {
			System.out.println("Error occurred initializing Cell");
			System.out.println(re);
			System.exit(1);
		} 			
	}
	
		private void setVal() {				
		Stack<Double> components = new Stack<Double>();
		int i = 0;
		String[] cellContentSplits = inside.split(" ");
        try{
            while (i < cellContentSplits.length) {
                String component = cellContentSplits[i];
                if (component.equals("+"))  	    		                    
                        components.push(components.pop() + components.pop());                
                else if (component.equals("*")) 
                    components.push(components.pop() * components.pop());
                else if (component.equals("/")){
                    double divisor = components.pop();
                    double dividend = components.pop();               	
                    components.push( dividend / divisor);
                } 
                else if (component.equals("-")){
                    if(i+1 < cellContentSplits.length &&  isNumeric(cellContentSplits[i+1])){ // negative
                        components.push(-components.pop());
                        i++;
                    }else {
                        double subtrathend = components.pop();
                        double minuend = components.pop();               	
                        components.push( minuend - subtrathend);
                    }             	            	            
                }
                else if (isNumeric(component)) 
                    components.push(Double.parseDouble(component));
                else if (component.equals("++"))
                    components.push(components.pop() + 1);                
                else if(component.equals("--"))
                    components.push(components.pop() - 1);                
                else {
                    // Should never enter here because means that is a reference to another cell                     
                    System.out.println("Cell component KO: " +component);  
                    throw new IllegalArgumentException("Invalid Cell");
                }
                i++;
            }
        }catch (RuntimeException re) {
			System.out.println("Error occurred setting the value of Cell");
			System.out.println(re);
			System.exit(1);
		}
        value = components.pop();
		
	}

    
	
    
	private void setReferences() {			
        Map<String, Integer> mapOperators = new HashMap<String, Integer>();
        mapOperators.put("+", 1);
        mapOperators.put("-", 1);
        mapOperators.put("*", 1);
        mapOperators.put("/", 1);
        String[] cellContentSplits = inside.split(" ");
        final String regex = "([a-zA-Z]+)(\\d+)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            
        for(int i=0; i < cellContentSplits.length; i++) {
        	String component = cellContentSplits[i];        	        	
        	Matcher matcher = pattern.matcher(component.trim());        	
        	if(!mapOperators.containsKey(component) && matcher.matches())         		
        		referencesOtherCells.add(component);        		
        	
        }    	
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  
	}

	public boolean haveReferences() {		
		return referencesOtherCells.size() > -1 ? true : false;
	}
	
	public boolean isCalculated() {
		return value != null ? true : false;
	}
	
	public String getCellName() {
		return getCharForNumber(row)+col;
	}

	public LinkedHashSet<String> getReferences() {
		return referencesOtherCells;
	}	

	public Double getVal() {		
		return value;
	}
	
	public void setRefreshVal() {
		setVal();
	}
		
    private void setRefreshInside(String newCellInside){
        inside = newCellInside;
    }
    
	public void refreshInsideContent(String cellRefName, Double val) {		
		String temp = inside.replaceAll(cellRefName.trim(), val.toString());
		inside = temp;		
		referencesOtherCells.remove(cellRefName);
		if(referencesOtherCells.size() > 0){
			setRefreshInside(inside);
        }else{
        	setRefreshVal();
        }		
	}
	
	private String getCharForNumber(int i) {
	    return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
	}

	public String getContent() {		
		return inside;
	}
}

