
package denominator.model;

public class SOAData extends RData {

    public SOAData(String value) {
        super(value);
    }

    @Override
    public int type() {
        return 6;
    }

    public static class Builder extends RData.Builder<RData> {
        private long serial;
        private long refresh;
        private long expire;
        private long ttl;
        
        @Override
        public RData build() {
            return null;
        }
    }
    
}
