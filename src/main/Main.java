package main;

import simplex.Simplex;
import xfind.FuzzyProblem;
import xfind.Problem;

import java.util.List;
import java.util.Scanner;

/**
 * Created by Hlib on 15.12.2015.
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("console.encoding","cp866");
        /*Scanner scanner = new Scanner(System.in);
        System.out.print("Введите количество размеров полуфабрикатов(m): ");
        int m = Integer.parseInt(scanner.nextLine());
        Integer[] a = new Integer[m];
        for (int i = 0; i < m; i++) {
            System.out.print("Количество полуфабриката a["+(i+1)+"]= ");
            a[i] = Integer.parseInt(scanner.nextLine());
        }
        Integer[] alpha = new Integer[m];
        for (int i = 0; i < m; i++) {
            System.out.print("Размер полуфабриката альфа["+(i+1)+"]= ");
            alpha[i] = Integer.parseInt(scanner.nextLine());
        }
        System.out.print("Введите количество размеров необходимых деталей (l): ");
        int l = Integer.parseInt(scanner.nextLine());
        Integer[] k = new Integer[l];
        for (int i = 0; i < l; i++) {
            System.out.print("Количество деталей размера k["+(i+1)+"]= ");
            k[i] = Integer.parseInt(scanner.nextLine());
        }
        Integer[] t = new Integer[l];
        for (int i = 0; i < l; i++) {
            System.out.print("Размер детали t["+(i+1)+"]= ");
            t[i] = Integer.parseInt(scanner.nextLine());
        }*/
        int m = 1;
        Integer[] a = new Integer[]{75};
        Integer[] alpha = new Integer[]{92};
        int l = 3;
        Integer[] k = new Integer[]{3,5,2};
        Integer[] t = new Integer[]{32,18,12};

        Problem problem = new Problem(m,a,alpha,l,k,t);
        Simplex simplex = problem.createSimplexProblem();
        int initNV = simplex.getNumberOfVariables();
        simplex.prepare();
        List<Integer> ans = simplex.count();
        for (int i = 0; i < initNV - 1; i++) {
            if (ans.get(i) > 0) System.out.println("x["+(i+1)+"]="+ans.get(i));
        }
        System.out.println("Ответ: " + ans.get(initNV - 1));
        System.out.println("Задача оптимиста: ");
        FuzzyProblem lucky = new FuzzyProblem(m,a,alpha,l,k,t,alpha);
        lucky.lucky(0.8);
        simplex = lucky.createSimplexProblem();
        initNV = simplex.getNumberOfVariables();
        simplex.prepare();
        ans = simplex.count();
        for (int i = 0; i < initNV - 1; i++) {
            if (ans.get(i) > 0) System.out.println("x["+(i+1)+"]="+ans.get(i));
        }
        System.out.println("Ответ: " + ans.get(initNV - 1));
        simplex = lucky.createSimplexPriceProbem(c, sigma, 0.8);
        System.out.println("Задача пессимиста: ");
        FuzzyProblem unlucky = new FuzzyProblem(m,a,alpha,l,k,t,alpha);
        unlucky.unlucky(0.8);
        simplex = unlucky.createSimplexProblem();
        initNV = simplex.getNumberOfVariables();
        simplex.prepare();
        ans = simplex.count();
        for (int i = 0; i < initNV - 1; i++) {
            if (ans.get(i) > 0) System.out.println("x["+(i+1)+"]="+ans.get(i));
        }
        System.out.println("Ответ: " + ans.get(initNV - 1));
    }
}
