package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import org.simpleframework.xml.transform.Transform;

public class BiometricTypeTransformer implements Transform<BiometricType> {

    @Override
    public BiometricType read(String value) throws Exception {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return BiometricType.fromValue(value);
    }

    @Override
    public String write(BiometricType value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.value();
    }
}
