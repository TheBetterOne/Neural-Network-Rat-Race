package nets;

import java.util.Arrays;

import static java.lang.Math.*;


public class Layer {

    private double[][] weights;

    private int numberOfInputs;

    private int numberOfOutputs;//This is also equal to the number of neurons.

    private boolean firing = false;
    private boolean training = false;

    public Layer(int numberOfInputs, int numberOfNeurons, int maxWeight){
        this.numberOfInputs = numberOfInputs;
        this.numberOfOutputs = numberOfNeurons;
        this.weights = initializeWeights(numberOfInputs + 1, numberOfNeurons, maxWeight);
    }

    private double activate(double input){
        return (tanh(input) + 1)/2;
    }

    private double derivativeOfActivate(double input){
        return (1 - pow(tanh(input), 2)) / 2;
    }

    public double[] fire(double[] input){
        input = Arrays.copyOf(input, numberOfInputs + 1);
        input[numberOfInputs] = 1;
        synchronized (this){
            while (training){
                try {
                    wait();
                } catch (InterruptedException ignored) {}
            }
            firing = true;
        }
        double[][] result = Matrices.apply(Matrices.multiply(Matrices.toMatrix(input), weights), this::activate);
        synchronized (this){
            firing = false;
            this.notifyAll();
        }
        return Matrices.toVector(result);
    }

    private double[][] initializeWeights(int width, int height, double maxWeight){

        double[][] result = new double[height][width];

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){

                result[i][j] = (2 * random() - 1) * maxWeight;

            }
        }

        return result;
    }

    public synchronized double[] train(double[] input, double[] expectedOutput, double learningRate){

        if (expectedOutput.length != numberOfOutputs){
            throw new IllegalArgumentException("Expected output has to have the same length as actual output!");
        }

        double[] output = fire(input);
        double[] error = new double[output.length];

        for (int i = 0; i < output.length; i++){
            error[i] = output[i] - expectedOutput[i];
        }

        return train(input, output, learningRate, error);
    }

    public synchronized double[] train(double[] input, double learningRate, double[] error){
        return train(input, fire(input), learningRate, error);
    }

    private synchronized double[] train(double[] input, double[] output, double learningRate, double[] error) {

        double[][] net = getNet(input);

        double[][] delta = Matrices.multiplyHadamard(Matrices.toMatrix(error), net);
        double[][] weightsDelta = Matrices.multiply(Matrices.multiplyKronecker(Matrices.toVector(delta), output), learningRate);

        synchronized (this){
            while (firing){
                try {
                    wait();
                } catch (InterruptedException ignored) {}
            }
            training = true;
        }
        weights = Matrices.add(weights, weightsDelta);
        synchronized (this){
            training = false;
            notifyAll();
        }

        double[] inputErrors = new double[numberOfInputs];

        for (int i = 0; i < numberOfInputs; i++){
            int sum = 0;
            for (int j = 0; j < numberOfOutputs; j++){
                sum += weightsDelta[i][j];
            }
            inputErrors[i] = sum;
        }

        return inputErrors;
    }

    private double[][] getNet(double[] input) {
        input = Arrays.copyOf(input, numberOfInputs + 1);
        input[numberOfInputs] = 1;
        return Matrices.multiply(Matrices.toMatrix(input), weights);
    }

}
