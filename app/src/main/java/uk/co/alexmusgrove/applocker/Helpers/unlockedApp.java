package uk.co.alexmusgrove.applocker.Helpers;

public class unlockedApp {
    private String packageName;
    private long unlockedAt;
    private static final int DURATION = 180000; //180 Seconds, 3 Minutes

    public unlockedApp(String packageName) {
        this.packageName = packageName;
        this.unlockedAt = System.currentTimeMillis();
    }

    public boolean isUnlocked () {
        return ((unlockedAt + DURATION) > System.currentTimeMillis());
    }

    public String getPackageName() {
        return packageName;
    }
}
