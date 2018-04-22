package utils;

/**
 * Copied from PedroEngine and Learning repo to remove dependencies
 */
public abstract class TokenRecognizer {
    private String[] patterns;

    public String[] getPatterns() {
        return patterns;
    }

    public void setPatterns(String[] patterns) {
        this.patterns = patterns;
    }

    TokenRecognizer(String[] patterns){
        this.patterns = patterns ;
    }

    public abstract String[] tokenize(String s);

    public abstract void init();
}