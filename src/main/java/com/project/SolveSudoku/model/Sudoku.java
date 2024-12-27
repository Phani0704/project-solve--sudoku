package com.project.SolveSudoku.model;

import jakarta.persistence.*;

@Entity
public class Sudoku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 81, nullable = false)
    private String puzzle;

    @Column(length = 81, nullable = false)
    private String solution;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(String puzzle) {
        this.puzzle = puzzle;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
