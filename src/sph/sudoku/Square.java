package sph.sudoku;

public class Square {
	
	public static final int MAX_VALUE = 9;
	private static final String EMPTY = "X";
	
	private Integer value;

	public void setVal(Integer value) {
		if (value != null && (value < 1 || value > MAX_VALUE)) {
			throw new IllegalArgumentException("Value must be within legal range or be NULL");
		}
		
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return (value != null ? String.valueOf(value) : EMPTY);
	}

}