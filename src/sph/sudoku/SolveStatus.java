package sph.sudoku;

//This Status enum indicates the result of the most recent attempt at solving a single square.  Statuses that
//have a true value for "continueWithOverall" mean that an a further attempt at solving the next single square
//should occur.  Statuses that have a true value for "continueWithStep" mean that the current instance of
//solving a single square should continue, as opposed to aborting.
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
