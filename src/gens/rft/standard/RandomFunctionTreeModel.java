/*
 * The MIT License
 *
 * Copyright 2017 Moritz Rieger.
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
package gens.rft.standard;

import general.GenModel;
import gens.rft.Function;
import gens.rft.TreeNode;
import java.util.Random;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *
 * @author Moritz Rieger
 */
public class RandomFunctionTreeModel extends GenModel {
    
    private static final int UNARY = 1;
    private static final int BINARY = 2;
    private final int R;
    private final int G;
    private final int B;
    
    private final IntegerProperty widthProperty = new SimpleIntegerProperty(250);
    private final IntegerProperty heightProperty = new SimpleIntegerProperty(250);
    private final IntegerProperty minDepthProperty = new SimpleIntegerProperty(2);
    private final IntegerProperty maxDepthProperty = new SimpleIntegerProperty(6);
    private final IntegerProperty seedProperty = new SimpleIntegerProperty(10);
    private Function[] functions;
    private Random random;
    
    public RandomFunctionTreeModel(){
        createFunctions();
        random = new Random(seedProperty.getValue());
        R = random.nextInt(255);
        G = random.nextInt(255);
        B = random.nextInt(255);
        
    }
    
    public void validate() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getGenName() {
        return "Standard Random Function Tree Generator";
    }

    @Override
    public void generate() {
        Function rootNode = createTree(minDepthProperty.getValue());
        canvas = new Canvas(widthProperty.getValue(), heightProperty.getValue());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        //loop through every pixel
        for(int x = 0; x < widthProperty.getValue(); x++){
            for(int y = 0; y < heightProperty.getValue(); y++){
                int rnd = random.nextInt(functions.length);
                //System.out.println("function no. " + rnd);
                double[] nCoords = normalize(x,y);
                //double result = functions[rnd].getResult(nCoords[0], nCoords[1]);
                double result = evalRFT(rootNode, nCoords[0], nCoords[1]);
                //System.out.println("Result "+result+" for x"+ x+ " y " + y);
                int percentage =(int) ((double)(x*heightProperty.getValue())/(double)(widthProperty.getValue()*heightProperty.getValue())*100);
                setGenState("Calculating Randomized Function Tree Image  " + percentage + " %");
                pw.setColor(x, y, getColor(result));
                //gc.setFill(Color.WHITE);
                //gc.fillRect(0, 0, 500, 500);
                waitForCanvasIterationDisplayedInApp();
            }
        }
    }
    
    public IntegerProperty getWidthProperty() {
    return widthProperty;
    }

    public IntegerProperty getHeightProperty() {
        return heightProperty;
    }

    public IntegerProperty getMinDepthProperty() {
        return minDepthProperty;
    }

    public IntegerProperty getMaxDepthProperty() {
        return maxDepthProperty;
    }
    private void createFunctions(){
        functions = new Function[6];
        //unary functions
        functions[0] = new Function(UNARY){
          @Override
          public double getResult(double x, double y){
              return Math.sin(2*Math.PI*x);
          }
        };
        functions[1] = new Function(UNARY){
          @Override
          public double getResult(double x, double y){
              return Math.cos(2*Math.PI*x);
          }
        };
        functions[2] = new Function(BINARY){
          @Override
          public double getResult(double x, double y){
              return Math.pow(x, y);
          }
        };
        //binary functions
        functions[3] = new Function(BINARY){
          @Override
          public double getResult(double x, double y){
              return (x+y)/2;
          }
        };
        functions[4] = new Function(BINARY){
          @Override
          public double getResult(double x, double y){
              return x*y;
          }
        };
        functions[5] = new Function(BINARY){
          @Override
          public double getResult(double x, double y){
              return x-y;
          }
        };
    }
    private double[] normalize(int x,int y){
        double[] result = {x/widthProperty.doubleValue(), y/heightProperty.doubleValue()};
        return result;
    }
    /**
     * creates recursively a directional Tree in the given depth
     * @param depth
     * @return 
     */
    private Function createTree(int depth){
        Function node;
        //take random function from pool
        node = functions[random.nextInt(functions.length-1)];
        //create one children for unary, two for binary ...
          if(depth > 0){
            Function[] children = new Function[node.getType()];
            for(int i=0; i < children.length; i++){
                children[i] = createTree(depth-1);
            }
            node.setChildren(children);
        }
        System.out.println("childrencount"+node.getChildrenCount());
        return node;
    }
    /**
     * recursively evaluate a random function tree
     * @param node
     * @param x
     * @param y
     * @return 
     */
    private double evalRFT(Function node, double x, double y){
        System.out.println("count" + node.getChildrenCount());
        if(node.getChildrenCount() > 0){
            Function[] children = (Function[]) node.getChildren();
            //save results from each children
            double[] values = new double[children.length];
            for(int i=0; i<children.length; i++){
                values[i] = evalRFT(children[i], x, y);
            }
            //when all childrens are evaluated we can set the values of the children in this nodes function
            switch (node.getType()){
                case UNARY: return node.getResult(values[0], values[0]);
                case BINARY: return node.getResult(values[0], values[1]);
                default: return node.getResult(values[0], values[0]); //this should never be reached, just to satisfy javacompiler
            }
        }else{
            System.out.println("Coords for x"+ x+ " y " + y);
            return node.getResult(x, y);
        }
    }
    
    private Color getColor(double val){
        int r = (int) (R * val);
        int g = (int) (G * val);
        int b = (int) (B * val);
        return Color.rgb(r,g,b);
    }

}
