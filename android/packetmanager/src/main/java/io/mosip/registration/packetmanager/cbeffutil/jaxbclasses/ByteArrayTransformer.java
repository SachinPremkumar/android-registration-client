package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import io.mosip.registration.keymanager.util.CryptoUtil;
import org.simpleframework.xml.transform.Transform;

import java.util.Base64;

/**
 * Transformer for byte[] to XSD base64Binary format
 * Converts URL-safe Base64 (from CryptoUtil) to standard Base64 (required by XSD)
 */
public class ByteArrayTransformer implements Transform<byte[]> {
    
    @Override
    public byte[] read(String value) throws Exception {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // Convert standard Base64 to URL-safe for decoding
        String urlSafeBase64 = value.replace('+', '-').replace('/', '_');
        // Remove padding for URL-safe decoder
        urlSafeBase64 = urlSafeBase64.replaceAll("=+$", "");
        return CryptoUtil.base64decoder.decode(urlSafeBase64);
    }

    @Override
    public String write(byte[] value) throws Exception {
        if (value == null) {
            return "";
        }
        // CryptoUtil uses URL-safe Base64, but XSD requires standard Base64
        String urlSafeBase64 = CryptoUtil.base64encoder.encodeToString(value);
        
        // Convert URL-safe Base64 to standard Base64 for XSD compliance
        // URL-safe: - and _ → Standard: + and /
        String standardBase64 = urlSafeBase64.replace('-', '+').replace('_', '/');
        
        // Add padding if needed (Base64 must be padded to length % 4 == 0)
        int padding = 4 - (standardBase64.length() % 4);
        if (padding < 4) {
            StringBuilder sb = new StringBuilder(standardBase64);
            for (int i = 0; i < padding; i++) {
                sb.append('=');
            }
            standardBase64 = sb.toString();
        }
        
        return standardBase64;
    }
}
