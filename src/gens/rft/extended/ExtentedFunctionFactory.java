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
package gens.rft.extended;
import gens.rft.Function;
import gens.rft.FunctionFactory;

/**
 *
 * @author Moritz Rieger
 */
public class ExtentedFunctionFactory extends FunctionFactory{
    
    protected final int TERTIARY = 3;
    
    public ExtentedFunctionFactory(int seed) {
        super(seed);
    }
    
    /**
     * if you add a function to this method make sure its picture is just in the right intervall [0,1]
     * @param id
     * @return 
     */
    private Function createFunction(int id){
        switch(id){
            case 0: return new Function(UNARY){
                @Override
                public double getResult(double x, double y){
                    return Math.abs(Math.sin(2*Math.PI*x));
                }
                };
            case 1: return new Function(UNARY){
                @Override
                public double getResult(double x, double y){
                    return Math.abs(Math.cos(2*Math.PI*x));
                }
                };
            case 2: return new Function(BINARY){
                @Override
                public double getResult(double x, double y){
                    return Math.pow(x, y);
                }
                };
            case 3: return new Function(BINARY){
                @Override
                public double getResult(double x, double y){
                    return (x+y)/2;
                }
                };
            case 4: return new Function(BINARY){
                @Override
                public double getResult(double x, double y){
                    return x*y;
                }
                };
            case 5: return new Function(BINARY){
                @Override
                public double getResult(double x, double y){
                    return Math.abs(x-y);
                }
                };
             case 6: return new Function(TERTIARY){
                @Override
                public double getResult(double x, double y){
                    return Math.abs(x-y);
                }
                };
            default: return null;
        }
    }
}
