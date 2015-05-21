package denominator.ultradns;

import dagger.Lazy;
import denominator.Provider;
import denominator.common.Filter;
import denominator.model.ResourceRecordSet;
import denominator.profile.GeoResourceRecordSetApi;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import static denominator.common.Preconditions.checkNotNull;

final class UltraDNSGeoResourceRecordSetApi implements GeoResourceRecordSetApi {

  private static final Filter<ResourceRecordSet<?>> IS_GEO = new Filter<ResourceRecordSet<?>>() {
    @Override
    public boolean apply(ResourceRecordSet<?> in) {
      return in != null && in.geo() != null;
    }
  };
  private static final int DEFAULT_TTL = 300;

  private final Collection<String> supportedTypes;
  private final Lazy<Map<String, Collection<String>>> regions;
  private final UltraDNS api;
  private final String zoneName;

  UltraDNSGeoResourceRecordSetApi(Collection<String> supportedTypes,
                                  Lazy<Map<String, Collection<String>>> regions,
                                  UltraDNS api,
                                  String zoneName) {
    this.supportedTypes = supportedTypes;
    this.regions = regions;
    this.api = api;
    this.zoneName = zoneName;
  }

  static final class Factory implements GeoResourceRecordSetApi.Factory {

    private final Collection<String> supportedTypes;
    private final Lazy<Map<String, Collection<String>>> regions;
    private final UltraDNS api;
    //private final GroupGeoRecordByNameTypeIterator.Factory iteratorFactory;

    @Inject
    Factory(Provider provider, @Named("geo") Lazy<Map<String, Collection<String>>> regions,
            UltraDNS api/*,
            GroupGeoRecordByNameTypeIterator.Factory iteratorFactory*/) {
      this.supportedTypes = provider.profileToRecordTypes().get("geo");
      this.regions = regions;
      this.api = api;
      //this.iteratorFactory = iteratorFactory;
    }

    @Override
    public GeoResourceRecordSetApi create(String name) {
      checkNotNull(name, "name was null");
      // Eager fetch of regions to determine if directional records are supported or not.
//      try {
        regions.get();
//      } catch (UltraDNSException e) {
//        if (e.code() == UltraDNSException.DIRECTIONAL_NOT_ENABLED) {
//          return null;
//        }
//        throw e;
//      }
      return new UltraDNSGeoResourceRecordSetApi(supportedTypes, regions, api,/* iteratorFactory,*/
              name);
    }
  }

  @Override
  public Map<String, Collection<String>> supportedRegions() {
    return regions.get();
  }

  @Override
  public void put(ResourceRecordSet<?> rrset) {

  }

  @Override
  public void deleteByNameTypeAndQualifier(String name, String type, String qualifier) {

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
  public Iterator<ResourceRecordSet<?>> iterateByNameAndType(String name, String type) {
    return null;
  }

  @Override
  public ResourceRecordSet<?> getByNameTypeAndQualifier(String name, String type, String qualifier) {
    return null;
  }
}
