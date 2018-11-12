package ru.sooslick.royale;

import org.bukkit.entity.Player;
import org.bukkit.map.*;

import java.security.Timestamp;

/**
 * Created by sooslick on 15.10.2018.
 */
public class Renderer extends MapRenderer {

    private int size;
    private int sizemod;

    public void init(int s, int sm) {
        size = s;
        sizemod = sm;
    }

    @Override
    public void render(MapView mv, MapCanvas mc, Player p) {
        int begin = (int) (64 - size / sizemod);
        int end = (int) (64 + size / sizemod);
        for (int i = begin; i < end; i++){
            mc.setPixel(begin,i, MapPalette.BLUE);
            mc.setPixel(end,i, MapPalette.BLUE);
            mc.setPixel(i,begin, MapPalette.BLUE);
            mc.setPixel(i,end, MapPalette.BLUE);
        }
        if (System.currentTimeMillis() % 1000 < 500) {
            int px = (int) ((p.getLocation().getBlockX() - mv.getCenterX()) / (sizemod / 2) + 64);
            int pz = (int) ((p.getLocation().getBlockZ() - mv.getCenterZ()) / (sizemod / 2) + 64);
            mc.setPixel(px,pz,MapPalette.RED);
            mc.setPixel(px-1,pz-1,MapPalette.RED);
            mc.setPixel(px-1,pz+1,MapPalette.RED);
            mc.setPixel(px+1,pz-1,MapPalette.RED);
            mc.setPixel(px+1,pz+1,MapPalette.RED);
        }
    }
}
