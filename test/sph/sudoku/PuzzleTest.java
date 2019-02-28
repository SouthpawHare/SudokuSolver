package sph.sudoku;

import static org.junit.jupiter.api.Assertions.*;

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

}
