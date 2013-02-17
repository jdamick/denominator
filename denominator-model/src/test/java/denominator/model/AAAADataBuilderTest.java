package denominator.model;

import static denominator.model.ResourceRecordSet.Builder.aaaa;
import static denominator.model.ResourceRecordSet.Builder.raw;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.List;

import org.testng.annotations.Test;

import denominator.model.format.Formatting;
import denominator.model.format.MasterFileFormatter;

public class AAAADataBuilderTest {

    @Test
    public void typeSafeARecordsTest() {
        ResourceRecordSet<AAAAData> rrset = ResourceRecordSet.<AAAAData> builder()
            .name("www")
            .ttl(3600)
            .add(aaaa().address("2620:0:1cfe:face:b00c::3"))
            .add(aaaa().address("2001:502:4612::1"))
            .add(aaaa().address("2001:502:f3ff::1")).build();
        
        assertEquals(1, rrset.getKlass());
        assertEquals("www", rrset.getName());
        assertEquals(3600, rrset.getTtl());
        assertEquals(28, rrset.getType());
        
        assertFalse(rrset.isEmpty());
        assertEquals(3, rrset.size());
        
        AAAAData rec = rrset.iterator().next();
        assertEquals("2620:0:1cfe:face:b00c::3", rec.toString());
        
        // check formatting
        List<String> values = (new Formatting(new MasterFileFormatter())).format(rec); 
        assertEquals(1, values.size());
        assertEquals("2620:0:1cfe:face:b00c::3", values.get(0));
    }
    
    @Test
    public void typeSafeARecordsShortCutTest() {
        // TODO: is this good or bad? something to consider creating short cuts for records with only a few options..
        ResourceRecordSet.<AAAAData> builder()
            .name("www2")
            .ttl(3000)
            .add(aaaa().address("2620:0:1cfe:face:b00c::3"))
            .add(aaaa().address("2001:502:4612::1"))
            .add(aaaa().address("2001:502:f3ff::1"))
            .build();
    }
    
    @Test
    public void typeFreeARecordsTest() { 
        ResourceRecordSet<RData> rrset = ResourceRecordSet.<RData> builder()
            .name("abc")
            .ttl(600)
            .type(28)
            .add(raw("2620:0:1cfe:face:b00c::3"))
            .add(raw("2001:502:4612::1"))
            .add(raw("2001:502:f3ff::1"))
            .build();
        
        assertEquals(1, rrset.getKlass());
        assertEquals("abc", rrset.getName());
        assertEquals(600, rrset.getTtl());
        assertEquals(28, rrset.getType());
        
        assertFalse(rrset.isEmpty());
        assertEquals(3, rrset.size());
        assertEquals(rrset.iterator().next().toString(), "2620:0:1cfe:face:b00c::3");
    }
    
    @Test
    public void typeSafeSingleARecordTest() {
        ResourceRecordSet<AAAAData> rrset = ResourceRecordSet.<AAAAData> builder()
                .name("www")
                .ttl(3600)
                .add(aaaa().address("2620:0:1cfe:face:b00c::3")).build();
        
        assertEquals(1, rrset.getKlass());
        assertEquals("www", rrset.getName());
        assertEquals(3600, rrset.getTtl());
        assertEquals(28, rrset.getType());
        
        assertFalse(rrset.isEmpty());
        assertEquals(1, rrset.size());
        assertEquals("2620:0:1cfe:face:b00c::3", rrset.iterator().next().toString());
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void badIpTest() {
        ResourceRecordSet.<AAAAData> builder()
                .name("www")
                .ttl(3600)
                .add(aaaa().address("192.168.254.5")).build();
    }
    
    @Test(expectedExceptions=IllegalStateException.class)
    public void noIpTest() {
        ResourceRecordSet.<AAAAData> builder()
                .name("www")
                .ttl(3600)
                .add(aaaa()).build();
    }
}
