
package denominator.model;


public abstract class RData {
    private final String value;
    
    protected RData(String value) {
        this.value = value;
    }
    
    abstract int type();
    
    public static abstract class Builder<R extends RData> {
        public abstract R build();
    }
    
    public String toString() {
        return value;
    }
}
