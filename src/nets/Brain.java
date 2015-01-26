package nets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Brain {

    private Layer[] layers;

    private final int numberOfInputs;
    private final int numberOfOutputs;
    private final int numberOfLayers;
    private boolean training = false;
    private boolean deciding = false;

    public Brain(int numberOfInputs, int numberOfOutputs, int numberOfLayers, int sizeOfEachLayer) {
        this.numberOfInputs = numberOfInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.numberOfLayers = numberOfLayers;
        layers = new Layer[numberOfLayers];
        if (numberOfLayers > 1) {
            layers[0] = new Layer(numberOfInputs, sizeOfEachLayer, 1);
            for (int i = 1; i < numberOfLayers - 1; i++) {
                layers[i] = new Layer(sizeOfEachLayer, sizeOfEachLayer, 1);
            }
            layers[numberOfLayers - 1] = new Layer(sizeOfEachLayer, numberOfOutputs, 1);
        } else {
            layers[0] = new Layer(numberOfInputs, numberOfOutputs, 1);
        }
    }

    public double[] convertToInput(String dna, Map<Point, Integer> vision){

        double[] result = new double[dna.length() + vision.size()];

        int i;

        for (i = 0; i < dna.length(); i++){

            result[i] = dna.charAt(i) == '1' ? 1 : -1;

        }

        List<Map.Entry<Point, Integer>> entryList = new ArrayList<>(vision.entrySet());

        entryList.sort((a,b) -> {
            if (a.getKey().x < b.getKey().x){
                return -1;
            }
            if (a.getKey().x > b.getKey().x){
                return 1;
            }
            if (a.getKey().y < b.getKey().y){
                return  -1;
            }
            if (a.getKey().y > b.getKey().y){
                return 1;
            }
            return 0;
        });

        for (Map.Entry<Point, Integer> entry : entryList){

            result[i] = entry.getValue() - 7.5;
            i++;
        }

        return result;
    }

    public Point convertToOutput(double[] output){

        if (output.length != 9){
            throw new IllegalArgumentException("Wrong length");
        }

        int highest = 0;
        for (int i = 0; i < output.length; i++){

            if (output[i] > output[highest]){
                highest = i;
            }

        }

        return new Point(highest / 3, highest % 3);

    }

    public double[] covertToOutputFormat(Point point){
        double[] result = new double[9];
        int index = point.x * 3 + point.y;

        for (int i = 0; i < 9; i++){

            if (i == index){
                result[i] = 1;
            } else {
                result[i] = -1;
            }

        }

        return result;
    }

    public double[] decide(double[] input){
        synchronized (this){
            while (training){
                try {
                    wait();
                } catch (InterruptedException ignored) {}
            }
            deciding = true;
        }
        for (Layer layer : layers){
            input = layer.fire(input);
        }
        synchronized (this){
            deciding = false;
            notifyAll();
        }
        return input;
    }

    public synchronized void train(double[] input, double[] error, double learningRate){

        synchronized (this){
            while (deciding){
                try {
                    wait();
                } catch (InterruptedException ignored){}
            }
            training = true;
        }

        double[][] inputs = new double[numberOfLayers][];

        for (int i = 0; i < numberOfLayers; i++){
            inputs[i] = input;
            input = layers[i].fire(input);
        }

        for (int i = numberOfLayers - 1; i >= 0; i++){
            error = layers[i].train(inputs[i], learningRate, error);
        }
        synchronized (this) {
            training = false;
            notifyAll();
        }
    }

    public double[] computeError(double[] expectedOutput, double[] output){
        double[] error = new double[output.length];

        for (int i = 0; i < error.length; i++){
            error[i] = expectedOutput[i] - output[i];
        }

        return error;
    }

}
