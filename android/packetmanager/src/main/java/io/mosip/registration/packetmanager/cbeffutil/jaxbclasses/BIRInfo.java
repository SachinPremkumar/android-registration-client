package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import java.util.Date;

@Root(name = "BIRInfo")
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
@Order(elements = {
        "Creator",
        "Index",
        "Payload",
        "Integrity",
        "CreationDate",
        "NotValidBefore",
        "NotValidAfter"
})
public class BIRInfo {

	@Element(name = "Creator", required = false)
	private String creator;

	@Element(name = "Index", required = false)
	private String index;

	@Element(name = "Payload", required = false)
	private byte[] payload;

	@Element(name = "Integrity", required = true)
	private boolean integrity;

	@Element(name = "CreationDate", required = false)
	private Date creationDate;

	@Element(name = "NotValidBefore", required = false)
	private Date notValidBefore;
	
	@Element(name = "NotValidAfter", required = false)
	private Date notValidAfter;

	// Getters / Setters
	public String getCreator() { return creator; }
	public void setCreator(String creator) { this.creator = creator; }

	public String getIndex() { return index; }
	public void setIndex(String index) { this.index = index; }

	public byte[] getPayload() { return payload; }
	public void setPayload(byte[] payload) { this.payload = payload; }

	public boolean isIntegrity() { return integrity; }
	public void setIntegrity(boolean integrity) { this.integrity = integrity; }

	public Date getCreationDate() { return creationDate; }
	public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

	public Date getNotValidBefore() { return notValidBefore; }
	public void setNotValidBefore(Date notValidBefore) { this.notValidBefore = notValidBefore; }

	public Date getNotValidAfter() { return notValidAfter; }
	public void setNotValidAfter(Date notValidAfter) { this.notValidAfter = notValidAfter; }

	public static class BIRInfoBuilder {
		private boolean integrity;
		private String creator;
		private String index;
		private byte[] payload;
		private Date creationDate;
		private Date notValidBefore;
		private Date notValidAfter;

		public BIRInfoBuilder withIntegrity(boolean integrity) {
			this.integrity = integrity;
			return this;
		}

		public BIRInfoBuilder withCreator(String creator) {
			this.creator = creator;
			return this;
		}

		public BIRInfoBuilder withIndex(String index) {
			this.index = index;
			return this;
		}

		public BIRInfoBuilder withPayload(byte[] payload) {
			this.payload = payload;
			return this;
		}

		public BIRInfoBuilder withCreationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public BIRInfoBuilder withNotValidBefore(Date notValidBefore) {
			this.notValidBefore = notValidBefore;
			return this;
		}

		public BIRInfoBuilder withNotValidAfter(Date notValidAfter) {
			this.notValidAfter = notValidAfter;
			return this;
		}

		public BIRInfo build() {
			BIRInfo birInfo = new BIRInfo();
			birInfo.setIntegrity(integrity);
			birInfo.setCreator(creator);
			birInfo.setIndex(index);
			birInfo.setPayload(payload);
			birInfo.setCreationDate(creationDate);
			birInfo.setNotValidBefore(notValidBefore);
			birInfo.setNotValidAfter(notValidAfter);
			return birInfo;
		}
	}
}
