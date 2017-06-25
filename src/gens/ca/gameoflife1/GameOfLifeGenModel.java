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

import general.GenState;
import general.GenModel;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author sebastian
 */
public class GameOfLifeGenModel extends GenModel {

    private int width;
    private int height;

    private int numCells; // Number of cells per row
    private int numGens; // Number of generations, i.e. number of images created
    private int cellSize; // Cell size in px
    private long step; // period in which a single image is displayed (in ms)

    private boolean randomInit;
    private boolean edgesWrapped;

    private double populationDensity;

    private boolean waitForCanvasDisplayedInRootView;

    private boolean[][] currentGen;

    public GameOfLifeGenModel() {
        numCells = 400;
        numGens = 20;
        cellSize = 1;
        step = 500;
        width = numCells * cellSize;
        height = numCells * cellSize;
        randomInit = true;
        edgesWrapped = false;
        populationDensity = 0.2;
    }

    @Override
    public String getGenName() {
        return "Game of Life Generator";
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

    public double getPopulationDensity() {
        return populationDensity;
    }

    public boolean getEdgesWrapped() {
        return edgesWrapped;
    }

    public boolean getRandomInit() {
        return randomInit;
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

    // Step length is restricted to certain range
    public void setStep(long step) {
        if (step < 50 || step > 5000) {
            throw new IllegalArgumentException("Step requires an integer value between 50 and 5000.");
        } else {
            this.step = step;
        }
    }

    // Population density is a real number in [0,1]
    public void setPopulationDensity(double density) {
        if (density >= 0 && density <= 1) {
            populationDensity = density;
        } else {
            throw new IllegalArgumentException("Population Density requires a real value between 0.0 and 1.0.");
        }
    }

    public void setEdgesWrapped(boolean edgesWrapped) {
        this.edgesWrapped = edgesWrapped;
    }

    public void setRandomInit(boolean rndInit) {
        randomInit = rndInit;
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

    // Initial generation can be delivered this way
    public void setInitGen(boolean[][] initGen) {
        this.currentGen = initGen;
    }

    // Calculates the initial generation in case random initial generation is chosen
    // Calculation of random initial generation correlates to population density
    private boolean[][] calcInitGen() {
        boolean[][] nextGen;
        nextGen = new boolean[numCells][numCells];
        if (randomInit) {
            for (int i = 0; i < numCells; i++) {
                for (int j = 0; j < numCells; j++) {
                    double rnd = Math.random();
                    nextGen[i][j] = rnd <= populationDensity;
                }
            }
        } else {
            nextGen = currentGen;
        }
        return nextGen;
    }

    // Calculation of next generation of cells
    // Range of index variables depends on choice regarding wrapping of edges
    private boolean[][] calcNextGen() {
        if (currentGen == null) {
            return calcInitGen();
        }
        boolean[][] nextGen;
        nextGen = new boolean[numCells][numCells];
        int start;
        int stop;
        if (edgesWrapped) {
            start = 0;
            stop = numCells;
        } else {
            start = 1;
            stop = numCells - 1;
        }
        for (int i = start; i < stop; i++) {
            for (int j = start; j < stop; j++) {
                // Scan neighbourhood
                int aliveNeighbours = 0;
                for (int k = i - 1; k <= i + 1; k++) {
                    for (int l = j - 1; l <= j + 1; l++) {
                        int rowIndex;
                        int colIndex;
                        if (edgesWrapped) {
                            // Calculation of correct index (in case edge wrapping has been chosen)
                            if (k == -1 || k == numCells) {
                                rowIndex = (k + numCells) % numCells;
                            } else {
                                rowIndex = k;
                            }
                            if (l == -1 || l == numCells) {
                                colIndex = (l + numCells) % numCells;
                            } else {
                                colIndex = l;
                            }
                        } else {
                            rowIndex = k;
                            colIndex = l;
                        }
                        if (currentGen[rowIndex][colIndex]) {
                            aliveNeighbours++;
                        }
                    }
                }
                // Set next state of cell
                if (currentGen[i][j]) {
                    aliveNeighbours--; // The cell itself does not count towards living neighbours
                    nextGen[i][j] = aliveNeighbours == 2 || aliveNeighbours == 3;
                } else {
                    nextGen[i][j] = aliveNeighbours == 3;
                }
            }
        }
        return nextGen;
    }

    @Override
    public void generate() {
        if (randomInit) {
            currentGen = null;
        }
        try {
            for (int k = 0; k < numGens; k++) {
                
                setGenState("Creating new canvas...");
                canvas = new Canvas(width, height);

                setGenState("Filling image background...");
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, width, height);
                
                // Check for interrupt
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                // Measure calculation time
                long calcStartTime = System.nanoTime();
                
                setGenState("Calculating generation " + (k+1) + "...");
                currentGen = calcNextGen();
                
                // Image is created from previously calculated current generation
                gc.setFill(Color.BLACK);
                for (int i = 0; i < currentGen.length; i++) {
                    for (int j = 0; j < currentGen.length; j++) {
                        if (currentGen[i][j] == true) {
                            gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                        }
                    }
                }
                
                long calculationTime = System.nanoTime() - calcStartTime;
                
                // Convert to ms
                calculationTime = calculationTime / 1000000;
                
                // Generation is delayed according to step length
                long sleepTime = step - calculationTime;
                
                if(sleepTime > 0)
                    Thread.sleep(sleepTime);

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
