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
package gens.ca.wolfram;

import general.GenModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author sebastian
 */
public class WolframGenModel extends GenModel {

    private int width;
    private int height;

    private int numCells; // Number of cells per row
    private int numRows; // Number of rows
    private int numGens; // Number of generations (i.e. number of rows)
    private int ruleNumber; // Identifies rule according to the Wolfram code
                            // (cf. https://en.wikipedia.org/wiki/Elementary_cellular_automaton)
    private int cellSize = 1; // Cell size in px

    private boolean randomInit;
    private boolean edgesWrapped;
    
    private double populationDensity;

    public WolframGenModel() {
        numCells = 1001;
        numGens = 500;
        width = numCells * cellSize;
        height = numGens * cellSize;
        ruleNumber = 110;
        randomInit = false;
        edgesWrapped = false;
        populationDensity = 0.5;
    }

    @Override
    public String getGenName() {
        return "Wolfram Generator";
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

    public int getRule() {
        return ruleNumber;
    }

    public int getCells() {
        return numCells;
    }

    public int getGens() {
        return numGens;
    }
    
    public int getCellSize() {
        return cellSize;
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
    
    // Cell size is restricted to certain range
    // Furthermore, cell size is correlated to the number of cells and number of generations
    // (thus the image does not exceed 8001 px in width or 4000 px in height)
    public void setCellSize(int cellSize) {
        if (cellSize < 1 || cellSize > 50) {
            throw new IllegalArgumentException("Cell Size requires an integer value between 1 and 50.");
        } else {
            if (cellSize * numGens > 4000) {
                throw new IllegalArgumentException("The product of cell size and number of generations cannot exceed 4000.");
            } else {
                if(cellSize * numCells > 8001) {
                    throw new IllegalArgumentException("The product of cell size and number of cells cannot exceed 8001.");
                } else {
                    this.cellSize = cellSize;
                    width = numCells * cellSize;
                    height = numGens * cellSize;
                }
            }
        }
    }
    
    // Number of cells per row is restricted to certain range
    // Furthermore, number of cells is correlated to the cell size
    // (thus the image does not exceed 4000 px in each dimension)
    public void setCells(int value) {
        if (value < 1 || value > 8001) {
            throw new IllegalArgumentException("Cells requires an integer value between 1 and 8001.");
        } else if (cellSize * value > 8001) {
            throw new IllegalArgumentException("The product of cell size and number of cells cannot exceed 8001.");
        } else {
            numCells = value;
            width = numCells * cellSize;
        }
    }
    
    // Number of generations is restricted to certain range
    public void setGens(int value) {
        if (value < 1 || value > 4000) {
            throw new IllegalArgumentException("Cells requires an integer value between 1 and 4000.");
        } else if (cellSize * value > 4000) {
            throw new IllegalArgumentException("The product of cell size and number of generations cannot exceed 4000.");
        } else {
            numGens = value;
            height = numGens * cellSize;
        }
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
    private boolean[] calcTransitionFunction() {
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

    // Calculates the initial generation depending on whether random initial generation
    // or Single 1 has been chosen
    // Calculation of random initial generation correlates to population density
    private boolean[] calcInitGen() {
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

    // Calculation of next generation of cells by adding up powers of 2 depending
    // on the involved cells' status
    // This value is then used to look up each cell's respective next status
    // according to the rule selected in the setup process
    // In case of wrapped edges, further calculations are carried out in order
    // to determine the edge cells' next status
    private boolean[] calcNextGen(boolean[] currentGen, boolean[] rule) {
        boolean[] nextGen;
        nextGen = new boolean[numCells];
        for (int i = 1; i < numCells - 1; i++) {
            int value = 0;
            if (currentGen[i - 1] == true)
                value += 4;
            if (currentGen[i] == true)
                value += 2;
            if (currentGen[i + 1] == true)
                value += 1;
            nextGen[i] = rule[value];
        }
        if(edgesWrapped) {
            int value = 0;
            if (currentGen[numCells-1] == true)
                value += 4;
            if (currentGen[0] == true)
                value += 2;
            if (currentGen[1] == true)
                value += 1;
            nextGen[0] = rule[value];
            value = 0;
            if (currentGen[numCells-2] == true)
                value += 4;
            if (currentGen[numCells-1] == true)
                value += 2;
            if (currentGen[0] == true)
                value += 1;
            nextGen[numCells-1] = rule[value];
        }
        return nextGen;
    }

    @Override
    public void generate() {

        setGenState("Creating new canvas...");
        canvas = new Canvas(width, height);

        setGenState("Filling image background...");
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        setGenState("Calculating image...");
        // Calculate initial generation and rule table
        boolean[] currentGen = calcInitGen();
        boolean[] rule = calcTransitionFunction();
        
        // Output initial generation to image
        gc.setFill(Color.BLACK);
        for (int i = 0; i < currentGen.length; i++) {
            if (currentGen[i] == true) {
                gc.fillRect(i * cellSize, 0, cellSize, cellSize);
            }
        }

        // Remaining generations are calculated and written to image one by one
        for (int i = 1; i < numGens; i++) {
            currentGen = calcNextGen(currentGen, rule);
            for (int j = 0; j < currentGen.length; j++) {
                if (currentGen[j] == true) {
                    gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }

    }

}
