package io.mosip.registration.packetmanager.cbeffutil.common;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.transform.sax.SAXSource;

/**
 * Android-compatible XSD validator
 * Uses SAXSource (not STAX) to avoid Android compatibility issues
 */
public class CbeffXSDValidator {

    public static boolean validateXML(byte[] xsdBytes, byte[] xmlBytes) throws Exception {

        // Create schema factory
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // Load XSD using SAXSource (Android-safe, no STAX dependency)
        InputSource xsdInputSource = new InputSource(new ByteArrayInputStream(xsdBytes));
        SAXSource xsdSource = new SAXSource(xsdInputSource);
        Schema schema = factory.newSchema(xsdSource);

        // Create validator
        Validator validator = schema.newValidator();

        // Validate XML using SAXSource (Android-safe, no STAX dependency)
        InputSource xmlInputSource = new InputSource(new ByteArrayInputStream(xmlBytes));
        SAXSource xmlSource = new SAXSource(xmlInputSource);

        try {
            validator.validate(xmlSource);
        } catch (SAXException e) {
            throw e; // let CbeffValidator catch it
        }

        return true;
    }
}
