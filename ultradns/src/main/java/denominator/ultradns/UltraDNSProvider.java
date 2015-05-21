package denominator.ultradns;

import com.google.common.base.Charsets;
import com.google.gson.TypeAdapter;
import dagger.Provides;
import denominator.BasicProvider;
import denominator.CheckConnection;
import denominator.DNSApiManager;
import denominator.QualifiedResourceRecordSetApi;
import denominator.ResourceRecordSetApi;
import denominator.ZoneApi;
import denominator.config.ConcatBasicAndQualifiedResourceRecordSets;
import denominator.config.NothingToClose;
import denominator.config.WeightedUnsupported;
import denominator.profile.GeoResourceRecordSetApi;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.RequestTemplate;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.inject.Singleton;

public class UltraDNSProvider extends BasicProvider {

  private final String url;

  public UltraDNSProvider() {
    this(null);
  }

  /**
   * @param url if empty or null use default
   */
  public UltraDNSProvider(String url) {
    this.url =
        url == null || url.isEmpty() ? "https://restapi.ultradns.com/v1" : url;
  }

  @Override
  public String url() {
    return url;
  }

  /**
   * harvested from the {@code RESOURCE RECORD TYPE CODES} section of the SOAP user guide, dated
   * 2012-11-04.
   */
  @Override
  public Set<String> basicRecordTypes() {
    Set<String> types = new LinkedHashSet<String>();
    types.addAll(
        Arrays.asList("A", "AAAA", "CNAME", "HINFO", "MX", "NAPTR", "NS", "PTR", "RP", "SOA", "SPF",
                      "SRV", "TXT"));
    return types;
  }

  /**
   * directional pools in ultra have types {@code IPV4} and {@code IPV6} which accept both CNAME and
   * address types.
   */
  @Override
  public Map<String, Collection<String>> profileToRecordTypes() {
    Map<String, Collection<String>>
        profileToRecordTypes =
        new LinkedHashMap<String, Collection<String>>();
    profileToRecordTypes.put("geo",
                             Arrays
                                 .asList("A", "AAAA", "CNAME", "HINFO", "MX", "NAPTR", "PTR", "RP",
                                         "SRV", "TXT"));
    profileToRecordTypes.put("roundRobin",
                             Arrays.asList("A", "AAAA", "HINFO", "MX", "NAPTR", "NS", "PTR", "RP",
                                           "SPF", "SRV", "TXT"));
    return profileToRecordTypes;
  }

  @Override
  public Map<String, Collection<String>> credentialTypeToParameterNames() {
    Map<String, Collection<String>> options = new LinkedHashMap<String, Collection<String>>();
    options.put("password", Arrays.asList("username", "password"));
    return options;
  }

  @dagger.Module(injects = DNSApiManager.class, complete = false, includes = {NothingToClose.class,
                                                                              UltraDNSGeoSupport.class,
                                                                              WeightedUnsupported.class,
                                                                              ConcatBasicAndQualifiedResourceRecordSets.class,
                                                                              FeignModule.class})
  public static final class Module {

    @Provides
    CheckConnection checkConnection(NetworkStatusReadable checkConnection) {
      return checkConnection;
    }

    @Provides
    @Singleton
    GeoResourceRecordSetApi.Factory provideGeoResourceRecordSetApiFactory(
        UltraDNSGeoResourceRecordSetApi.Factory in) {
      return in;
    }

    @Provides
    @Singleton
    ZoneApi provideZoneApi(UltraDNS api) {
      return new UltraDNSZoneApi(api);
    }


//    @Provides
//    @Named("accountID")
//    String account(InvalidatableAccountIdSupplier accountId) {
//      return accountId.get();
//    }


    @Provides
    @Singleton
    ResourceRecordSetApi.Factory provideResourceRecordSetApiFactory(UltraDNSResourceRecordSetApi.Factory api) {
        return api;
    }

    @Provides(type = Provides.Type.SET)
    QualifiedResourceRecordSetApi.Factory factoryToProfiles(GeoResourceRecordSetApi.Factory in) {
      return in;
    }

  }

  @dagger.Module(injects = UltraDNSResourceRecordSetApi.Factory.class,
      complete = false, // doesn't bind Provider used by UltraDNSTarget
library = true
  )
  public static final class FeignModule {

    @Provides
    @Singleton
    OAuth ultraDNSOAuth(Feign feign, OAuthTarget target) {
      return feign.newInstance(target);
    }

    @Provides
    @Singleton
    UltraDNS ultraDNS(Feign feign, UltraDNSTarget target) {
      return feign.newInstance(target);
    }

    @Provides
    Logger logger() {
      java.util.logging.Logger.getLogger(Logger.class.getName()).setLevel(Level.FINE);
      return new Logger() {

        @Override
        protected void log(String configKey, String format, Object... args) {
          System.out.println(String.format(methodTag(configKey) + format, args));
        }
      };
      //return new Logger.NoOpLogger();
    }

    @Provides
    Logger.Level logLevel() {
      //return Logger.Level.NONE;
      return Logger.Level.FULL;
    }


    @Provides
    @Singleton
    Feign feign(Logger logger, Logger.Level logLevel) {

      /**
       * {@link UltraDNS#updateDirectionalPoolRecord(DirectionalRecord, DirectionalGroup)} and {@link
       * UltraDNS#addDirectionalPoolRecord(DirectionalRecord, DirectionalGroup, String)} can take up
       * to 10 minutes to complete.
       */
      Request.Options options = new Request.Options(10 * 1000, 10 * 60 * 1000);
      Decoder decoder = decoder();
      return Feign.builder()
          .logger(logger())
          .logLevel(logLevel)
          .options(options)
          .encoder(encoder())
          .decoder(decoder)
          //.errorDecoder(new UltraDNSErrorDecoder(decoder))
          .build();
    }

    static Encoder encoder() {
      return new Encoder() {
        GsonEncoder gsonEncoder = new GsonEncoder();
        @Override
        public void encode(Object object, Type bodyType, RequestTemplate template)
            throws EncodeException {
          if (template.headers().get("Content-Type").equals("application/x-www-form-urlencoded") &&
              bodyType == Map.class) {

            Map<String, Object> form = (Map<String, Object>) object;
            for (Map.Entry<String, Object> entry : form.entrySet()) {
              try {
                entry.setValue(URLEncoder.encode(entry.getValue().toString(),
                    Charsets.UTF_8.name()));
              } catch (UnsupportedEncodingException e) {
                throw new EncodeException("unable to encode: " + entry.getKey(), e);
              }
            }
          } else {
            gsonEncoder.encode(object, bodyType, template);
          }
        }
      };
    }

    static Decoder decoder() {
        return new GsonDecoder(Arrays.<TypeAdapter<?>>asList(
            new UltraDNS.NetworkStatusAdapter()));
/*
      return SAXDecoder.builder()
          .registerContentHandler(NetworkStatusHandler.class)
          .registerContentHandler(IDHandler.class)
          .registerContentHandler(ZoneNamesHandler.class)
          .registerContentHandler(RecordListHandler.class)
          .registerContentHandler(DirectionalRecordListHandler.class)
          .registerContentHandler(DirectionalPoolListHandler.class)
          .registerContentHandler(RRPoolListHandler.class)
          .registerContentHandler(RegionTableHandler.class)
          .registerContentHandler(DirectionalGroupHandler.class)
          .registerContentHandler(UltraDNSError.class)
          .build();
          */
    }
  }
}
