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
package gens.rft;

import java.util.Random;

/**
 *
 * @author Moritz Rieger
 */
public class FunctionFactory {
    
    private static final int UNARY = 1;
    private static final int BINARY = 2;
    private Function[] functionPool;
    private Random random;
    
    public FunctionFactory(int seed){
        createFunctions();
        random = new Random(seed);
    }
    
    private void createFunctions(){
        functionPool = new Function[6];
        //unary functions
        functionPool[0] = new Function(UNARY){
          @Override
          public double getResult(double x, double y){
              return Math.sin(2*Math.PI*x);
          }
        };
        functionPool[1] = new Function(UNARY){
          @Override
          public double getResult(double x, double y){
              return Math.cos(2*Math.PI*x);
          }
        };
        functionPool[2] = new Function(BINARY){
          @Override
          public double getResult(double x, double y){
              return Math.pow(x, y);
          }
        };
        //binary functions
        functionPool[3] = new Function(BINARY){
          @Override
          public double getResult(double x, double y){
              return (x+y)/2;
          }
        };
        functionPool[4] = new Function(BINARY){
          @Override
          public double getResult(double x, double y){
              return x*y;
          }
        };
        functionPool[5] = new Function(BINARY){
          @Override
          public double getResult(double x, double y){
              return x-y;
          }
};
    }
    public Function getRandomFunction(){
        return functionPool[random.nextInt(functionPool.length-1)];
    }
    
}
