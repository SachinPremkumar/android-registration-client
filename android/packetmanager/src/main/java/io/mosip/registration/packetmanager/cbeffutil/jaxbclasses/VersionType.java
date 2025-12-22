package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name = "Version")
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
@Order(elements = {
        "Major",
        "Minor"
})
public class VersionType {

	@Element(name = "Major")
	private int major;

	@Element(name = "Minor")
	private int minor;

	/** Required by JAXB */
	public VersionType() {}

	/** Convenience constructor */
	public VersionType(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	/** Builder constructor */
	public VersionType(VersionTypeBuilder builder) {
		this.major = builder.major;
		this.minor = builder.minor;
	}

	/** Manual getters/setters (to avoid Lombok on Android) */
	public int getMajor() { return major; }
	public void setMajor(int major) { this.major = major; }

	public int getMinor() { return minor; }
	public void setMinor(int minor) { this.minor = minor; }

	// ------------------------------
	// Builder Class
	// ------------------------------

	public static class VersionTypeBuilder {

		private int major;
		private int minor;

		public VersionTypeBuilder withMajor(int major) {
			this.major = major;
			return this;
		}

		public VersionTypeBuilder withMinor(int minor) {
			this.minor = minor;
			return this;
		}

		public VersionType build() {
			return new VersionType(this);
		}
	}
}
