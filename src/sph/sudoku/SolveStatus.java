package sph.sudoku;

public enum SolveStatus {
	
	Initial(true, true), Progress(true, false), NoProgress(false, true), Impossible(false, false);
	
	boolean continueWithOverall;
	boolean continueWithStep;
	
	private SolveStatus(boolean continueWithOverall, boolean continueWithStep) {
		this.continueWithOverall = continueWithOverall;
		this.continueWithStep = continueWithStep;
	}
	
	public boolean isContinueWithOverall() {
		return continueWithOverall;
	}
	
	public boolean isContinueWithStep() {
		return continueWithStep;
	}

}
