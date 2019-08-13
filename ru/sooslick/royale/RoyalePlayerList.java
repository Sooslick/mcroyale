package ru.sooslick.royale;

import java.util.ArrayList;
import java.util.List;

public class RoyalePlayerList {

    static List<RoyalePlayer> pls;

    public RoyalePlayerList() {
        pls = new ArrayList<>();
    }

    public void add(RoyalePlayer rp) {
        pls.add(rp);
    }

    public RoyalePlayer getPlayerByName(String name) {
        for (RoyalePlayer rp : pls) {
            if (rp.getName().equals(name)) {
                return rp;
            }
        }
        return null;
    }

}
