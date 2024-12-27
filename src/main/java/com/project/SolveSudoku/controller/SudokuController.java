package com.project.SolveSudoku.controller;

import com.project.SolveSudoku.service.SudokuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class SudokuController {

    @Autowired
    private SudokuService service;

    @GetMapping("/")
    public String home(Model model) {
        String puzzle = null;
        model.addAttribute("puzzle", puzzle);
        return "index"; // Return the view name
    }

    @PostMapping("/sudoku/solve")
    public String solveSudoku(@RequestParam Map<String, String> puzzleMap, Model model) {
        try {
            StringBuilder puzzle = new StringBuilder();

            for (int i = 0; i < 81; i++) {
                String cell = puzzleMap.get("cell_" + i / 9 + "_" + i % 9);
                if (cell != null && !cell.isEmpty()) {
                    puzzle.append(cell);
                } else {
                    puzzle.append("0");
                }
            }

            if (puzzle.length() != 81) {
                throw new IllegalArgumentException("Invalid input");
            }

            String solution = service.solveSudoku(puzzle.toString());
            model.addAttribute("solution", solution);

            String[][] solvedPuzzle = new String[9][9];
            for (int i = 0; i < 81; i++) {
                solvedPuzzle[i / 9][i % 9] = String.valueOf(solution.charAt(i));
            }

            model.addAttribute("solvedPuzzle", solvedPuzzle);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "index";
    }

}
