package io.mosip.registration.keymanager.service;

/**
 * TEMPORARY, TEST-ONLY toggle for evaluating "Option 3": swapping the packet-encryption
 * certificate source away from the MOSIP keymanager. Flip ENABLED to false (or delete this
 * file and the one call site that references it) to fully restore normal behavior.
 *
 * Do not ship a build with ENABLED = true. TEST_CERTIFICATE_PEM's matching private key is a
 * throwaway key generated for this test and is not stored anywhere in this repo.
 */
public final class CredIssuerTestConfig {

    public static final boolean ENABLED = false;

    public static final String TEST_CERTIFICATE_PEM =
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIDbTCCAlWgAwIBAgIUTblp6TUFVN1lROmVuPDuUcM33cowDQYJKoZIhvcNAQEL\n" +
            "BQAwRjEYMBYGA1UEAwwPQ3JlZElzc3Vlci1URVNUMRMwEQYDVQQKDApDcmVkSXNz\n" +
            "dWVyMRUwEwYDVQQLDAxUZXN0aW5nLU9ubHkwHhcNMjYwNzIzMDcwMTUyWhcNMjcw\n" +
            "NzIzMDcwMTUyWjBGMRgwFgYDVQQDDA9DcmVkSXNzdWVyLVRFU1QxEzARBgNVBAoM\n" +
            "CkNyZWRJc3N1ZXIxFTATBgNVBAsMDFRlc3RpbmctT25seTCCASIwDQYJKoZIhvcN\n" +
            "AQEBBQADggEPADCCAQoCggEBAJR3owbdTYohW8+CBIS02ox9DPCigMDL5mFqWHZ0\n" +
            "PX23wLYS/LuAL0JuEgYV3QsAvvjuyW/r5iO3cCkZoFqyblsTc+zwpOzvPdE+0Avv\n" +
            "kAGxIQiQG8Yei2QKst70HlV+54p+EYCRcdkaZ1IgcwI9Gm/cI08R+9QJD8zJZ2Pi\n" +
            "kd1h63ye5TkAalENkTzIG/+tN0cNWJaIDmYQrq/jYF8Os4B61As55d/7/T/i+p3r\n" +
            "9s+XGoIL8aLCEl6rq8HH5orh4QA8MG0HYjIGuMej4ssU6pwv3pNgdMStKFk2IxtP\n" +
            "d64F/xR1Rg6tW5j+YYdB5A13uPkcdGp9kfp9C80a1Whbo60CAwEAAaNTMFEwHQYD\n" +
            "VR0OBBYEFPtjFu8wkI11WE7alowClMUhhueOMB8GA1UdIwQYMBaAFPtjFu8wkI11\n" +
            "WE7alowClMUhhueOMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEB\n" +
            "AAmg3G5nzBCLRoKDkd/+dbcWHpB3K4/w9LR1kOjE0jmBsxfyruRLUK6fq7Oe0pDx\n" +
            "HHKg8mAoKNvl/Cs+JfZLTfJgts17DEWQdCsvpcGYNbTLIAhx89MXNmySqIajKEEM\n" +
            "KmhHOQT1r9Wxx2dXOM8Q0z20RwHRIbB6b5pq1jRYbpqqgc7UfVMJFXC+XHFJWK2n\n" +
            "xheG8/18MXFKgHOQllbOBjqG4SYhEiZkPFFALyDXq8uug0p+Xo+rDOlcWOlUR6gu\n" +
            "wdXtc0rOKXLKxPQTwXQWsIGkPmCv8Cngz4Jk3SVVE5R4Csxg0h/B4rRL03/sJFYO\n" +
            "bhrEweiuAOmGJY9bs+qNpgw=\n" +
            "-----END CERTIFICATE-----\n";

    private CredIssuerTestConfig() {
    }
}