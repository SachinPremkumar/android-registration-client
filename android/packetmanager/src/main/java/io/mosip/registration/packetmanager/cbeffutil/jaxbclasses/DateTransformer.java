package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.transform.Transform;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Transformer for Date to XSD dateTime format (ISO-8601)
 * Formats dates as: yyyy-MM-dd'T'HH:mm:ss.SSSXXX (e.g., "2025-12-12T14:55:09.500+05:30")
 */
public class DateTransformer implements Transform<Date> {
    
    private static final SimpleDateFormat XSD_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
    
    static {
        // Use system default timezone (will format with offset like +05:30)
        XSD_DATETIME_FORMAT.setTimeZone(TimeZone.getDefault());
    }
    
    @Override
    public Date read(String value) throws Exception {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // Parse XSD dateTime format
        return XSD_DATETIME_FORMAT.parse(value);
    }

    @Override
    public String write(Date value) throws Exception {
        if (value == null) {
            return null;
        }
        // Format to XSD dateTime with timezone offset
        return XSD_DATETIME_FORMAT.format(value);
    }
}

