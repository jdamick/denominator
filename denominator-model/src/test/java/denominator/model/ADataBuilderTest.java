package denominator.model;

import java.util.List;

import org.testng.annotations.Test;

import denominator.model.format.Formatting;
import denominator.model.format.MasterFileFormatter;
import static org.testng.Assert.*;
import static denominator.model.ResourceRecordSet.Builder.*;

public class ADataBuilderTest {

    @Test
    public void typeSafeARecordsTest() {
        ResourceRecordSet<AData> rrset = ResourceRecordSet.<AData> builder()
            .name("www")
            .ttl(3600)
            .add(a().address("192.168.254.3"))
            .add(a().address("192.168.254.4"))
            .add(a().address("192.168.254.5")).build();
        
        assertEquals(1, rrset.getKlass());
        assertEquals("www", rrset.getName());
        assertEquals(3600, rrset.getTtl());
        assertEquals(1, rrset.getType());
        
        assertFalse(rrset.isEmpty());
        assertEquals(3, rrset.size());
        
        AData rec = rrset.iterator().next();
        assertEquals("192.168.254.3", rec.toString());
        
        List<String> values = (new Formatting(new MasterFileFormatter())).format(rec);
        assertEquals(1, values.size());
        assertEquals("192.168.254.3", values.get(0));
    }
    
    @Test
    public void typeSafeARecordsShortCutTest() {
        // TODO: is this good or bad? something to consider creating short cuts for records with only a few options..
        ResourceRecordSet.<AData> builder()
            .name("www2")
            .ttl(3000)
            .add(a("192.168.254.3"))
            .add(a("192.168.254.4"))
            .add(a("192.168.254.5")).build();
    }
    
    @Test
    public void typeFreeARecordsTest() { 
        ResourceRecordSet<RData> rrset = ResourceRecordSet.<RData> builder()
            .name("abc")
            .ttl(600)
            .type(1)
            .add(raw("192.168.254.3"))
            .add(raw("192.168.254.4"))
            .add(raw("192.168.254.5")).build();
        
        assertEquals(1, rrset.getKlass());
        assertEquals("abc", rrset.getName());
        assertEquals(600, rrset.getTtl());
        assertEquals(1, rrset.getType());
        
        assertFalse(rrset.isEmpty());
        assertEquals(3, rrset.size());
        assertEquals("192.168.254.3", rrset.iterator().next().toString());
    }
    
    @Test
    public void typeSafeSingleARecordTest() {
        ResourceRecordSet<AData> rrset = ResourceRecordSet.<AData> builder()
                .name("www")
                .ttl(3600)
                .add(a().address("192.168.254.5")).build();
        
        assertEquals(1, rrset.getKlass());
        assertEquals("www", rrset.getName());
        assertEquals(3600, rrset.getTtl());
        assertEquals(1, rrset.getType());
        
        assertFalse(rrset.isEmpty());
        assertEquals(1, rrset.size());
        assertEquals("192.168.254.5", rrset.iterator().next().toString());
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void badIpTest() {
        ResourceRecordSet.<AData> builder()
                .name("www")
                .ttl(3600)
                .add(a().address("2620:0:1cfe:face:b00c::3")).build();
    }
    
    @Test(expectedExceptions=IllegalStateException.class)
    public void noIpTest() {
        ResourceRecordSet.<AData> builder()
                .name("www")
                .ttl(3600)
                .add(a()).build();
    }
}
