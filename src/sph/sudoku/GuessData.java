package sph.sudoku;

import java.util.ArrayList;
import java.util.List;

public class GuessData {
	
	public int row;
	public int col;
	public List<Integer> possibleValues = new ArrayList<Integer>();
	
	public GuessData(int row, int col, List<Integer> possibleValues) {
		this.row = row;
		this.col = col;
		
		for (Integer possibleVal : possibleValues) {
			this.possibleValues.add(new Integer(possibleVal));
		}
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public List<Integer> getPossibleValues() {
		return possibleValues;
	}
	
	@Override
	public String toString() {
		return String.format("(r%d, c%d)%s", row, col, possibleValues.toString());
	}
	
}
