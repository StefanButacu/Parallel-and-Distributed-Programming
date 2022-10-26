import java.util.function.BinaryOperator;

public class LabInClass {

    public static void main(String[] args) {
        int NMAX = 10000000;
        int noThreads = 16;
        double[] a = new double[NMAX];
        double[] b = new double[NMAX];
        double[] cSecv = new double[NMAX];
        double[] cPar = new double[NMAX];
        double[] cCycle = new double[NMAX];

        BinaryOperator<Double> operator = (aDouble, bDouble) -> aDouble + bDouble;

        operator = ((aDouble, bDouble) -> Math.sqrt(Math.pow(aDouble, 3) + Math.pow(bDouble, 3)));
        for(int i = 0 ; i < NMAX; i++){
            a[i] = i;
            b[i] = -i;
        }

        long start = System.nanoTime();
        addVectors(a,b,cSecv, operator);
        long end = System.nanoTime();
        System.out.println("Secv\n" +  (end - start));
        start = System.nanoTime();
        addParallel(a,b,cPar, noThreads, operator);
        end = System.nanoTime();
        System.out.println("Distributed\n" + (end - start));

        System.out.println(areVectorEquals(cSecv, cPar));

        start = System.nanoTime();
        addParallelCycle(a, b, cCycle, noThreads, operator);
        end = System.nanoTime();
        System.out.println("Cycle\n" + (end - start));

        System.out.println(areVectorEquals(cSecv, cCycle));

    }

    public static void addVectors(double[] a, double[] b, double[] c, BinaryOperator<Double> operator){
        for(int i = 0 ; i < a.length; i++){
            c[i] = operator.apply(a[i], b[i]);
        }
    }

    public static boolean areVectorEquals(double[] a, double[] b){
        for(int i = 0 ; i < a.length; i++){
            if( Math.abs(a[i] -b[i]) > 0.001)
                return false;
        }
        return true;
    }

    public static class MyThread extends Thread {

        private double[] a;
        private double[] b;
        private double[] c;
        private int start;
        private int end;
        private BinaryOperator<Double> operator;
        public MyThread(double[] a, double[] b, double[] c, int start, int end, BinaryOperator<Double> operator){
            super();
            this.a = a;
            this.b = b;
            this.c = c;
            this.start = start;
            this.end = end;
            this.operator = operator;
        }

        @Override
        public void run(){
            for(int i = start; i < end; i++){
//                c[i] = a[i] + b[i];
//                c[i] = Math.sqrt(Math.pow(a[i],3) + Math.pow(b[i], 3));
                c[i] = operator.apply(a[i], b[i]);

            }
        }
    }

    public static void addParallel(double[] a, double[] b, double[] c, int noThreads, BinaryOperator<Double> operator){
        Thread[] threads = new Thread[noThreads];
        int size = a.length;
        int rest = size % noThreads;
        int start = 0;
        int end = size / noThreads;
        for (int i = 0; i < noThreads; i++){
            if (rest > 0) {
                end++;
                rest--;
            }
            threads[i] = new MyThread(a,b,c, start,end, operator);
            threads[i].start();
            start = end;
            end += size / noThreads;
        }

        for(int i = 0 ; i < noThreads; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public static void addParallelCycle(double[] a, double[] b, double[] c, int noThreads, BinaryOperator<Double> operator){
        Thread[] threads = new Thread[noThreads];
        int size = a.length;
        for (int i = 0; i < noThreads; i++){
            threads[i] = new MyThreadCycle(a,b,c,noThreads, i,size, operator);
            threads[i].start();
        }
        for(int i = 0 ; i < noThreads; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public static class MyThreadCycle extends Thread {

        private double[] a;
        private double[] b;
        private double[] c;
        private int start;
        private int end;
        private BinaryOperator<Double> operator;

        private int step;
        public MyThreadCycle(double[] a, double[] b, double[] c,int step, int start, int end, BinaryOperator<Double> operator){
            super();
            this.a = a;
            this.b = b;
            this.c = c;
            this.step = step;
            this.start = start;
            this.end = end;
            this.operator = operator;
        }

        @Override
        public void run(){
            for(int i = start; i < end; i+= step){
                c[i] =operator.apply(a[i], b[i]);
            }
        }
    }
}