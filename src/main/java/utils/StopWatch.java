package utils;

/**
 * Copied from PedroEngine and Learning repo to remove dependencies
 */
public class StopWatch {
    private double time;

    /**
     * Instantiates a new Stop watch.
     */
    public StopWatch() {
        this.time = System.nanoTime();
    }

    /**
     * Gets eleapsed time in seconds
     *
     * @return the eleapsed time
     */
    public double getEleapsedTime() {
        return (System.nanoTime() - time) * 1E-9;
    }

    /**
     * Reset time.
     */
    public void resetTime() {
        this.time = System.nanoTime();
    }
}
