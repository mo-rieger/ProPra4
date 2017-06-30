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
 * This Factory creates pseudorandom Functions depended on the seed you
 * instanciated the Factory with. Every Functions domain and co-domain is from
 * [0,1]
 *
 * @author Moritz Rieger
 */
public class FunctionFactory {

    protected static final int UNARY = 1;
    protected static final int BINARY = 2;
    protected final int TERNARY = 3;
    protected final int QUATERNARY = 4;
    protected final int QUINARY = 5;
    //need to be adjusted when you add a new function into the createFunction method
    private final int UNARY_BINARY_FUNCTIONS = 5;
    protected final int AMOUNT_OF_FUNCTIONS = 10;

    protected Random random;

    public FunctionFactory(int seed) {
        random = new Random(seed);
    }

    /**
     * This is the heart of the factory. In every case the getResult() Method
     * from a common Function is overriden by a anonymous class. Its very
     * important that the Function just goes from [0,1]->[0,1] otherwise the rft
     * generator wont work properly, you'll get an Exceptions in the Color
     * calculating method. if you add a function to this method make sure its
     * picture is just in the right intervall [0,1]
     *
     * @param id
     * @return
     */
    protected Function createFunction(int id) {
        switch (id) {
            case 0:
                return new Function(UNARY) {
                    @Override
                    public double getResult(double[] param) {
                        return Math.abs(Math.sin(2 * Math.PI * param[0]));
                    }
                };
            case 1:
                return new Function(UNARY) {
                    @Override
                    public double getResult(double[] param) {
                        return Math.abs(Math.cos(2 * Math.PI * param[0]));
                    }
                };
            case 2:
                return new Function(BINARY) {
                    @Override
                    public double getResult(double[] param) {
                        return Math.pow(param[0], param[1]);
                    }
                };
            case 3:
                return new Function(BINARY) {
                    @Override
                    public double getResult(double[] param) {
                        return (param[0] + param[1]) / 2;
                    }
                };
            case 4:
                return new Function(BINARY) {
                    @Override
                    public double getResult(double[] param) {
                        return param[0] * param[1];
                    }
                };
            case 5:
                return new Function(BINARY) {
                    @Override
                    public double getResult(double[] param) {
                        return Math.abs(param[0] - param[1]);
                    }
                };
            case 6:
                return new Function(TERNARY) {
                    @Override
                    public double getResult(double[] param) {
                        return (param[0] + param[1] + param[2]) / 3;
                    }
                };
            case 7:
                return new Function(QUATERNARY) {
                    @Override
                    public double getResult(double[] param) {
                        return Math.abs(
                                Math.sin(2 * Math.PI * param[0]) *
                                Math.cos(2 * Math.PI * param[1]) *
                                Math.sin(2 * Math.PI * param[2]) *
                                Math.cos(2 * Math.PI * param[3]));
                    }
                };
            case 8:
                return new Function(QUATERNARY) {
                    @Override
                    public double getResult(double[] param) {
                        return ((param[0] * param[1]) +
                                (param[2] * param[3])) / 2;
                    }
                };
            case 9:
                return new Function(QUINARY) {
                    @Override
                    public double getResult(double[] param) {
                        return Math.abs(
                                (Math.sin(2 * Math.PI * param[0]) *
                                 Math.cos(2 * Math.PI * param[1]) *
                                 Math.sin(2 * Math.PI * param[2]) *
                                 Math.cos(2 * Math.PI * param[3]) +
                                 param[4]
                                ) / 2);
                    }
                };
            default:
                return null;
        }
    }

    public void setSeed(int seed) {
        random = new Random(seed);
    }

    /**
     *
     * @return a random function wich is just unary or binary
     */
    public Function getUnaryOrBinaryFunction() {
        return createFunction(random.nextInt(UNARY_BINARY_FUNCTIONS));
    }

    /**
     *
     * @return a random Function this could be one with more than two parameters
     */
    public Function getRandomFunction() {
        return createFunction(random.nextInt(AMOUNT_OF_FUNCTIONS));
    }

}
