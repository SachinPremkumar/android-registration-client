package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name = "Quality")
@Namespace(reference = "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
@Order(elements = {
        "Algorithm",
        "Score",
        "QualityCalculationFailed"
})
public class QualityType {

	@Element(name = "Algorithm", required = false)
	private RegistryIDType algorithm;

	@Element(name = "Score", required = false)
	private Long score;

	@Element(name = "QualityCalculationFailed", required = false)
	private String qualityCalculationFailed;

	/** Required by JAXB */
	public QualityType() {}

	/** Optional convenience constructor */
	public QualityType(RegistryIDType algorithm, Long score, String qualityCalculationFailed) {
		this.algorithm = algorithm;
		this.score = score;
		this.qualityCalculationFailed = qualityCalculationFailed;
	}

	// ---------------------------
	// Getters & Setters
	// ---------------------------

	public RegistryIDType getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(RegistryIDType algorithm) {
		this.algorithm = algorithm;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public String getQualityCalculationFailed() {
		return qualityCalculationFailed;
	}

	public void setQualityCalculationFailed(String qualityCalculationFailed) {
		this.qualityCalculationFailed = qualityCalculationFailed;
	}
}
