package io.mosip.registration.packetmanager.cbeffutil.common;

import android.util.Log;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;
import org.simpleframework.xml.transform.RegistryMatcher;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import io.mosip.registration.packetmanager.cbeffutil.exception.CbeffException;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BDBInfo;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.ByteArrayTransformer;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.CbeffConstant;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.DateTransformer;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.Entry;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.OthersList;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import io.mosip.registration.packetmanager.util.PacketManagerConstant;

/**
 * Utility Class to validate the data before generating a valid CBEFF XML
 * and to get all the data based on Type and SubType
 */
public class CbeffValidator {

    private static final String TAG = CbeffValidator.class.getSimpleName();

    // Singleton Serializer for better performance on Android
    // Register custom ByteArrayTransformer for byte[] fields
    private static final Serializer SERIALIZER = initSerializer();

    /**
     * Initialize Serializer with custom transformers
     * 
     * @return Serializer instance
     */
    private static Serializer initSerializer() {
        RegistryMatcher matcher = new RegistryMatcher();
        // Register custom byte[] transformer that uses CryptoUtil.base64encoder
        matcher.bind(byte[].class, ByteArrayTransformer.class);
        // Register Date transformer for XSD dateTime format (ISO-8601)
        matcher.bind(Date.class, DateTransformer.class);
        return new Persister(matcher);
    }

    /**
     * Method used for custom validation of the BIR
     *
     * @param birRoot BIR data
     * @return boolean value if BIR is valid
     * @throws CbeffException when any condition fails
     */
    public static boolean validateXML(BIR birRoot) throws CbeffException {
        if (birRoot == null) {
            throw new CbeffException("BIR value is null");
        }
        List<BIR> birList = birRoot.getBirs();
        if (birList == null || birList.isEmpty()) {
            throw new CbeffException("BIR list is empty");
        }

        for (BIR bir : birList) {
            if (bir != null) {
                // Check if this is an exception by looking through List<OthersList>
                boolean isException = false;
                if (bir.getOthers() != null && !bir.getOthers().isEmpty()) {
                    for (OthersList othersList : bir.getOthers()) {
                        if (othersList.getEntries() != null) {
                            for (Entry entry : othersList.getEntries()) {
                                if (PacketManagerConstant.OTHER_KEY_EXCEPTION.equals(entry.getKey())
                                        && "true".equals(entry.getValue())) {
                                    isException = true;
                                    break;
                                }
                            }
                        }
                        if (isException) break;
                    }
                }

                if ((bir.getBdb() == null || bir.getBdb().length < 1) && !isException) {
                    throw new CbeffException("BDB value can't be empty");
                }
                if (bir.getBdbInfo() == null) {
                    throw new CbeffException("BDB information can't be empty");
                }

                BDBInfo bdbInfo = bir.getBdbInfo();
                String typeString = bdbInfo.getType();

                if (typeString == null || typeString.isEmpty()) {
                    throw new CbeffException("Type value needs to be provided");
                }

                BiometricType biometricType = BiometricType.fromValue(typeString);

                if (bdbInfo.getFormat() == null || bdbInfo.getFormat().getType() == null) {
                    throw new CbeffException("Format type is missing");
                }

                if (!validateFormatType(Long.valueOf(bdbInfo.getFormat().getType()), biometricType)) {
                    throw new CbeffException("Patron Format type is invalid");
                }
            }
        }
        return true;
    }

    /**
     * Method used for validation of Format Type
     *
     * @param formatType    format type
     * @param biometricType biometric type
     * @return boolean value if format type is matching with type
     */
    private static boolean validateFormatType(long formatType, BiometricType biometricType) {
        switch (biometricType.value()) {
            case "Finger":
                return formatType == CbeffConstant.FORMAT_TYPE_FINGER
                        || formatType == CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE;
            case "Iris":
                return formatType == CbeffConstant.FORMAT_TYPE_IRIS;
            case "ExceptionPhoto":
            case "Face":
                return formatType == CbeffConstant.FORMAT_TYPE_FACE;
            case "HandGeometry":
                return formatType == CbeffConstant.FORMAT_TYPE_FACE;
        }
        return false;
    }

    /**
     * Method used for creating XML bytes using SimpleXML (Android-compatible)
     *
     * @param bir BIR type
     * @param xsd xml schema definition
     * @return byte[] byte array of XML data
     * @throws Exception exception
     */
    public static byte[] createXMLBytes(BIR bir, byte[] xsd) throws Exception {
        CbeffValidator.validateXML(bir);

        // Use SimpleXML for marshalling (Android-compatible)
        // Marshaling to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);

        SERIALIZER.write(bir, writer);
        writer.close();

        byte[] savedData = baos.toByteArray();

        // Remove SimpleXML-generated length="xxxx" attributes from byte[] elements
        // XSD requires base64Binary to be simple text content without attributes
        String xmlString = new String(savedData, StandardCharsets.UTF_8);
        
        // Remove length attributes from byte[] elements
        xmlString = xmlString.replaceAll("(<BDB)([^>]*length=\"[0-9]+\")", "$1");
        xmlString = xmlString.replaceAll("(<SB)([^>]*length=\"[0-9]+\")", "$1");
        xmlString = xmlString.replaceAll("(<Payload)([^>]*length=\"[0-9]+\")", "$1");
        xmlString = xmlString.replaceAll("(<ChallengeResponse)([^>]*length=\"[0-9]+\")", "$1");
        
        // Fix URL-safe Base64 to standard Base64 in BDB, SB, Payload, ChallengeResponse elements
        // XSD requires standard Base64 (+, /) not URL-safe Base64 (-, _)
        xmlString = fixBase64InElements(xmlString, "BDB");
        xmlString = fixBase64InElements(xmlString, "SB");
        xmlString = fixBase64InElements(xmlString, "Payload");
        xmlString = fixBase64InElements(xmlString, "ChallengeResponse");
        
        savedData = xmlString.getBytes(StandardCharsets.UTF_8);

        // Log the generated XML for debugging
        Log.d(TAG, "=== Generated XML (first 1000 chars) ===");
        Log.d(TAG, xmlString.length() > 1000 ? xmlString.substring(0, 1000) + "..." : xmlString);
        Log.d(TAG, "==========================================");

        // Validate using XSD
        try {
            CbeffXSDValidator.validateXML(xsd, savedData);
            return savedData;
        } catch (SAXException e) {
            String message = e.getMessage();
            if (message != null && message.contains(":")) {
                message = message.substring(message.indexOf(":"));
            }
            throw new CbeffException("XSD validation failed due to attribute " + message, e);
        }
    }

    /**
     * Fix URL-safe Base64 to standard Base64 in XML element content
     * XSD requires standard Base64 (+, /) not URL-safe Base64 (-, _)
     * 
     * @param xml XML string
     * @param elementName Element name (e.g., "BDB", "SB")
     * @return Fixed XML string
     */
    private static String fixBase64InElements(String xml, String elementName) {
        // Pattern to match: <ElementName>base64content</ElementName>
        // This handles both single-line and multi-line base64 content
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "<" + elementName + ">([^<]+)</" + elementName + ">",
            java.util.regex.Pattern.DOTALL
        );
        
        java.util.regex.Matcher matcher = pattern.matcher(xml);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String base64Content = matcher.group(1).trim();
            
            // Convert URL-safe Base64 to standard Base64
            // URL-safe: - and _ → Standard: + and /
            String standardBase64 = base64Content.replace('-', '+').replace('_', '/');
            
            // Add padding if needed (Base64 must be padded to length % 4 == 0)
            int padding = 4 - (standardBase64.length() % 4);
            if (padding < 4) {
                StringBuilder paddingBuilder = new StringBuilder(standardBase64);
                for (int i = 0; i < padding; i++) {
                    paddingBuilder.append('=');
                }
                standardBase64 = paddingBuilder.toString();
            }
            
            matcher.appendReplacement(sb, "<" + elementName + ">" + standardBase64 + "</" + elementName + ">");
        }
        
        matcher.appendTail(sb);
        return sb.toString();
    }
}
