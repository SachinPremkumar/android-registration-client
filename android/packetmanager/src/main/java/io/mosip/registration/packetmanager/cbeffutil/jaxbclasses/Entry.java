package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Root;

@Root(name = "entry")
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
public class Entry {

    @Text(required = false)
    private String value;

    @Attribute(name = "key", required = true)
    private String key;

    /** Required by JAXB */
    public Entry() {}

    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}
