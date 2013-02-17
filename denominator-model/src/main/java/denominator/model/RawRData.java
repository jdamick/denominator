
package denominator.model;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Optional;



public class RawRData extends RData {
    private final int type;

    protected RawRData(int type, String value) {
        super(value);
        this.type = type;
    }
    
    @Override
    int type() {
        return type;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder extends RData.Builder<RData> {
        private String value;
        private Optional<Integer> type;
        
        public Builder value(String value) {
            this.value = value;
            return this;
        }
        
        public Builder type(int type) {
            this.type = Optional.of(type);
            return this;
        }
        
        @Override
        public RawRData build() {
            checkState(value != null, "Must have a value set");
            // type is optional in this context, though you can't have a record without a type, 
            // the exact type number may not be known if it's a custom type
            return new RawRData(type.or(0), value);
        }
    }
}
