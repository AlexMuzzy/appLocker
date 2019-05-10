package uk.co.alexmusgrove.applocker.Helpers;

public class unlockedApp {
    private String packageName;
    private long unlockedAt;
    private static final int DURATION = 30000; //30 Seconds

    public unlockedApp(String packageName) {
        this.packageName = packageName;
        this.unlockedAt = System.currentTimeMillis();
    }

    public boolean isUnlocked () {
        return ((unlockedAt + DURATION) > System.currentTimeMillis());
    }

}
