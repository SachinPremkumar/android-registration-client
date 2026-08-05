package io.mosip.registration.keymanager.service;

import android.content.Context;
import android.util.Log;

/**
 * Test Certificate Provider for packet encryption testing.
 * Provides the public X.509 certificate directly in code so no local files or folder setup
 * are required on the Android device during testing.
 */
public class TestKeyStorageService {

    private static final String TAG = TestKeyStorageService.class.getSimpleName();

    /**
     * TEST-ONLY toggle flag. Set to true to use test certificate for packet encryption,
     * or set to false before production build to restore standard keymanager behavior.
     */
    public static boolean ENABLE_TEST_MODE = true;

    /**
     * Public X.509 Certificate PEM matching C:\Users\sachin.sp\Desktop\credissuer_test_keys\credissuer_test_private.pem.
     */
    public static final String TEST_CERTIFICATE_PEM =
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIDGDCCAgCgAwIBAgIUSlvgSBQRZsbIWzJIGJHPPWMXL98wDQYJKoZIhvcNAQEL\n" +
            "BQAwRjEYMBYGA1UEAwwPQ3JlZGlzc3Vlci1URVNUMRMwEQYDVQQKDApDcmVkSXNz\n" +
            "dWVyMRUwEwYDVQQLDAxUZXN0aW5nLU9ubHkwHhcNMjYwODAyMDk1ODMzWhcNMzYw\n" +
            "NzMxMDk1ODMzWjBGMRgwFgYDVQQDDA9DcmVkSXNzdWVyLVRFU1QxEzARBgNVBAoM\n" +
            "CkNyZWRJc3N1ZXIxFTATBgNVBAsMDFRlc3RpbmctT25seTCCASIwDQYJKoZIhvcN\n" +
            "AQEBBQADggEPADCCAQoCggEBALBM1irwNpr+UeGQzZcYyt8giWZ0zYWyUklrFCrV\n" +
            "q+YMGKFd+LA9DdtdzmWlaETo7iI6fh1YvGDloa4tfAMBaA5cLLwj1+9K88L5b5NI\n" +
            "iJ8spwj+VzRwSdNrkFDEZQrRf32ve6pe6FmaTJyCxr0595bURiUxIDP3h80MH/jf\n" +
            "PtNYbHZoR8jgfSPeFffHblEfDNrNK/d9I3aOln975c1JJ40QTwrDasH2oGZkEo7u\n" +
            "Tj5+RSE9V4TQBJ330IuAMwEYfNRvEYIkdvWRWgVl3Xt3fatJvgq2vNstIXjI8avZ\n" +
            "tn+IzWa0fSQDdJaiLBFnuotGcPX2+3ejSIOVfVWYB9FMfVUCAwEAATANBgkqhkiG\n" +
            "9w0BAQsFAAOCAQEAIqcOrB/egpxBBXZYCIvmJYu/2h0PW3RqNNGeWHbUB6wlQk1j\n" +
            "dL+A91qH1ql/n9K1ydutANlCzfr8iOO025bFDW+D4065VbxZhSGSh/erMPyX02yK\n" +
            "BeK3IR2YL9xDSbJdvXkMeFT8ET4T9n3DBEOoGqbRSc/nE1bq3L+M7WYWH0mZEthQ\n" +
            "ySddI8it97lzLFdr7KM8mAYLSaT7zocua6cBh3GiZQsXJOxN+E+EBsekVI3pI+4j\n" +
            "Hl6jgJOh4FzsvyJjaGmR7+4iaPfaUi71sh/iBv4XfTvsFAgUfDH4sGD0B7uHp6u6\n" +
            "pQ0pP+QKHUpf1mN2ULXy4Xs9rgt5DNdqDBjXpg==\n" +
            "-----END CERTIFICATE-----";

    /**
     * Checks if test mode is enabled.
     */
    public static boolean isTestModeEnabled(Context context) {
        return ENABLE_TEST_MODE;
    }

    /**
     * Returns the test public certificate PEM string directly.
     */
    public static String getTestCertificate(Context context) {
        Log.i(TAG, "Using direct test public certificate for packet encryption.");
        return TEST_CERTIFICATE_PEM;
    }
}
