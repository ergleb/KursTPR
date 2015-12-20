package simplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hlib on 24.11.2015.
 */
public class Simplex {
    final double eps = 100000d;
    int numberOfVariables;
    List<Double> targetFunction;
    boolean maximize;
    int numberOfRestrictions;
    List<List<Double>> restrictions;
    int[] restrictionSigns;
    double[] restrictionConstants;

    public double getEps() {
        return eps;
    }

    public int getNumberOfVariables() {
        return numberOfVariables;
    }

    public void setNumberOfVariables(int numberOfVariables) {
        this.numberOfVariables = numberOfVariables;
    }

    public List<Double> getTargetFunction() {
        return targetFunction;
    }

    public void setTargetFunction(List<Double> targetFunction) {
        this.targetFunction = targetFunction;
    }

    public boolean isMaximize() {
        return maximize;
    }

    public void setMaximize(boolean maximize) {
        this.maximize = maximize;
    }

    public int getNumberOfRestrictions() {
        return numberOfRestrictions;
    }

    public void setNumberOfRestrictions(int numberOfRestrictions) {
        this.numberOfRestrictions = numberOfRestrictions;
    }

    public List<List<Double>> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<List<Double>> restrictions) {
        this.restrictions = restrictions;
    }

    public int[] getRestrictionSigns() {
        return restrictionSigns;
    }

    public void setRestrictionSigns(int[] restrictionSigns) {
        this.restrictionSigns = restrictionSigns;
    }

    public double[] getRestrictionConstants() {
        return restrictionConstants;
    }

    public void setRestrictionConstants(double[] restrictionConstants) {
        this.restrictionConstants = restrictionConstants;
    }



    public Simplex(){}

    public Simplex(int numberOfVariables, List<Double> targetFunction, boolean maximize, int numberOfRestrictions, List<List<Double>> restrictions, int[] restrictionSigns, double[] restrictionConstants) {
        this.numberOfVariables = numberOfVariables;
        this.targetFunction = targetFunction;
        this.maximize = maximize;
        this.numberOfRestrictions = numberOfRestrictions;
        this.restrictions = restrictions;
        this.restrictionSigns = restrictionSigns;
        this.restrictionConstants = restrictionConstants;
    }

    void maxToMin(){
        if (maximize){
            maximize = false;
            for (int i = 0; i < numberOfVariables; i++) {
                double temp = targetFunction.get(i);
                temp *=-1;
                targetFunction.remove(i);
                targetFunction.add(i,temp);
            }
        }
    }
    public void prepare(){
        for (int i = 0; i < numberOfRestrictions; i++) {
            if (restrictionSigns[i] != 0){
                numberOfVariables++;
                targetFunction.add(0.0);
                for (int j = 0; j < numberOfRestrictions; j++) {
                    restrictions.get(j).add(i == j ? -restrictionSigns[i] : 0.0);
                }
            }
        }
        for (int i = 0; i < numberOfRestrictions; i++) {
            if (restrictionSigns[i] >= 0){
                numberOfVariables++;
                targetFunction.add(-999999.0);
                for (int j = 0; j < numberOfRestrictions; j++) {
                    restrictions.get(j).add(i == j ? 1.0 : 0.0);
                }
            }
        }
    }
    public void outp(){
        System.out.print(maximize?"max ":"min ");
        for (int i = 0; i < numberOfVariables; i++) {
            System.out.print(targetFunction.get(i) + "*x" + i + " + ");
        }
        System.out.println();
        for (int i = 0; i < numberOfRestrictions; i++) {
            for (int j = 0; j < numberOfVariables; j++) {
                System.out.print(restrictions.get(i).get(j) + "*x" + j + " + ");
            }
            System.out.print(restrictionSigns[i]>0 ? " >= " : restrictionSigns[i]<0 ? " <= " : " = ");
            System.out.println(restrictionConstants[i]);
        }
    }

    public List<Integer> count(){
        List<List<Double>> table = new ArrayList<>();
        for (int i = 0; i < numberOfRestrictions; i++) {
            table.add(new ArrayList<>());
        }
        List<Integer> basis = new ArrayList<>(numberOfRestrictions);
        for (int i = 0; i < numberOfRestrictions; i++) {
            basis.add(-1);
        }
        List<Double> delta = new ArrayList<>(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            delta.add(0.0);
        }
        List<Double> result = new ArrayList<>(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            result.add(0.0);
        }
        outer:
        for (int i = numberOfVariables - 1; i >= 0; i--) {
            boolean isBasis = false;
            int basisPos = 0;
            for (int j = 0; j < numberOfRestrictions; j++) {
                double temp = restrictions.get(j).get(i);
                if (temp != 0 && (temp != 1 || isBasis)) continue outer;
                if (temp == 1){
                    isBasis = true;
                    basisPos = j;
                }
            }
            if (isBasis && basis.get(basisPos) == -1){
                basis.set(basisPos,i);
                result.set(i,restrictionConstants[basisPos]);
                List<Double> tableAdd = new ArrayList<>();
                for (double k: restrictions.get(basisPos)){
                    tableAdd.add(k);
                }
                table.set(basisPos,tableAdd);
            }
        }
        for (int i = 0; i < numberOfVariables; i++) {
            double sum = 0;
            for (int j = 0; j < numberOfRestrictions; j++) {
                sum += (double)targetFunction.get(basis.get(j)) * table.get(j).get(i);
            }
            delta.set(i, sum - targetFunction.get(i));
        }
        //System.out.println(Arrays.toString(result.toArray()));
        //System.out.println(Arrays.toString(basis.toArray()));
        /*for (int i = 0; i < table.size(); i++) {
            System.out.println(Arrays.toString(table.get(i).toArray()));
        }*/
        //System.out.println(Arrays.toString(delta.toArray()));
        boolean isOptimal = false,
                isUndoable = false;
        while (!isOptimal && !isUndoable){
            int column = -1;
            double minDelta = 0;
            for (int i = 0; i < numberOfVariables; i++) {
                if (delta.get(i) < minDelta){
                    minDelta = delta.get(i);
                    column = i;
                }
            }
            if (column < 0){
                isOptimal = true;
                break;
            }
            int row = -1;
            double minRel = Double.MAX_VALUE;
            for (int i = 0; i < numberOfRestrictions; i++) {
                double res = result.get(basis.get(i));
                double tab = table.get(i).get(column);
                if (res >= 0 && tab > 0 && res / tab < minRel){
                    row = i;
                    minRel = res / tab;
                }
            }
            if (row == -1){
                isUndoable = true;
                break;
            }
            //System.out.println("column " + column + "row " + row);
            double tmp = result.get(basis.get(row));
            result.set(column, (double)Math.round(tmp * eps)/eps);
            result.set(basis.get(row), 0.0);
            basis.set(row,column);
            double div = table.get(row).get(column);
            tmp = result.get(basis.get(row)) / div;
            result.set(basis.get(row), (double)Math.round(tmp * eps)/eps);
            for (int i = 0; i < numberOfVariables; i++) {
                double temp = table.get(row).get(i);
                tmp = temp / div;
                table.get(row).set(i, (double)Math.round(tmp * eps)/eps);
            }
            for (int i = 0; i < numberOfRestrictions; i++) {
                if (i != row){
                    double temp = table.get(i).get(column);
                    for (int j = 0; j < numberOfVariables; j++) {
                        double prev = table.get(i).get(j);
                        tmp = prev - temp * table.get(row).get(j);
                        table.get(i).set(j, (double)Math.round(tmp * eps)/eps);
                    }
                    tmp = result.get(basis.get(i)) - temp * result.get(basis.get(row));
                    result.set(basis.get(i),(double)Math.round(tmp * eps)/eps);
                }
            }
            //System.out.println("basis:");
            //System.out.println(Arrays.toString(basis.toArray()));
            //System.out.println();
            //System.out.println("table:");
            /*for (int i = 0; i < table.size(); i++) {
                System.out.print(result.get(basis.get(i)) + " ");
                System.out.println(Arrays.toString(table.get(i).toArray()));
            }*/
            for (int i = 0; i < numberOfVariables; i++) {
                double sum = 0;
                for (int j = 0; j < numberOfRestrictions; j++) {
                    sum += targetFunction.get(basis.get(j)) * table.get(j).get(i);
                }
                tmp = sum - targetFunction.get(i);
                delta.set(i, (double)Math.round(tmp * eps)/eps);
            }
            //System.out.println("delta");
            //System.out.println(Arrays.toString(delta.toArray()));
        }
        //if(isOptimal) System.out.println("answer " + Arrays.toString(result.toArray()));
        //if(isUndoable) System.out.println("Can't do it:(");
        //System.out.println(Arrays.toString(result.toArray()));
        if(isOptimal) {
            boolean exNotInt = true;
            int iterNum = 0;
            //System.out.println("Starting integer part:");
            while (exNotInt) {
                exNotInt = false;
                iterNum++;
                if (iterNum > 1) break;
                int nor = numberOfRestrictions;
                for (int i = 0; i < nor; i++) {
                    int n = basis.get(i);
                    if (Math.abs(result.get(n) - Math.round(result.get(n))) > 0.1) {
                        exNotInt = true;
                        //numberOfVariables++;
                        //numberOfRestrictions++;
                        table.add(new LinkedList<>());
                        for (int j = 0; j < numberOfRestrictions; j++) {
                            table.get(j).add(0d);
                        }
                        for (int j = 0; j < numberOfVariables + 1; j++) {
                            table.get(numberOfRestrictions).add((double) Math.round(-Math.abs(table.get(i).get(j) % 1d) * eps) / eps);
                        }

                        table.get(numberOfRestrictions).set(numberOfVariables, 1.0);
                        basis.add(numberOfVariables);
                        result.add((double) Math.round(-Math.abs(result.get(basis.get(i)) % 1d) * eps) / eps);
                        //System.out.println("basis:");
                        //System.out.println(Arrays.toString(basis.toArray()));
                        //System.out.println();
                        //System.out.println("table:");
                        for (int j = 0; j < table.size(); j++) {
                            //System.out.print(result.get(basis.get(j)) + " ");
                            //System.out.println(Arrays.toString(table.get(j).toArray()));
                        }
                        delta.add(0.0);
                        targetFunction.add(0.0);
                        for (int j = 0; j < numberOfVariables + 1; j++) {
                            double sum = 0;
                            for (int k = 0; k < numberOfRestrictions + 1; k++) {
                                sum += targetFunction.get(basis.get(k)) * table.get(k).get(j);
                            }
                            double tmp = sum - targetFunction.get(j);
                            delta.set(j, (double) Math.round(tmp * eps) / eps);
                        }
                        boolean moreThanZero = false;
                        while (!moreThanZero) {
                            double minRes = 0d;
                            int row = -1;
                            for (int j = 0; j < basis.size(); j++) {
                                if (result.get(basis.get(j)) < minRes) {
                                    row = j;
                                    minRes = result.get(basis.get(j));
                                }
                            }
                            int column = -1;
                            double minDelta = Double.MAX_VALUE;
                            for (int j = 0; j < numberOfVariables + 1; j++) {
                                if (Math.abs(table.get(numberOfRestrictions).get(j)) > 0.05 && Math.abs(delta.get(j) / table.get(numberOfRestrictions).get(j)) < minDelta) {
                                    minDelta = table.get(numberOfRestrictions).get(j);
                                    column = j;
                                }
                            }
                            double tmp = result.get(basis.get(row));
                            result.set(column, (double) Math.round(tmp * eps) / eps);
                            result.set(basis.get(row), 0.0);
                            basis.set(row, column);
                            double div = table.get(row).get(column);
                            tmp = result.get(basis.get(row)) / div;
                            result.set(basis.get(row), (double) Math.round(tmp * eps) / eps);
                            for (int j = 0; j < numberOfVariables + 1; j++) {
                                double temp = table.get(row).get(j);
                                tmp = temp / div;
                                table.get(row).set(j, (double) Math.round(tmp * eps) / eps);
                            }
                            for (int j = 0; j < numberOfRestrictions + 1; j++) {
                                if (j != row) {
                                    double temp = table.get(j).get(column);
                                    for (int k = 0; k < numberOfVariables; k++) {
                                        double prev = table.get(j).get(k);
                                        tmp = prev - temp * table.get(row).get(k);
                                        table.get(j).set(k, (double) Math.round(tmp * eps) / eps);
                                    }
                                    tmp = result.get(basis.get(j)) - temp * result.get(basis.get(row));
                                    result.set(basis.get(j), (double) Math.round(tmp * eps) / eps);
                                }
                            }
                            moreThanZero = true;
                            for (int bas : basis) {
                                if (result.get(bas) < -0.1) {
                                    moreThanZero = false;
                                }
                            }
                        }
                        //System.out.println("basis:");
                        //System.out.println(Arrays.toString(basis.toArray()));
                        //System.out.println();
                        //System.out.println("table:");
                    /*for (int j = 0; j < table.size(); j++) {
                        System.out.print(result.get(basis.get(j)) + " ");
                        System.out.println(Arrays.toString(table.get(j).toArray()));
                    }*/
                        numberOfRestrictions++;
                        numberOfVariables++;
                    }
                }
                //System.out.println(iterNum);
                //System.out.println(Arrays.toString(result.toArray()));
            }
        }
        //System.out.println(Arrays.toString(result.toArray()));
        List<Integer> ans = new LinkedList<>();
        for (int i = 0; i < numberOfVariables; i++) {
            ans.add((int)Math.round(result.get(i)));
        }
        //System.out.println(Arrays.toString(ans.toArray()));
        return ans;
    }


    public static void main(String[] args) {
        //Double[] tf = new Double[]{-40d,-40d,-10d,-80d,-80d,-30d,-60d,-50d,-1210d,-1240d,-1170d,-1220d,-684d,-674d,-554d,-624d,-90d,-110d,-140d,-130d,-150d,-110d};
        List<Double> targetFunction = new LinkedList(Arrays.asList(new Double[]{-40d,-40d,-10d,-80d,-80d,-30d,-60d,-50d,-1210d,-1240d,-1170d,-1220d,-684d,-674d,-554d,-624d,-90d,-110d,-140d,-130d,-150d,-110d}));
        List<List<Double>> restrictions = new LinkedList<>();
        restrictions.add(new LinkedList(Arrays.asList(new Double[]{-1d,-1d,-1d,-1d,0d,0d,0d,0d,10d,10d,10d,10d,0d,0d,0d,0d,40d,0d,40d,0d,40d,0d})));
        restrictions.add(new LinkedList(Arrays.asList(new Double[]{0d,0d,0d,0d,-1d,-1d,-1d,-1d,0d,0d,0d,0d,8d,8d,8d,8d,0d,40d,0d,40d,0d,40d})));
        restrictions.add(new LinkedList(Arrays.asList(new Double[]{1d,0d,0d,0d,1d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d})));
        restrictions.add(new LinkedList(Arrays.asList(new Double[]{0d,1d,0d,0d,0d,1d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d})));
        restrictions.add(new LinkedList(Arrays.asList(new Double[]{0d,0d,1d,0d,0d,0d,1d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d})));
        restrictions.add(new LinkedList(Arrays.asList(new Double[]{0d,0d,0d,1d,0d,0d,0d,1d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d})));
        int[] restrictionSigns = new int[]{1,1,1,1,1,1};
        double[] restrictionConstants = new double[]{0d,0d,1000d,1200d,1500d,2000d};
        Simplex simplex = new Simplex(22, targetFunction, false, 6, restrictions, restrictionSigns,restrictionConstants);
        simplex.prepare();
        List<Integer> ans = simplex.count();
        System.out.println("answer: " + Arrays.toString(ans.toArray()));
        int znach = 1382400;
        for (int i = 0; i < targetFunction.size(); i++) {
            znach -= ans.get(i)*targetFunction.get(i);
        }
        System.out.println("Znach:" + znach);
    }
}
