package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name = "SBInfo")
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
@Order(elements = {
        "Format"
})
public class SBInfo {

	@Element(name = "Format", required = false)
	private RegistryIDType format;

	/** Required by JAXB */
	public SBInfo() {}

	/** Builder constructor */
	public SBInfo(SBInfoBuilder builder) {
		this.format = builder.format;
	}

	// Getter
	public RegistryIDType getFormat() {
		return format;
	}

	// ---------------------------
	// Builder Class
	// ---------------------------

	public static class SBInfoBuilder {

		private RegistryIDType format;

		public SBInfoBuilder withFormat(RegistryIDType format) {
			this.format = format;
			return this;
		}

		public SBInfo build() {
			return new SBInfo(this);
		}
	}
}
