package sph.sudoku;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Solver {
	
	public static void main (String [] args) {
		if (args == null || args.length != 1) {
			System.out.println("Please provide an input filename");
			System.exit(1);
		}
		
		File fileInput = new File(args[0]);
		
		System.out.println(String.format("Running on file %s", fileInput.toString()));
		
		if (!fileInput.exists() || !fileInput.isFile()) {
			System.out.println("Invalid file provided");
			System.exit(1);
		}
		
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
		return solveWorker(puzzle.copy());
	}
	
	private static Puzzle solveWorker(Puzzle puzzle) {
		while (!puzzle.isFilled() && puzzle.getStatus().isContinueWithOverall()) {
			puzzle.solveNext();
		}
		
		if (!puzzle.isFilled() && puzzle.getStatus() != SolveStatus.Impossible) {
			List<GuessData> guesses = puzzle.getGuessesInPriorityOrder();
			for (GuessData guess : guesses) {
				for (Integer possibleVal : guess.getPossibleValues()) {

					//							System.out.println(String.format("Making a guess at row %d, col %d: Possible values = %s, guessing %d",
					//									row, col, possibleValues.toString(), possibleVal));

					Puzzle puzzleCopy = puzzle.copy();
					puzzleCopy.getSquare(guess.getRow(), guess.getCol()).setVal(possibleVal);
					puzzleCopy = solveWorker(puzzleCopy);

					if (puzzleCopy.isFilled()) {
						return puzzleCopy;
					}

					//							System.out.println("Guess yielded no results");
				}
			}
		}
		
		return puzzle;
	}

}
