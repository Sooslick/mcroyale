package ru.sooslick.royale;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class squad {

    public String name;
    public String leader;
    private ArrayList<String> players = new ArrayList<>();
    private ArrayList<String> alives = new ArrayList<>();
    static int MaxMembers = 4;

    public void Reset() {
        players.clear();
        alives.clear();
    }

    public squad(Player p, String s) {
        leader = p.getName();
        name = s;
        Reset();
        AddPlayer(leader);
    }

    public void SetName(String param) {
        name = param;
    }

    public void AddPlayer(String pname) {
        players.add(pname);
    }

    public void JoinGame(){
        alives.clear();
        for (String s: players) alives.add(s);
    }

    public void RevivePlayer(String pname) {
        if (!alives.contains(pname)) alives.add(pname);
    }

    public void KillPlayer(String pname) {
        if (alives.contains(pname)) alives.remove(pname);
    }

    public void KickPlayer(String pname)
    {
        if (!(leader.equals(pname)))
        {
            players.remove(pname);
            alives.remove(pname);
        }
        else
        {
            //tODO msg
        }
    }

    public boolean HasPlayer(String pname) {return  players.contains(pname);}

    public boolean HasAlive(String pname) {return alives.contains(pname);}

    public boolean HaveAlive() {return alives.size()>0;}

    public boolean isFull() {return players.size()>=MaxMembers;}

    public int GetAliveCount(){return alives.size();}

    public int GetPlayersCount(){return players.size();}

    public ArrayList<String> GetAlives(){return alives;}

    public ArrayList<String> GetPlayers(){return players;}
}
