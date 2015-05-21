package denominator.ultradns;

import dagger.Module;
import dagger.Provides;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.inject.Named;

@Module(injects = UltraDNSGeoResourceRecordSetApi.Factory.class, complete = false)
public class UltraDNSGeoSupport {
  @Provides
  @Named("geo")
  Map<String, Collection<String>> regions(UltraDNS api) {
    return Collections.EMPTY_MAP; //return api.getAvailableRegions();
  }

}
