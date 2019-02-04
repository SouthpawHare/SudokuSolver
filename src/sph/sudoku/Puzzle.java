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
				line = reader.readLine();
				if (line != null && line.length() > 0) {
					
					List<Square> row = new ArrayList<Square>();
					puzzle.addRow(row);
					
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (List<Square> row : squares) {
			for (Square square : row) {
				sb.append(square.toString());
			}
			sb.append('\n');
		}
		
		return sb.toString();
	}

	
	//Returns if every square in the puzzle is filled in with
	//a value, signifying a completed solution
	public boolean isFilled() {
		for (List<Square> row : squares) {
			for (Square square : row) {
				if (!square.isFilled()) {
					return false;
				}
			}
		}
		
		return true;
	}

	//Search through every square in order, find the first empty square that has only one
	//possible value, and fill it.  Return whether a square was filled.
	public SolveStatus solveNext() {
		if (isImpossible()) {
			return SolveStatus.Impossible;
		}
		
		for (int row = 0; row < Square.MAX_VALUE; row++) {
			for (int col = 0; col < Square.MAX_VALUE; col++) {
				SolveStatus status = solve(row, col);
				if (status.getContinueAfterResult()) {
					return status;
				}
			}
		}
		return SolveStatus.NoProgress;
	}

	private boolean isImpossible() {
		for (int row = 0; row < Square.MAX_VALUE; row++) {
			for (int col = 0; col < Square.MAX_VALUE; col++) {
				List<Integer> possibleValues = getPossibleValuesFromRelatedSquares(row, col);
				if (possibleValues.size() == 0) {
					System.out.println(String.format("Row %d Col %d is impossible!", row, col));
					return true;
				}
			}
		}
		return false;
	}

	//Check if the square at the coordinates is empty and has only one possible value.
	//If so, fill it with this value and return true.  If it cannot be filled or is
	//already filled, return false.
	private SolveStatus solve(int row, int col) {
		Square square = squares.get(row).get(col);
		
		if (square.isFilled()) {
			return SolveStatus.NoProgress;
		}
		
		List<Integer> possibleValues = getPossibleValuesFromRelatedSquares(row, col);
		
		System.out.println(String.format("Row %d Col %d possible values: %s", row, col, possibleValues.toString()));
		
		if (possibleValues.size() == 1) {
			square.setVal(possibleValues.get(0));
			return SolveStatus.Progress;
		}
		
		return SolveStatus.NoProgress;
	}

	private List<Square> getRelatedSquares(int row, int col) {
		List<Square> relatedSquares = new ArrayList<Square>();
		relatedSquares.addAll(getSquaresInRow(row));
		relatedSquares.addAll(getSquaresInCol(col));
		relatedSquares.addAll(getSquaresInBox(row, col));
		
		System.out.println(String.format("Row %d Col %d, related squares: %s", row, col, relatedSquares.toString()));
		
		return relatedSquares;
	}
	
	private List<Integer> getPossibleValuesFromRelatedSquares(int row, int col) {
		Square square = squares.get(row).get(col);
		
		if (square.getValue() != null) {
			List<Integer> result = new ArrayList<Integer>();
			result.add(square.getValue());
			return result;
		}
		
		return getPossibleValuesFromRelatedSquares(getRelatedSquares(row, col));
	}
	
	private List<Integer> getPossibleValuesFromRelatedSquares(List<Square> relatedSquares) {
		List<Integer> possibleValues = new ArrayList<Integer>();
		for (int i = 1; i <= Square.MAX_VALUE; i++) {
			possibleValues.add(i);
		}
		
		for (Square relatedSquare : relatedSquares) {
			possibleValues.remove((Object)relatedSquare.getValue());
		}
		return possibleValues;
	}

	private List<Square> getSquaresInRow(int row) {
		return squares.get(row);
	}
	
	private List<Square> getSquaresInCol(int col) {
		List<Square> result = new ArrayList<Square>();
		for (List<Square> row : squares) {
			result.add(row.get(col));
		}
		return result;
	}
	
	private List<Square> getSquaresInBox(int row, int col) {
		List<Square> result = new ArrayList<Square>();
		int rowMin = getBoxMin(row);
		int colMin = getBoxMin(col);
		for (int currRow = rowMin; currRow < rowMin + Square.BOX_SIZE; currRow++) {
			for (int currCol = colMin; currCol < colMin + Square.BOX_SIZE; currCol++) {
				result.add(squares.get(currRow).get(currCol));
			}
		}
		
		return result;
	}

	//Return the index of the first row or column in the same box as the current row or column.
	//This will return 0 if given 0-2, 3 if given 3-5, or 6 if given 6-8.
	private int getBoxMin(int index) {
		while (index % Square.BOX_SIZE != 0) {
			index--;
		}
		return index;
	}

}
