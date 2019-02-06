# Spreadsheet_Calculator
Simple Spreadsheet Calculator in Java - interview's test


A spreadsheet consists of a two dimensional array of cells, labeled A1, A2, etc. Rows are 
identified using letters, columns by numbers. Each cell contains either an integer  its value) or 
an expression. Expressions contain integers, cell references, and the operators '+', '-', '*', '/' 
with the usual rules of evaluation – note that the input is RPN and should be evaluated in stack 
order. 
The spreadsheet input is defined as follows: 
• Line 1: two integers, defining the width and height of the spreadsheet  n, m) 
• n*m lines each containing an expression which is the value of the corresponding cell 
 cells enumerated in the order A1, A2, A<n>, B1, ...) 
 Your program must output its data in the same format, but each cell should be reduced to a 
single floating8point value.  For example, we would expect the following expect to produce the 
indicated output:  
 
Input:

3 2

A2

4 5 *

A1

A1 B2 / 2 +

3

39 B1 B2 * /

Output 

3 2

20.0000

20.0000

20.0000

8.66667

3.00000

1.50000

Program should detect cyclic


