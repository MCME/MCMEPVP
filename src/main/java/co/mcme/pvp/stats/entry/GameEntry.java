package co.mcme.pvp.stats.entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GameEntry {

    private String date;
    private String winner = null;
    private String map = null;
    private String gt = null;
    private String type = "game";

    public GameEntry() {
    }

    public GameEntry(String outcome, String map, String gt) {

        setInfo(outcome, map, gt);

    }

    private void setInfo(String outcome, String map, String gt) {
        setWinner(outcome);
        setMap(map);
        setGt(gt);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setDate(sdf.format(Calendar.getInstance().getTime()));
    }

    private void setDate(String format) {
        this.date = format;
    }

    private void setGt(String gt) {
        this.gt = gt;
    }

    private void setMap(String map) {
        this.map = map;
    }

    private void setWinner(String outcome) {
        this.winner = outcome;
    }

    public String getDate() {
        return date;
    }

    public String getGt() {
        return gt;
    }

    public String getMap() {
        return map;
    }

    public String getWinner() {
        return winner;
    }
}
