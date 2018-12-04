package ru.sooslick.royale;

import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.map.*;

/**
 * Created by sooslick on 15.10.2018.
 */
public class Renderer extends MapRenderer {

    private int size;
    private int sizemod;
    private int px_old;
    private int pz_old;
    private WorldBorder wb;

    //todo: dynamic teammates array

    public void init(int s, int sm, WorldBorder w) {
        size = s;
        sizemod = sm;
        px_old = 0;
        pz_old = 0;
        wb = w;
    }

    @Override
    public void render(MapView mv, MapCanvas mc, Player p) {
        //new zone
        int begin = (int) (64 - size / sizemod);
        int end = (int) (64 + size / sizemod);
        for (int i = begin; i < end; i++) {
            mc.setPixel(begin,i, MapPalette.BLUE);
            mc.setPixel(end,i, MapPalette.BLUE);
            mc.setPixel(i,begin, MapPalette.BLUE);
            mc.setPixel(i,end, MapPalette.BLUE);
        }
        //zone border
        int px = (int) ((wb.getCenter().getBlockX() - mv.getCenterX()) / (sizemod / 2) + 64);
        int pz = (int) ((wb.getCenter().getBlockZ() - mv.getCenterZ()) / (sizemod / 2) + 64);
        begin = (int) (px - wb.getSize() / sizemod);
        end = (int) (pz - wb.getSize() / sizemod);
        for (int i = begin; i <= end; i++) {
            mc.setPixel(begin,i, MapPalette.RED);
            mc.setPixel(end,i, MapPalette.RED);
            mc.setPixel(i,begin, MapPalette.RED);
            mc.setPixel(i,end, MapPalette.RED);
        }
        //player location
        px = (int) ((p.getLocation().getBlockX() - mv.getCenterX()) / (sizemod / 2) + 64);
        pz = (int) ((p.getLocation().getBlockZ() - mv.getCenterZ()) / (sizemod / 2) + 64);
        if (System.currentTimeMillis() % 1000 < 500) {
            mc.setPixel(px,pz,MapPalette.BROWN);
            mc.setPixel(px-1,pz-1,MapPalette.BROWN);
            mc.setPixel(px-1,pz+1,MapPalette.BROWN);
            mc.setPixel(px+1,pz-1,MapPalette.BROWN);
            mc.setPixel(px+1,pz+1,MapPalette.BROWN);
        }
        else {
            mc.setPixel(px,pz,mc.getBasePixel(px,pz));
            mc.setPixel(px-1,pz-1,mc.getBasePixel(px-1,pz-1));
            mc.setPixel(px-1,pz+1,mc.getBasePixel(px-1,pz+1));
            mc.setPixel(px+1,pz-1,mc.getBasePixel(px+1,pz-1));
            mc.setPixel(px+1,pz+1,mc.getBasePixel(px+1,pz+1));
        }
        if ((px != px_old)||(pz != pz_old)) {
            mc.setPixel(px_old,pz_old,mc.getBasePixel(px_old,pz_old));
            mc.setPixel(px_old-1,pz_old-1,mc.getBasePixel(px_old-1,pz_old-1));
            mc.setPixel(px_old-1,pz_old+1,mc.getBasePixel(px_old-1,pz_old+1));
            mc.setPixel(px_old+1,pz_old-1,mc.getBasePixel(px_old+1,pz_old-1));
            mc.setPixel(px_old+1,pz_old+1,mc.getBasePixel(px_old+1,pz_old+1));
            px_old = px;
            pz_old = pz;
        }
    }
}
