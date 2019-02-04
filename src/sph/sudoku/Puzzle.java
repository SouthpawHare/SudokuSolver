package sph.sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Puzzle {
	
	private List< List <Square> > squares = new ArrayList< List <Square> >();
	
	private Puzzle() {
		
	}
	
	public static Puzzle createPuzzleFromInput(File puzzleFile) throws IOException {
		Puzzle puzzle = new Puzzle();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(puzzleFile));
			
			String line = null;
			do {
				List<Square> row = new ArrayList<Square>();
				puzzle.addRow(row);
				
				line = reader.readLine();
				if (line != null) {
					for (int i = 0; i < line.length(); i++) {
						String s = "" + line.charAt(i);
						Square square = new Square();
						try {
							int val = Integer.parseInt(s);
							square.setVal(val);
						}
						catch (NumberFormatException ex) {
						}
						row.add(square);
					}
				}
			} while (line != null);
			
			if (!puzzle.isValidSize()) {
				throw new IllegalStateException("Given input is not a valid puzzle of the appropriate size");
			}
			
		}
		finally {
	         if (reader != null) {
	        	 reader.close();
	          }
		}
		
		return puzzle;
	}
	
	private boolean isValidSize() {
		if (squares == null || squares.size() != Square.MAX_VALUE) {
			return false;
		}
		
		for (List<Square> row : squares) {
			if (row == null || row.size() != Square.MAX_VALUE) {
				return false;
			}
		}
		
		return true;
	}

	private void addRow(List <Square> row) {
		squares.add(row);
	}

}
