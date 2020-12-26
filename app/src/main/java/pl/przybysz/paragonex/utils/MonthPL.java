package pl.przybysz.paragonex.utils;

import androidx.annotation.NonNull;

public enum MonthPL {
    STYCZEN("Styczeń", 1),
    LUTY("Luty", 2),
    MARZEC("Marzec", 3),
    KWIECIEN("Kiecień", 4),
    MAJ("Maj", 5),
    CZERWIEC("Czerwiec", 6),
    LIPIEC("Lipiec", 7),
    SIERPIEN("Sierpien", 8),
    WRZESIEN("Wrzesien", 9),
    PAZDZIERNIK("Pazdziernik", 10),
    LISTOPAD("Listopad", 11),
    GRUDZIEN("Grudzien", 12);

    private String label;
    private int index;

    MonthPL(String label, int index) {
        this.label = label;
        this.index = index;
    }

    @NonNull
    @Override
    public String toString() {
        return this.label;
    }

    public int getIndex(){
        return this.index;
    }

}
