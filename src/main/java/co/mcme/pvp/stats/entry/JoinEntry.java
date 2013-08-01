package co.mcme.pvp.stats.entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JoinEntry {

    private String date;
    private String player = null;
    private String map = null;
    private String gt = null;
    private String type = "Join";
    private boolean win;

    public JoinEntry() {
    }

    public JoinEntry(String player, String map, String gt, boolean win) {

        setInfo(player, map, gt, win);

    }

    public void setInfo(String player, String map, String gt, boolean win) {
        setPlayer(player);
        setMap(map);
        setGt(gt);
        setWin(win);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setDate(sdf.format(Calendar.getInstance().getTime()));
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getPlayer() {
        return player;
    }

    public String getMap() {
        return map;
    }

    public String getGt() {
        return gt;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean getWin() {
        return win;
    }
}
