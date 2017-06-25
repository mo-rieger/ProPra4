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
package gens.ca.cookiemonster;

import general.GenState;
import general.GenModel;
import java.util.Random;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author sebastian
 */

// Implementation of "Cookie Monster" generator as presented at
// https://moodle2wrm.fernuni-hagen.de/mod/forum/discuss.php?d=9380
public class CookieMonsterGenModel extends GenModel {

    private int width;
    private int height;

    private int numCells; // Number of cells per row
    private int numGens; // Number of generations, i.e. number of images created
    private int cellSize; // Cell size in px
    private int states; // Number of states
    private long step; // period in which a single image is displayed (in ms)


    private boolean waitForCanvasDisplayedInRootView;

    private int[][] currentGen;
    
    private Color[] colors;

    public CookieMonsterGenModel() {
        numCells = 120;
        numGens = 500;
        cellSize = 5;
        states = 15;
        step = 50;
        width = numCells * cellSize;
        height = numCells * cellSize;
    }

    @Override
    public String getGenName() {
        return "Cookie Monster Generator";
    }

    // Cell size is restricted to certain range
    // Furthermore, cell size is correlated to the number of cells
    // (thus the image does not exceed 4000 px in each dimension)
    public void setCellSize(int cellSize) {
        if (cellSize < 1 || cellSize > 50) {
            throw new IllegalArgumentException("Cell Size requires an integer value between 1 and 50.");
        } else if (cellSize * numCells > 4000) {
            throw new IllegalArgumentException("The product of cell size and number of cells cannot exceed 4000.");
        } else {
            this.cellSize = cellSize;
            width = numCells * cellSize;
            height = numCells * cellSize;
        }
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCells() {
        return numCells;
    }

    public int getGens() {
        return numGens;
    }

    public long getStep() {
        return step;
    }

    public int getStates() {
        return states;
    }

    // Number of states is restricted to certain range
    public void setStates(int states) {
        if (states < 1 || states > 20) {
            throw new IllegalArgumentException("States requires an integer value between 1 and 20.");
        } else {
            this.states = states;
        }
    }

    // Step length is restricted to certain range
    public void setStep(long step) {
        if (step < 50 || step > 5000) {
            throw new IllegalArgumentException("Step requires an integer value between 50 and 5000.");
        } else {
            this.step = step;
        }
    }

    // Number of cells per row is restricted to certain range
    // Furthermore, number of cells is correlated to the cell size
    // (thus the image does not exceed 4000 px in each dimension)
    public void setCells(int value) {
        if (value < 1 || value > 4000) {
            throw new IllegalArgumentException("Cells requires an integer value between 1 and 4000.");
        } else if (cellSize * value > 4000) {
            throw new IllegalArgumentException("The product of cell size and number of cells cannot exceed 4000.");
        } else {
            numCells = value;
            width = numCells * cellSize;
            height = numCells * cellSize;
        }
    }

    // Number of generations is restricted to certain range
    public void setGens(int value) {
        if (value < 1 || value > 10000) {
            throw new IllegalArgumentException("Generations requires an integer value between 1 and 10000.");
        } else {
            numGens = value;
        }
    }

    // Use for random choice of cell state employed in calculating initial generation
    private int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    // Calculates the initial generation
    // Calculation of random initial generation correlates to population density
    private int[][] calcInitGen() {
        int[][] nextGen;
        nextGen = new int[numCells][numCells];
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                nextGen[i][j] = getRandomNumberInRange(0, states - 1);
            }
        }
        return nextGen;
    }

    // Calculation of next generation of cells
    private int[][] calcNextGen() {
        if (currentGen == null) {
            return calcInitGen();
        }
        int[][] nextGen;
        nextGen = new int[numCells][numCells];
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                // Scan neighbourhood
                boolean changeState = false;
                int possibleNewState = (currentGen[i][j] + 1) % states;
                // Seperate calculation of index due to edge wrapping
                int rowindex = (i - 1 + numCells) % numCells;
                int colindex = j;
                if (currentGen[rowindex][colindex] == possibleNewState) {
                    changeState = true;
                }
                
                rowindex = (i + 1 + numCells) % numCells;
                if (currentGen[rowindex][colindex] == possibleNewState) {
                    changeState = true;
                }
                
                rowindex = i;
                colindex = (j - 1 + numCells) % numCells;
                if (currentGen[rowindex][colindex] == possibleNewState) {
                    changeState = true;
                }
                
                colindex = (j + 1 + numCells) % numCells;
                if (currentGen[rowindex][colindex] == possibleNewState) {
                    changeState = true;
                }

                // Set next state of cell
                if (changeState) {
                    nextGen[i][j] = possibleNewState;
                } else {
                    nextGen[i][j] = currentGen[i][j];
                }
            }
        }
        return nextGen;
    }
    
    // Randomly generate colours, one for each state
    private void initializeColors() {
        colors = new Color[states];
        for (int i = 0; i < colors.length; i++) {
            double r = Math.random();
            double g = Math.random();
            double b = Math.random();
            colors[i] = Color.color(r, g, b);
        }
    }

    @Override
    public void generate() {
        currentGen = null;
        initializeColors();
        try {
            for (int k = 0; k < numGens; k++) {
                setGenState("Creating new canvas...");
                canvas = new Canvas(width, height);

                setGenState("Filling image background...");
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, width, height);

                // Measure calculation time
                long calcStartTime = System.nanoTime();
                
                // Check for interrupt
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                
                setGenState("Calculating generation " + (k + 1) + "...");
                currentGen = calcNextGen();
                
                // Image is created from previously calculated current generation
                // Each cell's colour is determined by its state
                for (int i = 0; i < currentGen.length; i++) {
                    for (int j = 0; j < currentGen.length; j++) {
                        int state = currentGen[i][j];
                        gc.setFill(colors[state]);
                        gc.fillOval(j * cellSize, i * cellSize, cellSize, cellSize);
                        }
                    }
                
                long calculationTime = System.nanoTime() - calcStartTime;

                // Convert to ms
                calculationTime = calculationTime / 1000000;

                // System.out.println(calculationTime);
                // Generation is delayed according to step length
                long sleepTime = step - calculationTime;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }

                // In case the canvas has not been displayed yet,
                // generation has to be delayed further
                waitForCanvasIterationDisplayedInApp();
            }
            // Signal controller to enable input
            setGenState(GenState.FINISHED_READY);
        } catch (InterruptedException ex) {
            //setGenState(GenState.FINISHED_READY);
            return; // Generation is stopped if e.g. the generator window is closed
        }

    }

}
