package sph.sudoku;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

class PuzzleTest {
	
	@Test
	void testCreateEmptyPuzzle() {
		Puzzle puzzle = Puzzle.createEmptyPuzzle();
		assertTrue(puzzle.isValidSize());
	}
	
	@Test
	void testGetSquaresInBox() {
		Puzzle puzzle = Puzzle.createEmptyPuzzle();
		
		List<Square> relatedSquares = puzzle.getSquaresInBox(8, 0);
		
		boolean[][] relationships = new boolean[][] {
			{false, false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false, false},
			{true , true , true , false, false, false, false, false, false},
			{true , true , true , false, false, false, false, false, false},
			{true , true , true , false, false, false, false, false, false}
		};
		
		checkAllRelationships(puzzle, relatedSquares, relationships);
	}

	@Test
	void testGetRelatedSquares() {
		Puzzle puzzle = Puzzle.createEmptyPuzzle();
		
		List<Square> relatedSquares = puzzle.getRelatedSquares(4, 6);
		
		boolean[][] relationships = new boolean[][] {
			{false, false, false, false, false, false, true , false, false},
			{false, false, false, false, false, false, true , false, false},
			{false, false, false, false, false, false, true , false, false},
			{false, false, false, false, false, false, true , true , true },
			{true , true , true , true , true , true , true , true , true },
			{false, false, false, false, false, false, true , true , true },
			{false, false, false, false, false, false, true , false, false},
			{false, false, false, false, false, false, true , false, false},
			{false, false, false, false, false, false, true , false, false},
		};
		
		checkAllRelationships(puzzle, relatedSquares, relationships);
		
	}

	private void checkAllRelationships(Puzzle puzzle, List<Square> relatedSquares, boolean[][] relationships) {
		for (int row = 0; row < relationships.length; row++) {
			for (int col = 0; col < relationships[row].length; col++) {
				Square square = puzzle.getSquare(row, col);
				assertEquals(relationships[row][col], relatedSquares.contains(square));
			}
		}
	}
	
	@Test
	void testEmptyPuzzleNotAllowed() {
		Puzzle puzzle = Puzzle.createEmptyPuzzle();
		assertFalse(puzzle.hasEnoughCluesToSolve());
	}
	
	@Test
	void testEasyPuzzle() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("puzzle1.txt").getFile());
		Puzzle puzzle = Puzzle.createPuzzleFromInput(file);
		puzzle = Solver.solve(puzzle);
		
		assertTrue(puzzle.isFilled());
		assertEquals(4, (int)puzzle.getSquare(0, 0).getValue());
		assertEquals(6, (int)puzzle.getSquare(8, 8).getValue());
	}
	
	@Test
	void testHardPuzzle() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("puzzle5.txt").getFile());
		Puzzle puzzle = Puzzle.createPuzzleFromInput(file);
		puzzle = Solver.solve(puzzle);
		
		assertTrue(puzzle.isFilled());
		assertEquals(9, (int)puzzle.getSquare(0, 0).getValue());
		assertEquals(1, (int)puzzle.getSquare(8, 8).getValue());
	}
	
	@Test
	void testVeryHardPuzzle() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("extra1.txt").getFile());
		Puzzle puzzle = Puzzle.createPuzzleFromInput(file);
		puzzle = Solver.solve(puzzle);
		
		assertTrue(puzzle.isFilled());
		assertEquals(1, (int)puzzle.getSquare(0, 0).getValue());
		assertEquals(9, (int)puzzle.getSquare(8, 8).getValue());
	}

}
