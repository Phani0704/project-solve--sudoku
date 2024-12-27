package com.project.SolveSudoku.service;

import com.project.SolveSudoku.model.Sudoku;
import com.project.SolveSudoku.repository.SudokuRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class SudokuService {

    private final SudokuRepository repository;

    public SudokuService(SudokuRepository repository) {
        this.repository = repository;
    }

    public String solveSudoku(String puzzle) {
        // Validate the input puzzle to ensure it is 81 characters long and contains only digits 0-9
        if (puzzle == null || puzzle.length() != 81 || !puzzle.matches("[0-9]{81}")) {
            throw new IllegalArgumentException("Invalid Input");
        }

        // Validate the Sudoku puzzle to check for duplicates in rows, columns, and subgrids
        if (!isValidPuzzle(puzzle)) {
            throw new IllegalArgumentException("Invalid puzzle: contains duplicates in row, column, or 3x3 subgrid.");
        }

        // Check if the solution already exists in the database
        Optional<Sudoku> existing = repository.findByPuzzle(puzzle);
        if (existing.isPresent()) {
            return existing.get().getSolution();
        }

        // Solve the puzzle and save the solution
        String solution = solvePuzzleLogic(puzzle);

        // Check if the solution is valid (length should be 81 and contain only digits)
        if (solution.length() != 81 || !solution.matches("[1-9]{81}")) {
            throw new IllegalStateException("Solution is invalid.");
        }

        // Save the puzzle and solution to the database
        try {
            Sudoku sudoku = new Sudoku();
            sudoku.setPuzzle(puzzle);
            sudoku.setSolution(solution);
            repository.save(sudoku);
        } catch (DataIntegrityViolationException e) {
            System.out.println("Puzzle already exists in the database, skipping save.");
        }

        return solution;
    }

    private boolean isValidPuzzle(String puzzle) {
        // Convert the string into a 2D char array
        char[][] board = new char[9][9];
        for (int i = 0; i < 81; i++) {
            board[i / 9][i % 9] = puzzle.charAt(i);
        }

        // Check for duplicates in rows, columns, and subgrids
        for (int i = 0; i < 9; i++) {
            Set<Character> rowSet = new HashSet<>();
            Set<Character> colSet = new HashSet<>();
            Set<Character> boxSet = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                // Check row
                if (board[i][j] != '0' && !rowSet.add(board[i][j])) {
                    return false;  // Duplicate found in row
                }
                // Check column
                if (board[j][i] != '0' && !colSet.add(board[j][i])) {
                    return false;  // Duplicate found in column
                }
                // Check 3x3 subgrid
                int boxRow = 3 * (i / 3) + j / 3;
                int boxCol = 3 * (i % 3) + j % 3;
                if (board[boxRow][boxCol] != '0' && !boxSet.add(board[boxRow][boxCol])) {
                    return false;  // Duplicate found in 3x3 subgrid
                }
            }
        }
        return true;  // No duplicates found
    }


    private String solvePuzzleLogic(String puzzle) {
        // Convert the string into a 2D char array
        char[][] board = new char[9][9];
        for (int i = 0; i < 81; i++) {
            board[i / 9][i % 9] = puzzle.charAt(i);
        }

        // Solve the Sudoku puzzle using backtracking
        if (solve(board)) {
            // Convert the solved 2D array back to a string
            StringBuilder solution = new StringBuilder();
            for (char[] row : board) {
                for (char cell : row) {
                    solution.append(cell);
                }
            }
            return solution.toString();
        } else {
            throw new IllegalStateException("Puzzle could not be solved.");
        }
    }

    private boolean solve(char[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == '0') {  // Empty cell
                    for (char c = '1'; c <= '9'; c++) {
                        if (isSafe(board, i, j, c)) {
                            board[i][j] = c;

                            if (solve(board)) {
                                return true;  // Puzzle solved
                            } else {
                                board[i][j] = '0';  // Backtrack
                            }
                        }
                    }
                    return false;  // No valid number found, need to backtrack
                }
            }
        }
        return true;  // Puzzle solved
    }

    private boolean isSafe(char[][] board, int row, int col, char c) {
        // Check the row, column, and 3x3 subgrid
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == c || board[i][col] == c ||
                board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3] == c) {
                return false;
            }
        }
        return true;
    }
}
