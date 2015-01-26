package nets;

import java.util.function.Function;

public class Matrices {

    public static double[][] transpose(double[][] matrix){
        double[][] result = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++){
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    public static double[][] add(double[][] matrix1, double[][] matrix2){

        if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length){
            throw new IllegalArgumentException("nets.Matrices have to be the same size");
        }

        double[][] result = new double[matrix1.length][matrix1[0].length];

        for (int i = 0; i < matrix1.length; i++){
            for (int j = 0; j < matrix1[0].length; j++){

                result[i][j] = matrix1[i][j] + matrix2[i][j];

            }
        }

        return result;
    }

    public static double[][] multiply(double[][] matrix, double scalar){

        double[][] result = new double[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++){

                result[i][j] = matrix[i][j] * scalar;

            }
        }

        return result;
    }

    public static double[][] multiply(double[][] matrix1, double[][] matrix2){

        if (matrix1[0].length != matrix2.length){
            throw new IllegalArgumentException("nets.Matrices cannot be multiplied.");
        }

        int m = matrix2.length;
        int height = matrix1.length;
        int width = matrix2[0].length;

        double[][] result = new double[height][width];

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){

                int sum = 0;

                for (int k = 0; k < m; k++){

                    sum += matrix1[i][k] * matrix2[k][j];

                }

                result[i][j] = sum;
            }
        }

        return result;
    }

    public static double[][] multiplyHadamard(double[][] matrix1, double[][] matrix2){

        if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length){
            throw new IllegalArgumentException("nets.Matrices must be same size.");
        }

        double[][] result = new double[matrix1.length][matrix1[0].length];

        for (int i = 0; i < matrix1.length; i++){
            for (int j = 0; j < matrix1[0].length; j++){

                result[i][j] = matrix1[i][j] * matrix2[i][j];

            }
        }

        return result;

    }

    public static double[][] multiplyKronecker(double[] row, double[] col){

        double[][] result = new double[col.length][row.length];

        for (int i = 0; i < col.length; i++){
            for (int j = 0; j < row.length; j++){

                result[i][j] = col[i] * row[j];

            }
        }

        return result;
    }

    public static double[][] multiplyKronecker(double[][] row, double[][] col){

        if (row.length > 1 || col[0].length > 1){
            throw new IllegalArgumentException("These are not valid rows/columns.");
        }

        double[][] result = new double[col.length][row[0].length];

        for (int i = 0; i < col.length; i++){
            for (int j = 0; j < row[0].length; j++){

                result[i][j] = col[i][0] * row[0][j];

            }
        }

        return result;
    }

    public static double[][] concatenateHorizontal(double[][] matrix1, double[][] matrix2){

        if (matrix1.length != matrix2.length){
            throw new IllegalArgumentException("These matrices cannot be concatenated together.");
        }

        double[][] result = new double[matrix1.length][matrix1[0].length + matrix2[0].length];

        for (int i = 0; i < matrix1.length; i++){
            for (int j = 0; j < matrix1[0].length + matrix2[0].length; j++){

                result[i][j] = j >= matrix1[0].length ? matrix2[i][j - matrix1[0].length] : matrix1[i][j];

            }
        }

        return result;
    }

    public static double[][] toMatrix(double[] vector){

        double[][] result = new double[0][vector.length];

        for (int i = 0; i < vector.length; i++){
            result[0][i] = vector[i];
        }

        return result;
    }

    public static double[] toVector(double[][] matrix) {
        if (matrix.length != 1){
            throw new IllegalArgumentException("Cannot be converted to a vector!");
        }

        double[] result = new double[matrix[0].length];

        for (int i = 0; i < result.length; i++){
            result[i] = matrix[0][i];
        }

        return result;
    }

    public static double[][] apply(double[][] matrix, Function<Double, Double> function){

        double[][] result = new double[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++){

                result[i][j] = function.apply(matrix[i][j]);

            }
        }

        return result;

    }
}
