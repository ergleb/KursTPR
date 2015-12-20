package xfind;

import simplex.Simplex;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hlib on 09.12.2015.
 */
public class FuzzyProblem extends Problem {
    Integer[] alphaAverage;
    boolean isLucky = false;
    public FuzzyProblem(int m, Integer[] a, Integer[] alpha, int l, Integer[] k, Integer[] t, Integer[] alphaAverage) {
        super(m, a, alpha, l, k, t);
        this.alphaAverage = alphaAverage;
    }
    public void unlucky(){
        for (int i = 0; i < alphaAverage.length; i++) {
            alpha[i] = (int)Math.round(alphaAverage[i] * 0.5);
        }
    }
    public void lucky(){
        for (int i = 0; i < alphaAverage.length; i++) {
            alpha[i] = (int)Math.round(alphaAverage[i] * 1.5);
        }
    }
    public void unlucky(double step){
        for (int i = 0; i < alphaAverage.length; i++) {
            alpha[i] = (int)Math.round(alphaAverage[i]*(1 - Math.sqrt(1/step - 1)));
        }
    }
    public void lucky(double step){
        for (int i = 0; i < alphaAverage.length; i++) {
            alpha[i] = (int)Math.round(alphaAverage[i]*(1 + Math.sqrt(1/step - 1)));
        }
        isLucky = true;
    }
    public Simplex createSimplexPriceProbem(double[] c, double[] sigma, double step){
        Simplex oldSimplex = this.createSimplexProblem();
        List<Double> targetFunction = new LinkedList<>();
        for (int i = 0; i < oldSimplex.getNumberOfVariables() - 1; i++) {
            double sum = 0;
            for (int j = 0; j < c.length; j++) {
                sum += oldSimplex.getRestrictions().get(j).get(i) * ((c[j]) +
                        Math.sqrt(-2d*Math.pow(sigma[j],2)*Math.log(step))*(isLucky?1:-1));
            }
            targetFunction.add(sum);
        }
        targetFunction.add(0d);
        oldSimplex.setTargetFunction(targetFunction);
        return oldSimplex;
    }
    public static void main(String[] args) {
        FuzzyProblem problem = new FuzzyProblem(2,new Integer[]{75,75},new Integer[2],3,new Integer[]{3,5,2},new Integer[]{32,18,12},new Integer[]{90,100});
        //Problem problem = new Problem(3,new Integer[]{113,264,48},new Integer[]{250,190,150},3,new Integer[]{5,4,12},new Integer[]{54,42,30});
        problem.lucky();
        Simplex simplex = problem.createSimplexProblem();
        simplex.prepare();
        List<Integer> ans = simplex.count();
        System.out.println("answer: " + Arrays.toString(ans.toArray()));
        List<Double> tf = simplex.getTargetFunction();
        double result = 0;
        for (int i = 0; i < simplex.getNumberOfVariables(); i++) {
            result += tf.get(i)*ans.get(i);
        }
        System.out.println("result: "+(int)result);
    }
}
