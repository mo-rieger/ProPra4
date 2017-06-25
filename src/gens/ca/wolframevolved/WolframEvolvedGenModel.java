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
package gens.ca.wolframevolved;

import general.GenState;
import general.GenModel;
import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author sebastian
 */

// The Wolfram Evolved Generator demonstrates the evolution of structures generated
// by a Wolfram generator under Game of Life rules. Every now and then (depending
// on the impact event interval parameter) the population is affected by another
// Wolfram structure: living cells die and dead cells are brought to life.
// The ruleset for creating a Wolfram structure is determined randomly each time
// an impact event occurs.
public class WolframEvolvedGenModel extends GenModel {

    private int width;
    private int height;

    private int numCells; // Number of cells per row
    private int numRows; // Number of rows
    private int numGens; // Number of generations, i.e. number of images created
    private int ruleNumber; // Rule number according to Wolfram code
    private int cellSize; // Cell size in px
    private long step; // period in which a single image is displayed (in ms)
    private int impactEventInterval; // Number of generations per impact event

    private boolean randomInit;
    private boolean edgesWrapped;

    private double populationDensity;

    private boolean waitForCanvasDisplayedInRootView;

    private boolean[][] currentGolGen;
    private boolean[][] currentWolfGen;

    public WolframEvolvedGenModel() {
        numCells = 121;
        numRows = 60;
        numGens = 200;
        ruleNumber = 110;
        cellSize = 5;
        impactEventInterval = 10;
        step = 200;
        width = numCells * cellSize;
        height = numRows * cellSize;
        randomInit = false;
        edgesWrapped = true;
        populationDensity = 0.2;
    }

    @Override
    public String getGenName() {
        return "Wolfram Evolved Generator";
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

    public int getRows() {
        return numRows;
    }

    public int getImpactEventInterval() {
        return impactEventInterval;
    }

    // Impact event interval length is restricted to certain range
    public void setImpactEventInterval(int impactEventInterval) {
        if(impactEventInterval > 0 && impactEventInterval <= 50)
            this.impactEventInterval = impactEventInterval;
        else
            throw new IllegalArgumentException("Impact Event Interval requires an integer value between 1 and 50.");
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
            height = numRows * cellSize;
        }
    }

    // Number of rows is restricted to certain range
    // Furthermore, number of rows is correlated to the cell size
    // (thus the image does not exceed 4000 px in height)
    public void setRows(int numRows) {
        if (numRows < 1 || numRows > 4000) {
            throw new IllegalArgumentException("Rows requires an integer value between 1 and 4000.");
        } else if (cellSize * numRows > 4000) {
            throw new IllegalArgumentException("The product of cell size and number of rows cannot exceed 4000.");
        } else {
            this.numRows = numRows;
            height = numRows * cellSize;
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
    // (thus the image does not exceed 4000 px in width)
    public void setCells(int value) {
        if (value < 1 || value > 4000) {
            throw new IllegalArgumentException("Cells requires an integer value between 1 and 4000.");
        } else if (cellSize * value > 4000) {
            throw new IllegalArgumentException("The product of cell size and number of cells cannot exceed 4000.");
        } else {
            numCells = value;
            width = numCells * cellSize;
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

    // Calculation of next generation of cells by rules of Game of Life
    // Range of index variables depends on choice regarding wrapping of edges
    private boolean[][] calcNextGen() {
        boolean[][] nextGen;
        nextGen = new boolean[numRows][numCells];
        
        // Initial generation is solely determined by initial Wolfram structure
        // (thus all cells in Game of Life generation are dead)
        if(currentGolGen == null) {
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCells; j++) {
                    nextGen[i][j] = false;
                }
            }
            return nextGen;
        }
        int start;
        int stopRow;
        int stopCol;
        // Determine cell area to be calculated
        if (edgesWrapped) {
            start = 0;
            stopRow = numRows;
            stopCol = numCells;
        } else {
            start = 1;
            stopRow = numRows - 1;
            stopCol = numCells - 1;
        }
        for (int i = start; i < stopRow; i++) {
            for (int j = start; j < stopCol; j++) {
                // Scan neighbourhood
                int aliveNeighbours = 0;
                for (int k = i - 1; k <= i + 1; k++) {
                    for (int l = j - 1; l <= j + 1; l++) {
                        int rowIndex;
                        int colIndex;
                        if (edgesWrapped) {
                            // Calculation of correct index (in case edge wrapping has been chosen)
                            if (k == -1 || k == numRows) {
                                rowIndex = (k + numRows) % numRows;
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
                        if (currentGolGen[rowIndex][colIndex]) {
                            aliveNeighbours++;
                        }
                    }
                }
                // Set next state of cell
                if (currentGolGen[i][j]) {
                    aliveNeighbours--; // The cell itself does not count towards living neighbours
                    nextGen[i][j] = aliveNeighbours == 2 || aliveNeighbours == 3;
                } else {
                    nextGen[i][j] = aliveNeighbours == 3;
                }
            }
        }
        return nextGen;
    }

    // Set rule according to Wolfram code
    public void setRule(int value) {
        if (value >= 0 && value <= 255) {
            ruleNumber = value;
        } else {
            throw new IllegalArgumentException("Rule requires an integer value between 0 and 255.");
        }
    }

    // Calculate rule table from decimal Wolfram code integer
    public boolean[] calcTransitionFunction() {
        boolean[] rule = new boolean[8];
        int ruleAkk = ruleNumber;
        // Conversion from decimal integer to binary in array
        // (thus it can serve as a lookup table when calculating next generation)
        for (int i = rule.length - 1; i >= 0; i--) {
            int div = (int) Math.floor((ruleAkk / Math.pow(2, i)));
            if (div == 1) {
                rule[i] = true;
                ruleAkk -= (int) Math.pow(2, i);
            } else {
                rule[i] = false;
            }
        }
        return rule;
    }

    // Calculates the Wolfram initial generation depending on whether random initial generation
    // or Single 1 has been chosen
    // Calculation of random initial generation correlates to population density
    public boolean[] calcInitWolframGen() {
        boolean[] nextGen;
        nextGen = new boolean[numCells];
        if (randomInit) {
            for (int i = 0; i < numCells; i++) {
                double rnd = Math.random();
                nextGen[i] = rnd <= populationDensity;
            }
        } else {
            for (int i = 0; i < numCells; i++) {
                nextGen[i] = false;
            }
            int midCell = numCells / 2;
            nextGen[midCell] = true;
        }
        return nextGen;
    }

    // Calculation of next Wolfram generation of cells by adding up powers of 2 depending
    // on the involved cells' status
    // This value is then used to look up each cell's respective next status
    // according to the rule selected in the setup process
    // In case of wrapped edges, further calculations are carried out in order
    // to determine the edge cells' next status
    public boolean[] calcNextWolframGen(boolean[] currentGen, boolean[] rule) {
        boolean[] nextGen;
        nextGen = new boolean[numCells];
        for (int i = 1; i < numCells - 1; i++) {
            int value = 0;
            if (currentGen[i - 1] == true) {
                value += 4;
            }
            if (currentGen[i] == true) {
                value += 2;
            }
            if (currentGen[i + 1] == true) {
                value += 1;
            }
            nextGen[i] = rule[value];
        }
        if (edgesWrapped) {
            int value = 0;
            if (currentGen[numCells - 1] == true) {
                value += 4;
            }
            if (currentGen[0] == true) {
                value += 2;
            }
            if (currentGen[1] == true) {
                value += 1;
            }
            nextGen[0] = rule[value];
            value = 0;
            if (currentGen[numCells - 2] == true) {
                value += 4;
            }
            if (currentGen[numCells - 1] == true) {
                value += 2;
            }
            if (currentGen[0] == true) {
                value += 1;
            }
            nextGen[numCells - 1] = rule[value];
        }
        return nextGen;
    }

    // Use for random choice of Wolfram rule employed in calculation
    private int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    @Override
    public void generate() {
        currentGolGen = null;
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

                setGenState("Calculating generation " + (k + 1) + "...");
                
                // Calculate next generation
                currentGolGen = calcNextGen();
                
                currentWolfGen = new boolean[numRows][numCells];

                // In case the time has come for the next impact event,
                // a Wolfram structure is calculated
                if (k % impactEventInterval == 0) {
                    ruleNumber = getRandomNumberInRange(0, 255);
                    //System.out.println(ruleNumber);
                    boolean[] rule = calcTransitionFunction();
                    currentWolfGen[0] = calcInitWolframGen();
                    for (int i = 1; i < numRows; i++) {
                        currentWolfGen[i] = calcNextWolframGen(currentWolfGen[i - 1], rule);
                    }
                } 

                // Cells are displayed in certain colours
                // Color key:
                // RED: Cell is alive and part of currently impacting Wolfram structure
                // GREEN: Cell is dead and part of currently impacting Wolfram structure
                // BLUE: Cell is alive and not part of currently impacting Wolfram
                //       structure or there is no impact event
                // ANTIQUE WHITE: Cell is dead and not part of currently impacting
                //                Wolfram structure or there is no impact event
                for (int i = 0; i < numRows; i++) {
                    for (int j = 0; j < numCells; j++) {
                        if (k%impactEventInterval == 0 && currentWolfGen[i][j]) {
                            if(currentGolGen[i][j])
                                gc.setFill(Color.CRIMSON);
                            else
                                gc.setFill(Color.DARKSEAGREEN);
                        } else {
                            if (currentGolGen[i][j]) {
                                gc.setFill(Color.STEELBLUE);
                            } else {
                                gc.setFill(Color.ANTIQUEWHITE);
                            }
                        }
                        gc.fillOval(j * cellSize, i * cellSize, cellSize, cellSize);
                    }
                }
                
                // Cells hit by impacting Wolfram structure switch their status
                if (k % impactEventInterval == 0) {
                    for (int i = 0; i < numRows; i++) {
                        for (int j = 0; j < numCells; j++) {
                            if (currentWolfGen[i][j]) {
                                currentGolGen[i][j] = !currentGolGen[i][j];
                            }
                        }
                    }
                }

                long calculationTime = System.nanoTime() - calcStartTime;

                // Convert to ms
                calculationTime = calculationTime / 1000000;

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
