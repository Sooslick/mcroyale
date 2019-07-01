package ru.sooslick.royale;

public abstract class AbstractZone implements Ticker {

    private static int freq;    //zone tick frequency
    private double xc;          //zone center x
    private double zc;          //zone center z
    private double size;        //zone size

    public double distanceToZone(double x, double y) {
        //todo default impl
    }

    public boolean isInside(double x, double y) {
        //todo default impl
    }

    public boolean triggerSecond(int checkedSecond, int ticksRemain){
        int ticks = checkedSecond*= 20;
        return ((ticksRemain < ticks)&&(ticksRemain + freq >= ticks));
    }

}
