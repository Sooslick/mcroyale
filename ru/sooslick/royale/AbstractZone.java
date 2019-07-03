package ru.sooslick.royale;

import org.bukkit.Location;
import org.bukkit.World;

public abstract class AbstractZone {

    private static int period;  //zone tick period
    private static World w;     //zone world
    private double xc;          //zone center x
    private double zc;          //zone center z
    private double size;        //zone size
    private double nxb;         //zone negative x bound
    private double pxb;         //zone positive x bound
    private double nzb;         //zone negative z bound
    private double pzb;         //zone positive z bound

    public AbstractZone() {
        setZone(0, 0, 100);
    }

    public AbstractZone(double xc, double zc, double size) {
        setZone(xc, zc, size);
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getSize() {
        return size;
    }

    public void setCenter(double xc, double zc) {
        this.xc = xc;
        this.zc = zc;
    }

    public Location getCenter() {
        return new Location(w, xc, 0, zc);
    }

    public void setZone(double xc, double zc, double size) {
        setCenter(xc, zc);
        setSize(size);
        recalculateBounds();
    }

    public double distanceToBorder(double x, double z) {
        return distanceToCenter(x, z) - size/2;
    }

    public double distanceToCenter(double x, double z) {
        return Math.sqrt(Math.pow(x - xc, 2) + Math.pow(z - zc, 2));
    }

    public boolean isInside(double x, double z) {
        return ((x > nxb) && (x < pxb) && (z > nzb) && (z < pzb));
    }

    public boolean secondTriggered(int checkedSecond, int ticksRemain){
        int ticks = checkedSecond * 20;
        return ((ticksRemain < ticks) && (ticksRemain + period >= ticks));
    }

    private void recalculateBounds() {
        double halfsize = size / 2;
        nxb = xc - halfsize;
        pxb = xc + halfsize;
        nzb = zc - halfsize;
        pzb = zc + halfsize;
    }

}
