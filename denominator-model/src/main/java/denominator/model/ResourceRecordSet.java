
package denominator.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 */
public class ResourceRecordSet<R extends RData> extends ForwardingSet<R> {
    private final ImmutableSet<R> records;
    private final int klass;
    private final String name;
    private final long ttl;
    private final int type;
    
    
    protected ResourceRecordSet(ImmutableSet<R> records, String name, int type, long ttl) {
        this(records, name, type, 1 /* IN */, ttl);
    }
    
    protected ResourceRecordSet(ImmutableSet<R> records, String name, int type, int klass, long ttl) {
        this.klass = klass;
        this.records = records;
        this.ttl = ttl;
        this.name = name;
        this.type = type;
    }
    
    /**
     * This returns the Record Set CLASS.
     * For example: {@code IN 1 'the Internet' }
     * @return the CLASS 
     */
    public int getKlass() {
        return klass;
    }
    
    public String getName() {
        return name;
    }
    
    public long getTtl() {
        return ttl;
    }

    public int getType() {
        return type;
    }

    public static <T extends RData> Builder<T> builder() {
        return new Builder<T>();
    }
    
    @Override
    protected Set<R> delegate() {
        return this.records;
    }
    
    
    
    public static class Builder<T extends RData> {
        private Set<T> rdatas = Sets.newLinkedHashSet();
        private String name;
        private long ttl;
        private Optional<Integer> type;
        
        
        public Builder<T> name(String name) {
            checkArgument(!Strings.isNullOrEmpty(name), "Name must specify a domain name.");
            this.name = name;
            return this;
        }
        
        public Builder<T> ttl(long ttl) {
            checkArgument(ttl >= 0 && ttl <= 0x7FFFFFFFL, // Per RFC 2181 
                    "Invalid ttl value: %d", ttl);
            this.ttl = ttl;
            return this;
        }
        
        public Builder<T> type(int type) {
            checkArgument(type >= 0 && type <= 0xFFFF, "Invalid type value: %d", ttl);
            this.type = Optional.of(type);
            return this;
        }
        
        public <RB extends RData> Builder<T> add(RData.Builder<T> rdataBuilder) {
            add(rdataBuilder.build());
            return this;
        }
        
        public Builder<T> add(T rdata) {
            rdatas.add(rdata);
            return this;
        }
        
        public ResourceRecordSet<T> build() {
            // TODO: should we infer the type from the rdata type, probably.
            if (!type.isPresent() && !rdatas.isEmpty()) {
                Optional<T> rdata = Iterables.tryFind(rdatas, new Predicate<T>() {
                    @Override
                    public boolean apply(T input) {
                        return (input != null && input.type() != 0);
                    }
                });
                if (rdata.isPresent()) {
                    type(rdata.get().type());
                } else {
                    throw new IllegalStateException("Must specify the type for an record set");
                }
            }
            return new ResourceRecordSet<T>(ImmutableSet.copyOf(rdatas), name, type.get(), ttl);
        }
        
        // RData Types
        //////////////
        
        public static RData raw(String value) {
            return RawRData.builder().value(value).build();
        }
        
        public static AData.Builder a() {
            return AData.builder();
        }
        
        // TODO: is this good or bad? Leaving it protected to consider.
        protected static AData.Builder a(String address) {
            return AData.builder().address(address);
        }
        
        public static AAAAData.Builder aaaa() {
            return AAAAData.builder();
        }
        
        public static CnameData.Builder cname() {
            return CnameData.builder();
        }
        
    }
}
