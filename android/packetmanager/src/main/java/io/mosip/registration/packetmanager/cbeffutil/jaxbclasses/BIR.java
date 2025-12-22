package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import java.util.*;

@Root(name = "BIR", strict = false)
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
@Order(elements = {
        "Version",
        "CBEFFVersion",
        "BIRInfo",
        "BDBInfo",
        "SBInfo",
        "BIR",     // nested BIR list
        "BDB",
        "SB"
        // "others" removed - it's optional and may be empty, so don't enforce in @Order
})
public class BIR {

	@Element(name = "Version", required = false)
	private VersionType version;

	@Element(name = "CBEFFVersion", required = false)
	private VersionType cbeffVersion;

	@Element(name = "BIRInfo", required = true)
	private BIRInfo birInfo;

	@Element(name = "BDBInfo", required = false)
	private BDBInfo bdbInfo;

	@Element(name = "SBInfo", required = false)
	private SBInfo sbInfo;

	@ElementList(name = "BIR", required = false, inline = true)
	private List<BIR> birs = new ArrayList<>();

	@Element(name = "BDB", required = false)
	private byte[] bdb;

	@Element(name = "SB", required = false)
	private byte[] sb;

	@ElementList(name = "others", required = false, inline = true)
	private List<OthersList> others = new ArrayList<>();

	// Getters & Setters
	public VersionType getVersion() { return version; }
	public void setVersion(VersionType value) { this.version = value; }

	public VersionType getCbeffVersion() { return cbeffVersion; }
	public void setCbeffVersion(VersionType value) { this.cbeffVersion = value; }

	public BIRInfo getBirInfo() { return birInfo; }
	public void setBirInfo(BIRInfo value) { this.birInfo = value; }

	public BDBInfo getBdbInfo() { return bdbInfo; }
	public void setBdbInfo(BDBInfo value) { this.bdbInfo = value; }

	public SBInfo getSbInfo() { return sbInfo; }
	public void setSbInfo(SBInfo value) { this.sbInfo = value; }

	public List<BIR> getBirs() { return birs; }
	public void setBirs(List<BIR> birs) { this.birs = birs; }

	public byte[] getBdb() { return bdb; }
	public void setBdb(byte[] bdb) { this.bdb = bdb; }

	public byte[] getSb() { return sb; }
	public void setSb(byte[] sb) { this.sb = sb; }

	public List<OthersList> getOthers() { return others; }
	public void setOthers(List<OthersList> others) { this.others = others; }

	public static class BIRBuilder {
		private byte[] bdb;
		private VersionType version;
		private VersionType cbeffVersion;
		private BIRInfo birInfo;
		private BDBInfo bdbInfo;
		private SBInfo sbInfo;
		private byte[] sb;
		private List<BIR> birs = new ArrayList<>();
		private HashMap<String, String> othersMap = new HashMap<>();

		public BIRBuilder withBdb(byte[] bdb) {
			this.bdb = bdb;
			return this;
		}

		public BIRBuilder withVersion(VersionType version) {
			this.version = version;
			return this;
		}

		public BIRBuilder withCbeffversion(VersionType cbeffVersion) {
			this.cbeffVersion = cbeffVersion;
			return this;
		}

		public BIRBuilder withBirInfo(BIRInfo birInfo) {
			this.birInfo = birInfo;
			return this;
		}

		public BIRBuilder withBdbInfo(BDBInfo bdbInfo) {
			this.bdbInfo = bdbInfo;
			return this;
		}

		public BIRBuilder withSbInfo(SBInfo sbInfo) {
			this.sbInfo = sbInfo;
			return this;
		}

		public BIRBuilder withSb(byte[] sb) {
			this.sb = sb;
			return this;
		}

		public BIRBuilder withBirs(List<BIR> birs) {
			if (birs != null) {
				this.birs = birs;
			}
			return this;
		}

		public BIRBuilder withOthers(String key, String value) {
			if (key != null) {
				this.othersMap.put(key, value != null ? value : "");
			}
			return this;
		}

		public BIRBuilder withOthers(HashMap<String, String> others) {
			if (others != null) {
				this.othersMap.putAll(others);
			}
			return this;
		}

		public BIR build() {
			BIR bir = new BIR();
			bir.setBdb(bdb);
			bir.setVersion(version);
			bir.setCbeffVersion(cbeffVersion);
			bir.setBirInfo(birInfo);
			bir.setBdbInfo(bdbInfo);
			bir.setSbInfo(sbInfo);
			bir.setSb(sb);
			bir.setBirs(birs);

			// Convert Map to List<OthersList>
			if (!othersMap.isEmpty()) {
				OthersList othersList = new OthersList();
				List<Entry> entries = new ArrayList<>();
				for (Map.Entry<String, String> entry : othersMap.entrySet()) {
					Entry e = new Entry(entry.getKey(), entry.getValue());
					entries.add(e);
		}
				othersList.setEntries(entries);
				bir.setOthers(new ArrayList<>(java.util.Collections.singletonList(othersList)));
			}

			return bir;
		}
	}
}
