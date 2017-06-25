/*
 * The MIT License
 *
 * Copyright 2017 sebastian.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package gens.ca.gameoflife1;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author sebastian
 */
public class MouseInputController {

    @FXML
    private Pane wrapperPane;
    @FXML
    private Button buttonGenerate;

    HasMouseInputController generatorController;

    private Canvas canvas;
    boolean[][] initGen;
    int width;
    int height;
    int numCells;
    int cellSize;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // Set up parameters for mouse input
        numCells = 60;
        cellSize = 10;
        width = numCells * cellSize;
        height = width;
        
        // Initial generation is stored in an array
        initGen = new boolean[numCells][numCells];
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                initGen[i][j] = false;
            }
        }
        
        // Create blank (i.e. white) canvas
        canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
        
        gc.setFill(Color.BLACK);
        
        // Image can be drawn by both clicking and dragging the mouse
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent clickEvent) -> {
            // Retrieve coordinates
            int x = (int) Math.floor(clickEvent.getX());
            int y = (int) Math.floor(clickEvent.getY());
            markCell(gc, x, y);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent dragEvent) -> {
            // Retrieve coordinates
            int x = (int) Math.floor(dragEvent.getX());
            int y = (int) Math.floor(dragEvent.getY());
            markCell(gc, x, y);
        });

        wrapperPane.getChildren().add(canvas);

        // Generate Button can be triggered by keyboard
        buttonGenerate.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ENTER) {
                handleGenerate();
            }
        });
    }

    // Allows communication with generator
    public void setGeneratorController(HasMouseInputController generatorController) {
        this.generatorController = generatorController;
    }

    // Pressing the "Generate" button delivers the initial generation from
    // mouse input, initiates the generation process and closes the mouse input window
    public void handleGenerate() {
        generatorController.setInitGen(initGen);
        generatorController.handleGenerate();
        generatorController.closeMouseInputWindow();
    }

    // Display current state of initial generation and update array
    private void markCell(GraphicsContext gc, int x, int y) {
        // Calculation of cell position according to cell size
        int posX = x - x % cellSize;
        int posY = y - y % cellSize;
        // Mark cell on canvas and store mark in array
        gc.fillRect(posX, posY, cellSize, cellSize);
        if (x > 0 && y > 0 && x < width && y < height) {
            initGen[y / cellSize][x / cellSize] = true;
        }
    }

}
