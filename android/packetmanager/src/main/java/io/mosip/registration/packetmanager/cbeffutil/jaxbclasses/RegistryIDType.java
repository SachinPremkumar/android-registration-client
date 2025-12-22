package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name = "RegistryIDType")
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
@Order(elements = {
        "Organization",
        "Type"
})
public class RegistryIDType {

	@Element(name = "Organization", required = false)
	private String organization;

	@Element(name = "Type", required = false)
	private String type;

	/** Required by JAXB */
	public RegistryIDType() {}

	/** Optional custom constructor */
	public RegistryIDType(String organization, String type) {
		this.organization = organization;
		this.type = type;
	}

	// ---------------------------
	// Getters & Setters
	// ---------------------------

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String value) {
		this.organization = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String value) {
		this.type = value;
	}
}
