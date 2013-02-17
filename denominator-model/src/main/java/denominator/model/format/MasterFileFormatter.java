
package denominator.model.format;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public class MasterFileFormatter implements Formatter {
    private static final Splitter SPLITTER = Splitter.on(' ').omitEmptyStrings().trimResults();
    
    @Override
    public ImmutableList<String> getValues(String value) {
        return ImmutableList.<String>builder().addAll(splitter().split(value)).build();
    }
    
    
    protected Splitter splitter() {
        return SPLITTER;
    }
}
