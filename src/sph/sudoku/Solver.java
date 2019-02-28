package sph.sudoku;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Solver {
	
	private static final int DEPTH_LIMIT_MAX = 3;

	//Main method for running the Solver.  Accepts one parameter: the input file.
	public static void main (String [] args) {
		
		//Require the input file parameter
		if (args == null || args.length != 1) {
			System.out.println("Please provide an input filename");
			System.exit(1);
		}
		
		File fileInput = new File(args[0]);
		
		System.out.println(String.format("Running on file %s", fileInput.toString()));
		
		//Make sure input file is valid
		if (!fileInput.exists() || !fileInput.isFile()) {
			System.out.println("Invalid file provided");
			System.exit(1);
		}
		
		//Create the initial puzzle state from the input file.
		Puzzle original = null;
		try {
			original = Puzzle.createPuzzleFromInput(fileInput);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if (original == null) {
			System.out.println("Puzzle could not be created from input");
			System.exit(1);
		}
		System.out.println("\nOriginal:");
		System.out.println(original.toString());
		
		//Attempt to solve the puzzle and acquire a solution
		Puzzle solution = Solver.solve(original);
		
		if (solution == null) {
			System.out.println("Puzzle could not be processed");
			System.exit(1);
		}
		
		System.out.println("\nResult:");
		System.out.println(solution.toString());

		if (solution.isFilled()) {
			System.out.println("A complete solution was found");
		}
		else {
			System.out.println("The puzzle was not completed");
		}

		//Output the solution to the output file
		File fileOutput = new File(fileInput.getName() + ".sln.txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileOutput));
			writer.write(solution.toString());
			System.out.println(String.format("Wrote output to output file %s", fileOutput.getAbsolutePath()));
		}
		catch (IOException ex) {
			System.out.println("Could not output to output file");
		}
		finally {
			//Clean up output file
			if (writer != null) {
				try {
					writer.close();
				}
				catch (IOException ex) {
				}
			}
		}
	}
	
	public static Puzzle solve(Puzzle puzzle) {
		if (!puzzle.hasEnoughCluesToSolve()) {
			System.out.println("Puzzle does not have the minimum number of clues to be valid");
			return puzzle;
		}
		
		Puzzle lastResult = puzzle;
		
		//Attempt to solve the puzzle with a limit to the recursive depth.  Each time
		//it fails at the current recursive depth limit, increase the limit by 1 and
		//start again from the beginning.  This allows for a hybrid style of depth-
		//first and breadth-first methods.
		for (int depthLimit = 0; depthLimit <= DEPTH_LIMIT_MAX; depthLimit++) {
			System.out.println(String.format("Current Guess Depth Limit: %d", depthLimit));
			lastResult = solveWorker(puzzle.copy(), 0, depthLimit);
			if (lastResult.isFilled()) {
				return lastResult;
			}
		}
		return lastResult;
	}
	
	private static Puzzle solveWorker(Puzzle puzzle, int currGuessDepth, int guessDepthLimit) {
		
		//While there continue to be spaces with only one possible value,
		//continue to fill them in.  Stop if no progress was made in the
		//last filling attempt or if the puzzle is finished.
		while (!puzzle.isFilled() && puzzle.getStatus().isContinueWithOverall()) {
			puzzle.solveNext();
		}
		
		//If the puzzle is not solved and there are only squares left that have more than one
		//possible answer, we must make a recursive guess.
		if (!puzzle.isFilled() && puzzle.getStatus() != SolveStatus.Impossible) {
			
			//We will only go to a recursive depth within our current depth limit, in order
			//to provide a breadth-first algorithm.
			if (currGuessDepth < guessDepthLimit) {
				
				//Squares to guess are prioritized by number of possibilities.  Squares with
				//the fewest possibilities with be tried first.
				List<GuessData> guesses = puzzle.getGuessesInPriorityOrder();
				
				//For every square with one than one possibility:
				for (GuessData guess : guesses) {
					//For each possibility:
					for (Integer possibleVal : guess.getPossibleValues()) {
						
						Puzzle puzzleCopy = puzzle.copy();
						puzzleCopy.getSquare(guess.getRow(), guess.getCol()).setVal(possibleVal);
						puzzleCopy = solveWorker(puzzleCopy, currGuessDepth+1, guessDepthLimit);

						if (puzzleCopy.isFilled()) {
							return puzzleCopy;
						}
					}
				}
			}
		}

		return puzzle;
	}

}
