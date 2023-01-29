import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cont {

    int soldLei;
    int soldEur;

    Lock lockLei = new ReentrantLock();
    Lock lockEur = new ReentrantLock();

    public Cont(int soldLei, int soldEur) {
        this.soldLei = soldLei;
        this.soldEur = soldEur;
    }

    public void depuneLei(int lei) {
        lockLei.lock();
        soldLei += lei;
        lockLei.unlock();
    }

    public boolean retrageLei(int lei) {
        lockLei.lock();
        if (soldLei >= lei) {
            soldLei -= lei;
            lockLei.unlock();
            return true;
        }
        else{
            System.out.println("No Lei founds");
            lockLei.unlock();
            return false;
        }

    }

    public void depuneEur(int eur) {
        lockEur.lock();
        soldEur += eur;
        lockEur.unlock();
    }

    public boolean retrageEur(int eur) {
        lockEur.lock();
        if (soldEur >= eur) {
            soldEur -= eur;
            lockEur.unlock();
            return true;
        }
        else{
            System.out.println("No Eur founds");
            lockEur.unlock();
            return false;
        }

    }

    public synchronized int getSoldLei() {

        return soldLei;
    }

    public void setSoldLei(int soldLei) {
        this.soldLei = soldLei;
    }

    public synchronized int getSoldEur() {
        return soldEur;
    }

    public void setSoldEur(int soldEur) {
        this.soldEur = soldEur;
    }
}
