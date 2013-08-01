package co.mcme.pvp.stats.entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class KillEntry {

    private String date;
    private String victim;
    private String killer;
    private String map;
    private String gt;

    public KillEntry() {
    }

    public KillEntry(String victim, String killer, String map, String gt) {

        setInfo(victim, killer, map, gt);

    }

    public void setInfo(String v, String k, String m, String g) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setDate(sdf.format(Calendar.getInstance().getTime()));
        setVictim(v);
        setKiller(k);
        setMap(m);
        setGt(g);
    }

    public void setGt(String a) {
        this.gt = a;
    }

    public void setMap(String b) {
        this.map = b;
    }

    public void setKiller(String c) {
        this.killer = c;
    }

    public void setVictim(String d) {
        this.victim = d;
    }

    public void setDate(String e) {
        this.date = e;
    }

    public String getDate() {
        return date;
    }

    public String getVictim() {
        return victim;
    }

    public String getKiller() {
        return killer;
    }

    public String getMap() {
        return map;
    }

    public String getGt() {
        return gt;
    }
}
