package denominator.ultradns;

import denominator.model.Zone;
import java.util.Iterator;

public final class UltraDNSZoneApi implements denominator.ZoneApi {
  private final UltraDNS api;

  UltraDNSZoneApi(UltraDNS api) {
    this.api = api;
  }

  @Override
  public Iterator<Zone> iterator() {
    return null;
  }

  @Override
  public Iterator<Zone> iterateByName(String name) {
    return null;
  }

  @Override
  public String put(Zone zone) {
    return null;
  }

  @Override
  public void delete(String id) {

  }
}
