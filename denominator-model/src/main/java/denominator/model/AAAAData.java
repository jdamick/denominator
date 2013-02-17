
package denominator.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.net.Inet6Address;
import java.net.InetAddress;

import com.google.common.net.InetAddresses;


public class AAAAData extends RData {

    protected AAAAData(String value) {
        super(value);
    }

    @Override
    public int type() {
        return 28;
    }

    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder extends RData.Builder<AAAAData> {
        private String address;
        
        public Builder address(String address) {
            InetAddress addr = InetAddresses.forString(address);
            checkArgument(addr instanceof Inet6Address, "Must be an IPV6 Address: %s", address);
            this.address = address;
            return this;
        }
        
        @Override
        public AAAAData build() {
            checkState(address != null, "Must set address for an AAAA record");
            return new AAAAData(address);
        }
    }
}
