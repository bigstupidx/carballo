package com.alonsoruibal.chess;

import com.alonsoruibal.chess.bitboard.AttacksInfo;
import com.alonsoruibal.chess.evaluation.Evaluator;
import com.alonsoruibal.chess.evaluation.ExperimentalEvaluator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ExperimentalEvaluatorTest {
	ExperimentalEvaluator evaluator;
	AttacksInfo attacksInfo;

	@Before
	public void setUp() throws Exception {
		attacksInfo = new AttacksInfo();
		evaluator = new ExperimentalEvaluator();
		evaluator.debug = true;
	}

	public static int countSubstring(String subStr, String str) {
		return (str.length() - str.replace(subStr, "").length()) / subStr.length();
	}

	@Test
	public void testEvaluatorSimmetry1() {
		String fen = "r2q1rk1/ppp2ppp/2n2n2/1B1pp1B1/1b1PP1b1/2N2N2/PPP2PPP/R2Q1RK1 w QKqk - 0 0";
		Board board = new Board();
		board.setFen(fen);
		int value = evaluator.evaluate(board, attacksInfo);
		System.out.println("value = " + value);
		assertEquals(Evaluator.o(ExperimentalEvaluator.TEMPO), value);
	}

	@Test
	public void testEvaluatorSimmetry2() {
		String fen = "7k/7p/6p1/3kp3/3PK3/1P6/P7/K7 w - - 0 0";
		Board board = new Board();
		board.setFen(fen);
		int value = evaluator.evaluate(board, attacksInfo);
		System.out.println("value = " + value);
		assertEquals(Evaluator.e(ExperimentalEvaluator.TEMPO), value);
	}

	@Test
	public void testPawnClassification() {
		evaluator.debugPawns = true;

		Board board = new Board();
		board.setFen("8/8/7p/1P2Pp1P/2Pp1PP1/8/8/7K w - - 0 0");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("Four passers", 4, countSubstring("passed ", evaluator.debugSB.toString()));
		assertEquals("One outside passed", 1, countSubstring("outside ", evaluator.debugSB.toString()));
		assertEquals("Three supported", 3, countSubstring("supported ", evaluator.debugSB.toString()));
		assertEquals("Six connected", 6, countSubstring("connected ", evaluator.debugSB.toString()));
		assertEquals("Three isolated", 3, countSubstring("isolated ", evaluator.debugSB.toString()));
		assertEquals("Four opposed", 4, countSubstring("opposed ", evaluator.debugSB.toString()));

		board.setFen("7k/p6p/PP6/6P1/8/7P/8/7K w - - 0 0");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("Two candidates", 2, countSubstring("candidate ", evaluator.debugSB.toString()));
		assertEquals("No backward", 0, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/p7/8/PP3ppp/8/5P1P/8/7K w - - 0 0");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("Two candidates", 2, countSubstring("candidate ", evaluator.debugSB.toString()));

		board.setFen("7k/8/3p4/1p6/2PP4/8/8/7K w - - 0 0");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("Two candidates", 2, countSubstring("candidate ", evaluator.debugSB.toString()));

		board.setFen("7k/3r4/8/3p4/8/8/8/R6K w - - 0 0");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("Runner", 1, countSubstring("runner ", evaluator.debugSB.toString()));

		board.setFen("7k/8/8/3p4/8/8/1r6/R6K w - - 0 0");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("No Runner", 0, countSubstring("runner ", evaluator.debugSB.toString()));
		assertEquals("Mobile", 1, countSubstring("mobile ", evaluator.debugSB.toString()));
		assertEquals("No Outside", 0, countSubstring("outside ", evaluator.debugSB.toString()));

		board.setFen("7k/8/8/3p4/R7/8/1r6/7K w - - 0 0");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("No Runner", 0, countSubstring("runner ", evaluator.debugSB.toString()));
		assertEquals("No Mobile", 0, countSubstring("mobile ", evaluator.debugSB.toString()));

		board.setFen("7k/8/8/5P2/5P2/8/8/7K w - - 0 0");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("Doubled", 1, countSubstring("doubled ", evaluator.debugSB.toString()));
		assertEquals("Not connected", 0, countSubstring("connected ", evaluator.debugSB.toString()));
		assertEquals("Only one passed", 1, countSubstring("passed ", evaluator.debugSB.toString()));

		board.setFen("R7/3p3p/8/3P2P1/3k4/1p5p/1P1NKP1P/7q w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("One backward", 1, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/8/8/3p1p2/1p1P1Pp1/1P2P1P1/P1P4P/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("Five backward", 5, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/7P/8/5p2/8/8/6P1/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("One backward", 1, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/8/Pp6/8/8/1P6/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("One backward", 1, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/8/P1p5/8/8/1P6/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("One backward", 1, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/2p5/P7/8/8/1P6/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("One backward", 1, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/2p5/8/P7/8/1P6/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("No backward", 0, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/8/8/P7/2p5/1P6/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("No backward", 0, countSubstring("backward ", evaluator.debugSB.toString()));

		board.setFen("7k/8/4p3/8/4pp2/8/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("One backward", 1, countSubstring("backward ", evaluator.debugSB.toString()));
		assertEquals("One Doubled", 1, countSubstring("doubled ", evaluator.debugSB.toString()));

		board.setFen("7k/4p3/8/5p2/4p3/8/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("No backward", 0, countSubstring("backward ", evaluator.debugSB.toString()));
		assertEquals("One Doubled", 1, countSubstring("doubled ", evaluator.debugSB.toString()));

		board.setFen("7k/4p3/8/5p2/3Pp3/8/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("One backward", 1, countSubstring("backward ", evaluator.debugSB.toString()));
		assertEquals("One Doubled", 1, countSubstring("doubled ", evaluator.debugSB.toString()));

		board.setFen("7k/2P5/pp6/1P6/8/8/8/7K w - -");
		evaluator.evaluate(board, attacksInfo);
		assertEquals("No backward because it can capture", 0, countSubstring("backward ", evaluator.debugSB.toString()));
		evaluator.debugPawns = false;
	}

	@Test
	public void testPassedPawn1() {
		String fen = "7k/7p/P7/8/8/6p1/7P/7K w QKqk - 0 0";
		Board board = new Board();
		board.setFen(fen);
		int value = evaluator.evaluate(board, attacksInfo);
		System.out.println("value = " + value);
		assertTrue(value > 0);
	}

	@Test
	public void testKnightTrapped() {
		String fen = "NPP5/PPP5/PPP5/8/8/8/8/k6K w - - 0 0";
		Board board = new Board();
		board.setFen(fen);
		int value = evaluator.evaluate(board, attacksInfo);
		System.out.println("value = " + value);
		assertTrue(value > 0);
	}

	@Test
	public void testKingSafety() {
		String fen = "r6k/1R6/8/7p/7P/8/8/7K w QKqk - 0 0";
		Board board = new Board();
		board.setFen(fen);
		int value = evaluator.evaluate(board, attacksInfo);
		System.out.println("value = " + value);
		assertTrue(value > 0);
	}

	// Compares the eval of two fens
	private void compareFenEval(String fenBetter, String fenWorse, int requiredDifference) {
		System.out.println("*\n* Comparing two board evaluations (first must be better for white):\n*");
		Board board = new Board();
		board.setFen(fenBetter);
		int valueBetter = evaluator.evaluate(board, attacksInfo);
		board.setFen(fenWorse);
		int valueWorse = evaluator.evaluate(board, attacksInfo);
		System.out.println("valueBetter = " + valueBetter);
		System.out.println("valueWorse = " + valueWorse);
		assertTrue(valueBetter > valueWorse + requiredDifference);
	}

	@Test
	public void testBishopBonus() {
		compareFenEval("3BB2k/8/8/8/8/8/p7/7K w QKqk - 0 0", "2B1B2k/8/8/8/8/8/p7/7K w QKqk - 0 0", 40);
	}

	@Test
	public void testSBDCastling() {
		compareFenEval("r4r2/pppbkp2/2n3p1/3Bp2p/4P2N/2P5/PP3PPP/2KR3R b q - 0 1",
				"2kr1r2/pppb1p2/2n3p1/3Bp2p/4P2N/2P5/PP3PPP/2KR3R b - - 0 1", 0);
	}
}