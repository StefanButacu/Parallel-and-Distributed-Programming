import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {

        Cont cont = new Cont(100, 100);
        List<Tranzactie> tranzactieList = new ArrayList<>();
        Lock lock = new ReentrantLock();
        Condition after5 = lock.newCondition();
        Condition hasPrinted = lock.newCondition();
        AtomicBoolean isFinished = new AtomicBoolean(false);
        int n = 3;
        AtomicInteger oldSize = new AtomicInteger(0);

        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new FamilyMember(cont, tranzactieList, lock, after5, oldSize, hasPrinted);
        }

        Thread iteratorThread = new IteratorThread(tranzactieList, lock, after5, isFinished, oldSize, hasPrinted);
        iteratorThread.start();

        for (int i = 0; i < n; i++) {
            threads[i].start();
        }

        for (int i = 0; i < n; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        isFinished.set(true);
        lock.lock();
        after5.signal();
        lock.unlock();
        try {
            iteratorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Lei: " + cont.getSoldLei());
        System.out.println("Eur: " + cont.getSoldEur());


    }

    public static class IteratorThread extends Thread {
        List<Tranzactie> tranzactieList;
        Condition after5;
        Condition hasPrinted;
        Lock lock;
        AtomicBoolean isFinished;
        AtomicInteger oldSize;

        public IteratorThread(List<Tranzactie> tranzactieList, Lock lock, Condition after5, AtomicBoolean isFinished, AtomicInteger oldSize, Condition hasPrinted) {
            super();
            this.tranzactieList = tranzactieList;
            this.lock = lock;
            this.after5 = after5;
            this.isFinished = isFinished;
            this.oldSize = oldSize;
            this.hasPrinted = hasPrinted;

        }


        @Override
        public void run() {
            while (!isFinished.get()) {

                lock.lock();
                while (tranzactieList.size() % 5 != 0 || tranzactieList.size() == 0 || oldSize.get() == tranzactieList.size()) {
                    try {
                        if (!isFinished.get()) {
                            after5.await();
                        } else
                            break;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (tranzactieList.size() % 5 == 0) {
                    oldSize.set(tranzactieList.size());
                    System.out.println("Tranzactii:");
                    for (Tranzactie t : tranzactieList) {
                        System.out.println(t);
                    }
                    hasPrinted.signal();
                }

                lock.unlock();

            }
        }
    }


    public static class FamilyMember extends Thread {
        Cont cont;
        List<Tranzactie> tranzactieList;

        Lock listLock;
        Condition after5;
        AtomicInteger oldSize;

        Condition hasPrinted;

        public FamilyMember(Cont cont, List<Tranzactie> tranzactieList, Lock listLock, Condition after5, AtomicInteger oldSize, Condition hasPrinted) {
            super();
            this.cont = cont;
            this.listLock = listLock;
            this.tranzactieList = tranzactieList;
            this.after5 = after5;
            this.oldSize = oldSize;
            this.hasPrinted = hasPrinted;
        }


        @Override
        public void run() {
            int numberTranzactions = 20;
            Random r = new Random(System.currentTimeMillis());
            for (int i = 0; i < numberTranzactions; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                long index = Thread.currentThread().getId();
                TipValuta tipValuta;
                TipTranzactie tipTranzactie;
                if (r.nextDouble() < 0.5) {
                    tipValuta = TipValuta.RON;
                } else {
                    tipValuta = TipValuta.EUR;
                }
                if (r.nextDouble() < 0.5) {
                    tipTranzactie = TipTranzactie.RETRAGERE;
                } else {
                    tipTranzactie = TipTranzactie.DEPUNERE;
                }
                int suma = r.nextInt(10) + 1;
                boolean isDone;
                if (tipValuta == TipValuta.RON) {
                    if (tipTranzactie == TipTranzactie.DEPUNERE) {
                        cont.depuneLei(suma);
                        isDone = true;
                    } else {
                        isDone = cont.retrageLei(suma);
                    }
                } else {
                    if (tipTranzactie == TipTranzactie.DEPUNERE) {
                        cont.depuneEur(suma);
                        isDone = true;
                    } else {
                        isDone = cont.retrageEur(suma);
                    }
                }
                int soldLei = cont.getSoldLei();
                int soldEur = cont.getSoldEur();
                if (isDone) {
                    Tranzactie tranzactie = new Tranzactie(index, tipValuta, tipTranzactie, suma, soldLei, soldEur);
                    // lock list
                    try {
                        listLock.lock();
                        while (tranzactieList.size() == oldSize.get() + 5) {
                            try {
                                hasPrinted.await();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        tranzactieList.add(tranzactie);
                        if (tranzactieList.size() % 5 == 0) {
                            after5.signal();
                        }

                    } finally {
                        listLock.unlock();
                    }
                } else {
                    System.out.println("Thread: " + Thread.currentThread().getId() + " failed");
                }

            }
            /// bariera
//            isFinished = true;
        }
    }
}
