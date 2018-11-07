package ru.sooslick.royale;

import org.bukkit.entity.Player;
import org.bukkit.map.*;

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
        MapCursorCollection mcc = new MapCursorCollection();
        int px = (int) (64+(p.getLocation().getBlockX() - mv.getCenterX()) / sizemod);
        int pz = (int) (64+(p.getLocation().getBlockZ() - mv.getCenterZ()) / sizemod);
        mcc.addCursor(px,pz,(byte)0, (byte)6, true);
        mc.setCursors(mcc);
    }
}
