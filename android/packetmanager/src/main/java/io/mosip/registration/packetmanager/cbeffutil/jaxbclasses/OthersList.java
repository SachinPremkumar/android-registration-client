package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import java.util.ArrayList;
import java.util.List;

@Root(name = "others")
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
public class OthersList {

    @ElementList(name = "entry", required = false, inline = true)
    private List<Entry> entries = new ArrayList<>();

    /** Required by JAXB */
    public OthersList() {}

    public OthersList(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() { return entries; }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    // Convenience method
    public void addEntry(Entry entry) {
        this.entries.add(entry);
    }
}
