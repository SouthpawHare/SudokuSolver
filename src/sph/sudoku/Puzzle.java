package sph.sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//Data structure for a full puzzle.  Contains a grid of squares (spaces) stored in a List
//of Lists which is required to form the appropriate dimensions (9x9).  Also contains a
//status, which indicates the result of the last attempt at finding and filling one more
//square.
public class Puzzle {
	
	private static final int MINIMUM_NECESSARY_CLUES = 17;
	private List< List <Square> > squares = new ArrayList< List <Square> >();
	private SolveStatus status = SolveStatus.Initial;
	
	private Puzzle() {
		
	}
	
	public SolveStatus getStatus() {
		return status;
	}

	public void setStatus(SolveStatus status) {
		this.status = status;
	}

	//Factory method for creating a new puzzle from an input file.  Handles file
	//handling IO logic and assures that a valid board has been generated.
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
	
	public static Puzzle createEmptyPuzzle() {
		Puzzle puzzle = new Puzzle();
		for (int row = 0; row < Square.MAX_VALUE; row++) {
			List<Square> rowList = new ArrayList<Square>();
			puzzle.addRow(rowList);
			for (int col = 0; col < Square.MAX_VALUE; col++) {
				rowList.add(new Square());
			}
		}
		return puzzle;
	}
	
	//Returns whether the board is of the correct size.
	protected boolean isValidSize() {
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
	//possible value, and fill it.  Set the status of the puzzle according to the result.
	public void solveNext() {
		if (isImpossible()) {
			setStatus(SolveStatus.Impossible);
			return;
		}
		
		for (int row = 0; row < Square.MAX_VALUE; row++) {
			for (int col = 0; col < Square.MAX_VALUE; col++) {
				SolveStatus status = solve(row, col);
				if (!status.isContinueWithStep()) {
					setStatus(status);
					return;
				}
			}
		}
		
		setStatus(SolveStatus.NoProgress);
	}

	//Return whether the puzzle is unsolvable in its current state.
	private boolean isImpossible() {
		for (int row = 0; row < Square.MAX_VALUE; row++) {
			for (int col = 0; col < Square.MAX_VALUE; col++) {
				List<Integer> possibleValues = getPossibleValuesFromRelatedSquares(row, col);
				if (possibleValues.size() == 0) {
					return true;
				}
			}
		}
		return false;
	}

	//Check if the square at the coordinates is empty and has only one possible value.
	//If so, fill it with this value.  Set a status indicating whether or not this
	//was successful.
	private SolveStatus solve(int row, int col) {
		Square square = squares.get(row).get(col);
		
		if (square.isFilled()) {
			return SolveStatus.NoProgress;
		}
		
		List<Integer> possibleValues = getPossibleValuesFromRelatedSquares(row, col);
		
		if (possibleValues.size() == 1) {
			square.setVal(possibleValues.get(0));
			return SolveStatus.Progress;
		}
		
		return SolveStatus.NoProgress;
	}

	//Return a list of all squares related to another square at a given set of coordinates.
	//The related squares cannot hold the same value as the given square.
	protected List<Square> getRelatedSquares(int row, int col) {
		List<Square> relatedSquares = new ArrayList<Square>();
		relatedSquares.addAll(getSquaresInRow(row));
		relatedSquares.addAll(getSquaresInCol(col));
		relatedSquares.addAll(getSquaresInBox(row, col));

		return relatedSquares;
	}
	
	//Return a list of all possible values that do not appear in the squares related to
	//the square at the given set of coordinates.  These are the possible values that
	//could go in the given square.
	public List<Integer> getPossibleValuesFromRelatedSquares(int row, int col) {
		Square square = squares.get(row).get(col);
		
		if (square.getValue() != null) {
			List<Integer> result = new ArrayList<Integer>();
			result.add(square.getValue());
			return result;
		}
		
		return getPossibleValuesFromRelatedSquares(getRelatedSquares(row, col));
	}
	
	//Return a list of all possible values that do not appear in the given list of squares.
	//A square can appear in the list multiple times, and this will not impact the result.
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

	//Return all squares in a given row
	protected List<Square> getSquaresInRow(int row) {
		return squares.get(row);
	}
	
	//Return all squares in a given column
	protected List<Square> getSquaresInCol(int col) {
		List<Square> result = new ArrayList<Square>();
		for (List<Square> row : squares) {
			result.add(row.get(col));
		}
		return result;
	}
	
	//Return all squares in the same mini-box as the square at the given coordinates
	protected List<Square> getSquaresInBox(int row, int col) {
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

	//Create a deep copy of all elements of the puzzle.  The status is reset to "Initial".
	public Puzzle copy() {
		Puzzle copy = new Puzzle();
		
		for (List <Square> row : squares) {
			if (row != null) {
				List<Square> rowCopy = new ArrayList<Square>();
				copy.addRow(rowCopy);
				for (Square square : row) {
					if (square != null) {
						Square squareCopy = new Square();
						if (square.getValue() != null) {
							squareCopy.setVal(new Integer(square.getValue()));
						}
						rowCopy.add(squareCopy);
					}
				}
			}
		}
		
		return copy;
	}

	protected Square getSquare(int row, int col) {
		return squares.get(row).get(col);
	}

	//Return a list of data representing all the possible "guesses" that could be made.  Each
	//guess is derrived from a square that has 2 or more possible values, and are sorted
	//such that entries with the fewest possible values come first.
	public List<GuessData> getGuessesInPriorityOrder() {
		List<GuessData> guesses = new ArrayList<GuessData>();
		
		for (int row = 0; row < Square.MAX_VALUE; row++) {
			for (int col = 0; col < Square.MAX_VALUE; col++) {
				List<Integer> possibleValues = getPossibleValuesFromRelatedSquares(row, col);
				if (possibleValues.size() > 1) {
					guesses.add(new GuessData(row, col, possibleValues));
				}
			}
		}
		
		Collections.sort(guesses, new Comparator<GuessData>() {
			@Override
			public int compare(GuessData o1, GuessData o2) {
				int compNumOfPossibleValues = Integer.compare(
						o1.getPossibleValues().size(), o2.getPossibleValues().size());
				if (compNumOfPossibleValues != 0) {
					return compNumOfPossibleValues;
				}
				
				int compRow = Integer.compare(o1.getRow(), o2.getRow());
				if (compRow != 0) {
					return compRow;
				}
				
				return Integer.compare(o1.getCol(), o2.getCol());
			}
		});
		
		return guesses;
	}

	//Require a minimum number of provided clues in order to allow solving a puzzle in order to
	//prevent excessive guessing on an invalid puzzle.  The generally agreed-upon minimum number
	//of clues for a valid unique Sudoku puzzle is 17.
	protected boolean hasEnoughCluesToSolve() {
		return getTotalNumberOfFilledSquares() >= MINIMUM_NECESSARY_CLUES;
	}
	
	protected int getTotalNumberOfFilledSquares() {
		int totalFilled = 0;
		for (List<Square> row : squares) {
			for (Square square : row) {
				if (square.isFilled()) {
					totalFilled++;
				}
			}
		}
		return totalFilled;
	}

}
