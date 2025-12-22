package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;

@Root(name = "BDBInfo")
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
@Order(elements = {
        "ChallengeResponse",
        "Index",
        "Format",
        "Encryption",
        "CreationDate",
        "NotValidBefore",
        "NotValidAfter",
        "Type",
        "Subtype",
        "Level",
        "Product",
        "CaptureDevice",
        "FeatureExtractionAlgorithm",
        "ComparisonAlgorithm",
        "CompressionAlgorithm",
        "Purpose",
        "Quality"
})
public class BDBInfo {

	@Element(name = "ChallengeResponse", required = false)
	private byte[] challengeResponse;
	
	@Element(name = "Index", required = false)
	private String index;
	
	@Element(name = "Format", required = false)
	private RegistryIDType format;
	
	@Element(name = "Encryption", required = false)
	private Boolean encryption;

	@Element(name = "CreationDate", required = false)
	private Date creationDate;
	
	@Element(name = "NotValidBefore", required = false)
	private Date notValidBefore;
	
	@Element(name = "NotValidAfter", required = false)
	private Date notValidAfter;

	@Element(name = "Type", required = false)
	private String type;
	
	@Element(name = "Subtype", required = false)
	private String subtype;
	
	@Element(name = "Level", required = false)
	private String level;
	
	@Element(name = "Product", required = false)
	private RegistryIDType product;
	
	@Element(name = "CaptureDevice", required = false)
	private RegistryIDType captureDevice;
	
	@Element(name = "FeatureExtractionAlgorithm", required = false)
	private RegistryIDType featureExtractionAlgorithm;
	
	@Element(name = "ComparisonAlgorithm", required = false)
	private RegistryIDType comparisonAlgorithm;
	
	@Element(name = "CompressionAlgorithm", required = false)
	private RegistryIDType compressionAlgorithm;

	@Element(name = "Purpose", required = false)
	private String purpose;
	
	@Element(name = "Quality", required = false)
	private QualityType quality;

	// Getters & Setters
	public byte[] getChallengeResponse() { return challengeResponse; }
	public void setChallengeResponse(byte[] challengeResponse) { this.challengeResponse = challengeResponse; }

	public String getIndex() { return index; }
	public void setIndex(String index) { this.index = index; }

	public RegistryIDType getFormat() { return format; }
	public void setFormat(RegistryIDType format) { this.format = format; }

	public Boolean getEncryption() { return encryption; }
	public void setEncryption(Boolean encryption) { this.encryption = encryption; }

	public Date getCreationDate() { return creationDate; }
	public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

	public Date getNotValidBefore() { return notValidBefore; }
	public void setNotValidBefore(Date notValidBefore) { this.notValidBefore = notValidBefore; }

	public Date getNotValidAfter() { return notValidAfter; }
	public void setNotValidAfter(Date notValidAfter) { this.notValidAfter = notValidAfter; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public String getSubtype() { return subtype; }
	public void setSubtype(String subtype) { this.subtype = subtype; }

	public String getLevel() { return level; }
	public void setLevel(String level) { this.level = level; }

	public RegistryIDType getProduct() { return product; }
	public void setProduct(RegistryIDType product) { this.product = product; }

	public RegistryIDType getCaptureDevice() { return captureDevice; }
	public void setCaptureDevice(RegistryIDType captureDevice) { this.captureDevice = captureDevice; }

	public RegistryIDType getFeatureExtractionAlgorithm() { return featureExtractionAlgorithm; }
	public void setFeatureExtractionAlgorithm(RegistryIDType featureExtractionAlgorithm) { this.featureExtractionAlgorithm = featureExtractionAlgorithm; }

	public RegistryIDType getComparisonAlgorithm() { return comparisonAlgorithm; }
	public void setComparisonAlgorithm(RegistryIDType comparisonAlgorithm) { this.comparisonAlgorithm = comparisonAlgorithm; }

	public RegistryIDType getCompressionAlgorithm() { return compressionAlgorithm; }
	public void setCompressionAlgorithm(RegistryIDType compressionAlgorithm) { this.compressionAlgorithm = compressionAlgorithm; }

	public String getPurpose() { return purpose; }
	public void setPurpose(String purpose) { this.purpose = purpose; }

	public QualityType getQuality() { return quality; }
	public void setQuality(QualityType quality) { this.quality = quality; }

	public static class BDBInfoBuilder {
		private RegistryIDType format;
		private QualityType quality;
		private String type;
		private String subtype;
		private String purpose;
		private String level;
		private Date creationDate;
		private String index;
		private byte[] challengeResponse;
		private Boolean encryption;
		private Date notValidBefore;
		private Date notValidAfter;
		private RegistryIDType product;
		private RegistryIDType captureDevice;
		private RegistryIDType featureExtractionAlgorithm;
		private RegistryIDType comparisonAlgorithm;
		private RegistryIDType compressionAlgorithm;

		public BDBInfoBuilder withFormat(RegistryIDType format) {
			this.format = format;
			return this;
		}

		public BDBInfoBuilder withQuality(QualityType quality) {
			this.quality = quality;
			return this;
		}

		public BDBInfoBuilder withType(BiometricType biometricType) {
			this.type = biometricType != null ? biometricType.value() : null;
			return this;
		}

		public BDBInfoBuilder withType(List<BiometricType> biometricTypes) {
			if (biometricTypes != null && !biometricTypes.isEmpty()) {
				this.type = biometricTypes.get(0).value(); // Use first type
			}
			return this;
		}

		public BDBInfoBuilder withSubtype(String subtype) {
			this.subtype = subtype;
			return this;
		}

		public BDBInfoBuilder withPurpose(PurposeType purpose) {
			this.purpose = purpose != null ? purpose.value() : null;
			return this;
		}

		public BDBInfoBuilder withLevel(ProcessedLevelType level) {
			this.level = level != null ? level.value() : null;
			return this;
		}

		public BDBInfoBuilder withCreationDate(LocalDateTime creationDate) {
			if (creationDate != null) {
				this.creationDate = Date.from(creationDate.atZone(ZoneId.of("UTC")).toInstant());
			}
			return this;
		}

		public BDBInfoBuilder withIndex(String index) {
			this.index = index;
			return this;
		}

		public BDBInfoBuilder withChallengeResponse(byte[] challengeResponse) {
			this.challengeResponse = challengeResponse;
			return this;
		}

		public BDBInfoBuilder withEncryption(Boolean encryption) {
			this.encryption = encryption;
			return this;
		}

		public BDBInfoBuilder withNotValidBefore(Date notValidBefore) {
			this.notValidBefore = notValidBefore;
			return this;
		}

		public BDBInfoBuilder withNotValidAfter(Date notValidAfter) {
			this.notValidAfter = notValidAfter;
			return this;
		}

		public BDBInfoBuilder withProduct(RegistryIDType product) {
			this.product = product;
			return this;
		}

		public BDBInfoBuilder withCaptureDevice(RegistryIDType captureDevice) {
			this.captureDevice = captureDevice;
			return this;
		}

		public BDBInfoBuilder withFeatureExtractionAlgorithm(RegistryIDType featureExtractionAlgorithm) {
			this.featureExtractionAlgorithm = featureExtractionAlgorithm;
			return this;
		}

		public BDBInfoBuilder withComparisonAlgorithm(RegistryIDType comparisonAlgorithm) {
			this.comparisonAlgorithm = comparisonAlgorithm;
			return this;
		}

		public BDBInfoBuilder withCompressionAlgorithm(RegistryIDType compressionAlgorithm) {
			this.compressionAlgorithm = compressionAlgorithm;
			return this;
		}

		public BDBInfo build() {
			BDBInfo bdbInfo = new BDBInfo();
			bdbInfo.setFormat(format);
			bdbInfo.setQuality(quality);
			bdbInfo.setType(type);
			bdbInfo.setSubtype(subtype);
			bdbInfo.setPurpose(purpose);
			bdbInfo.setLevel(level);
			bdbInfo.setCreationDate(creationDate);
			bdbInfo.setIndex(index);
			bdbInfo.setChallengeResponse(challengeResponse);
			bdbInfo.setEncryption(encryption);
			bdbInfo.setNotValidBefore(notValidBefore);
			bdbInfo.setNotValidAfter(notValidAfter);
			bdbInfo.setProduct(product);
			bdbInfo.setCaptureDevice(captureDevice);
			bdbInfo.setFeatureExtractionAlgorithm(featureExtractionAlgorithm);
			bdbInfo.setComparisonAlgorithm(comparisonAlgorithm);
			bdbInfo.setCompressionAlgorithm(compressionAlgorithm);
			return bdbInfo;
		}
	}
}
