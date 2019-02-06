package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.map.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sooslick on 15.10.2018.
 */
public class Renderer extends MapRenderer {

    private int size;
    private int sizemod;
    private HashMap<String, Integer> px_old;
    private HashMap<String, Integer> pz_old;
    private WorldBorder wb;
    private squad sq;

    public void init(int s, int sm, WorldBorder w, squad sq1) {
        size = s;
        sizemod = sm;
        px_old = new HashMap<>();
        pz_old = new HashMap<>();
        wb = w;
        sq = sq1;
        for (String str : sq.getPlayers()) {
            px_old.put(str, 0);
            pz_old.put(str, 0);
        }
    }

    @Override
    public void render(MapView mv, MapCanvas mc, Player plr) {
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
        int beginx = (int) (px - wb.getSize() / sizemod);
        int beginz = (int) (pz - wb.getSize() / sizemod);
        int endx = (int) (px + wb.getSize() / sizemod);
        int endz = (int) (pz + wb.getSize() / sizemod);
        for (int i = beginx; i <= endx; i++) {
            mc.setPixel(i,beginz, MapPalette.RED);
            mc.setPixel(i,endz, MapPalette.RED);
        }
        for (int i = beginz; i <= endz; i++) {
            mc.setPixel(beginx, i, MapPalette.RED);
            mc.setPixel(endx, i, MapPalette.RED);
        }
        for (String s : sq.getAlives()) {
            //player location
            Player p = Bukkit.getPlayer(s);
            int pxo = px_old.get(s);
            int pzo = pz_old.get(s);
            px = (int) ((p.getLocation().getBlockX() - mv.getCenterX()) / (sizemod / 2) + 64);
            pz = (int) ((p.getLocation().getBlockZ() - mv.getCenterZ()) / (sizemod / 2) + 64);
            if (System.currentTimeMillis() % 1000 < 500)
                if (p.equals(plr))
                    drawXColor(mc, px, pz, MapPalette.RED);
                else
                    drawXColor(mc, px, pz, MapPalette.DARK_BROWN);
            else
                drawXBase(mc, px, pz);
            if ((px != pxo) || (pz != pzo)) {
                drawXBase(mc, pxo, pzo);
                px_old.put(s, px);
                pz_old.put(s, pz);
            }
        }
    }

    private void drawXColor(MapCanvas mc, int x, int y, byte cl) {
        mc.setPixel(x,y,cl);
        mc.setPixel(x-1,y-1,cl);
        mc.setPixel(x-1,y+1,cl);
        mc.setPixel(x+1,y-1,cl);
        mc.setPixel(x+1,y+1,cl);
    }

    private void drawXBase(MapCanvas mc, int x, int y) {
        mc.setPixel(x,y,mc.getBasePixel(x,y));
        mc.setPixel(x-1,y-1,mc.getBasePixel(x-1,y-1));
        mc.setPixel(x-1,y+1,mc.getBasePixel(x-1,y+1));
        mc.setPixel(x+1,y-1,mc.getBasePixel(x+1,y-1));
        mc.setPixel(x+1,y+1,mc.getBasePixel(x+1,y+1));
    }
}
