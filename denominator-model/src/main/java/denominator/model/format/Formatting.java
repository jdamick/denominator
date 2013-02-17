
package denominator.model.format;

import com.google.common.collect.ImmutableList;

import denominator.model.RData;
import denominator.model.ResourceRecordSet;

public class Formatting {
    private final Formatter formatter;

    public Formatting(Formatter formatter) {
        this.formatter = formatter;
    }

    public ImmutableList<String> format(RData rdata) {
        return formatter.getValues(rdata.toString());
    }
    
    public ImmutableList<String> format(ResourceRecordSet<? extends RData> rrset) {
        ImmutableList.Builder<String> values = ImmutableList.<String>builder();
        for (RData rdata : rrset) {
            values.addAll(format(rdata));
        }
        return values.build();
    }
    
    // TODO: format zone
}
