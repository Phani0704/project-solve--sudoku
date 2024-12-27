package com.project.SolveSudoku.repository;

import com.project.SolveSudoku.model.Sudoku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SudokuRepository extends JpaRepository<Sudoku, Long> {
    Optional<Sudoku> findByPuzzle(String puzzle);
}
