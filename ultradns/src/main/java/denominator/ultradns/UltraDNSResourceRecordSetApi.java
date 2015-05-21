package denominator.ultradns;

import denominator.ResourceRecordSetApi;
import denominator.model.ResourceRecordSet;
import java.util.Iterator;
import javax.inject.Inject;

import static denominator.common.Preconditions.checkNotNull;

final class UltraDNSResourceRecordSetApi implements denominator.ResourceRecordSetApi {
  private final UltraDNS api;
  private final String zone;

  static final class Factory implements denominator.ResourceRecordSetApi.Factory {

    private final UltraDNS api;

    @Inject
    Factory(UltraDNS api) {
      this.api = api;
    }

    @Override
    public ResourceRecordSetApi create(String name) {
      checkNotNull(name, "name was null");
      return new UltraDNSResourceRecordSetApi(api, name);
    }
  }

  UltraDNSResourceRecordSetApi(UltraDNS api, String zone) {
    this.api = api;
    this.zone = zone;
  }

  @Override
  public Iterator<ResourceRecordSet<?>> iterator() {
    return null;
  }

  @Override
  public Iterator<ResourceRecordSet<?>> iterateByName(String name) {
    return null;
  }

  @Override
  public ResourceRecordSet<?> getByNameAndType(String name, String type) {
    return null;
  }

  @Override
  public void put(ResourceRecordSet<?> rrset) {

  }

  @Override
  public void deleteByNameAndType(String name, String type) {

  }
}
