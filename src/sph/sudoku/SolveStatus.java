package sph.sudoku;

public enum SolveStatus {
	
	Progress(true), NoProgress(false), Impossible(true);
	
	boolean continueAfterResult;
	
	private SolveStatus(boolean continueAfterResult) {
		this.continueAfterResult = continueAfterResult;
	}
	
	public boolean getContinueAfterResult() {
		return continueAfterResult;
	}

}
