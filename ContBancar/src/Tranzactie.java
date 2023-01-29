public class Tranzactie {

    long index;
    TipValuta tipValuta;
    TipTranzactie tipTranzactie;
    int sumaTranzactionata;
    int soldEur;
    int soldLei;



    public Tranzactie(long index, TipValuta tipValuta, TipTranzactie tipTranzactie, int suma, int soldLei, int soldEur) {
        this.index = index;
        this.tipValuta = tipValuta;
        this.tipTranzactie = tipTranzactie;
        this.sumaTranzactionata = suma;
        this.soldEur = soldEur;
        this.soldLei = soldLei;
    }
    @Override
    public String toString() {
        return "Tranzactie{" +
                "index=" + index +
                ", tipValuta=" + tipValuta +
                ", tipTranzactie=" + tipTranzactie +
                ", sumaTranzactionata=" + sumaTranzactionata +
                ", soldEur=" + soldEur +
                ", soldLei=" + soldLei +
                '}';
    }
}
