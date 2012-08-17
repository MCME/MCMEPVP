package co.mcme.pvp.games;

public enum GameType {

    /**
     * Represents Team Death Match.
     */
    TDM("TDM", true),
    /**
     * Represents Capture The Wool.
     */
    CTW("CTW", true),
    /**
     * Represents Capture the Ring.
     */
    CTR("CTR", true),
    /**
     * Represents Defend The Keep.
     */
    DTK("DTK", true),
    /**
     * Represents Timed Attack.
     */
    TIA("TIA", true),
    /**
     * Represents Protect the Ring Bearer.
     */
    PRB("PRB", true),
    /**
     * Represents Race Deathmatch.
     */
    RDM("RDM", true),
    /**
     * Represents Protect the Minecart.
     */
    PTM("PTM", true),
    /**
     * Represents Sword Game.
     */
    SWD("SWD", false),
    /**
     * Represents Last Man Standing.
     */
    LMS("LMS", false),
    /**
     * Represents Scavenge.
     */
    SCV("SCV", false),
    /**
     * Represents Knight.
     */
    KNI("KNI", false),
    /**
     * Represents Hunted.
     */
    HNT("HNT", false),
    /**
     * Represents Ogre.
     */
    OGR("OGR", false),
    /**
     * Represents Forage N Forge.
     */
    FNF("FNF", false);
    public String g;
    public boolean isteam;

    GameType(String type, boolean team) {
        g = type;
        isteam = team;
    }
}
