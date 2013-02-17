
package denominator.model.format;

import com.google.common.collect.ImmutableList;

public interface Formatter {
    ImmutableList<String> getValues(String rawValue);
}
