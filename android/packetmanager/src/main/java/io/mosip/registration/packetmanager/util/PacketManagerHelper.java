package io.mosip.registration.packetmanager.util;

import static io.mosip.registration.packetmanager.util.PacketManagerConstant.CREATION_DATE;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.ENCRYPTED_HASH;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.ID;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.PACKET_NAME;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.PROCESS;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.PROVIDER_NAME;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.PROVIDER_VERSION;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.SCHEMA_VERSION;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.SIGNATURE;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.SOURCE;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.*;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.Entry;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.OthersList;
import io.mosip.registration.packetmanager.cbeffutil.common.CbeffValidator;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * @Author Anshul Vanawat
 */
@Singleton
public class PacketManagerHelper {

    private static final String TAG = PacketManagerHelper.class.getSimpleName();

    private String configServerFileStorageURL;
    private String schemaName;
    private Context context;

    @Inject
    public PacketManagerHelper(Context context) {
        this.context = context;
        configServerFileStorageURL = ConfigService.getProperty("mosip.kernel.xsdstorage-uri", context);
        schemaName = ConfigService.getProperty("mosip.kernel.xsdfile", context);
    }

    public byte[] getXMLData(BiometricRecord biometricRecord, boolean offlineMode) throws Exception {
        Log.i(TAG, "Loading XSD schema from assets folder");

        // Load XSD schema from assets folder only
        InputStream xsd = null;
        try {
            if (context == null) {
                throw new Exception("Context is null. Cannot load XSD schema from assets.");
            }

            // Load from assets folder
            try {
                xsd = context.getAssets().open(PacketManagerConstant.CBEFF_SCHEMA_FILE_PATH);
                Log.i(TAG,
                        "Successfully loaded XSD schema from assets: " + PacketManagerConstant.CBEFF_SCHEMA_FILE_PATH);
            } catch (IOException e) {
                throw new Exception("Unable to load XSD schema from assets folder. Please ensure " +
                        PacketManagerConstant.CBEFF_SCHEMA_FILE_PATH + " exists in src/main/assets/", e);
            }

            // Create BIR from biometric record using setters
            // The segments are already complete BIR objects built in RegistrationServiceImpl
            // We just need to wrap them in a parent BIR with top-level metadata
            BIR bir = new BIR();

            // Use version/cbeffversion/birInfo from BiometricRecord if available, otherwise use defaults
            VersionType version = biometricRecord.getVersion();
            if (version == null) {
                version = new VersionType();
                version.setMajor(1);
                version.setMinor(1);
            }
            VersionType cbeffversion = biometricRecord.getCbeffversion();
            if (cbeffversion == null) {
                cbeffversion = new VersionType();
                cbeffversion.setMajor(1);
                cbeffversion.setMinor(1);
            }
            BIRInfo birInfo = biometricRecord.getBirInfo();
            if (birInfo == null) {
                birInfo = new BIRInfo();
                birInfo.setIntegrity(false);
            }

            bir.setVersion(version);
            bir.setCbeffVersion(cbeffversion);
            bir.setBirInfo(birInfo);

            // Use segments directly - they're already complete BIR objects with all required fields
            // (Version, CBEFFVersion, BIRInfo, BDBInfo, etc.) set in RegistrationServiceImpl.buildBIR()
            List<BIR> segments = biometricRecord.getSegments();
            if (segments != null && !segments.isEmpty()) {
                bir.setBirs(segments);
            }

            // Set others from biometricRecord - convert Map to List<OthersList>
            if (biometricRecord.getOthers() != null && !biometricRecord.getOthers().isEmpty()) {
                OthersList othersList = new OthersList();
                List<Entry> entries = new ArrayList<>();
                for (Map.Entry<String, String> entry : biometricRecord.getOthers().entrySet()) {
                    Entry e = new Entry(entry.getKey(), entry.getValue());
                    entries.add(e);
                }
                othersList.setEntries(entries);
                bir.setOthers(new ArrayList<>(java.util.Collections.singletonList(othersList)));
            }

            // Create XML bytes using CbeffValidator
            byte[] xsdBytes = IOUtils.toByteArray(xsd);
            return CbeffValidator.createXMLBytes(bir, xsdBytes);

        } finally {
            if (xsd != null) {
                try {
                    xsd.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing XSD stream", e);
                }
            }
        }
    }

    public static byte[] generateHash(List<String> order, Map<String, byte[]> data)
            throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (order != null && !order.isEmpty()) {
            for (String name : order) {
                byte[] value = data.get(name);
                if (value != null) {
                    outputStream.write(value);
                } else {
                    Log.w(TAG, "Null value found for key: " + name + " in generateHash");
                }
            }
            return HMACUtils2.digestAsPlainText(outputStream.toByteArray()).getBytes();
        }
        return null;
    }

    public static Map<String, Object> getMetaMap(PacketInfo packetInfo) {
        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put(ID, packetInfo.getId());
        metaMap.put(PACKET_NAME, packetInfo.getPacketName());
        metaMap.put(SOURCE, packetInfo.getSource());
        metaMap.put(PROCESS, packetInfo.getProcess());
        metaMap.put(SCHEMA_VERSION, packetInfo.getSchemaVersion());
        metaMap.put(SIGNATURE, packetInfo.getSignature());
        metaMap.put(ENCRYPTED_HASH, packetInfo.getEncryptedHash());
        metaMap.put(PROVIDER_NAME, packetInfo.getProviderName());
        metaMap.put(PROVIDER_VERSION, packetInfo.getProviderVersion());
        metaMap.put(CREATION_DATE, packetInfo.getCreationDate());
        return metaMap;
    }

    public static PacketInfo getPacketInfo(Map<String, Object> metaMap) {
        PacketInfo packetInfo = new PacketInfo();
        packetInfo.setId((String) metaMap.get(ID));
        packetInfo.setPacketName((String) metaMap.get(PACKET_NAME));
        packetInfo.setSource((String) metaMap.get(SOURCE));
        packetInfo.setProcess((String) metaMap.get(PROCESS));
        packetInfo.setSchemaVersion((String) metaMap.get(SCHEMA_VERSION));
        packetInfo.setSignature((String) metaMap.get(SIGNATURE));
        packetInfo.setEncryptedHash((String) metaMap.get(ENCRYPTED_HASH));
        packetInfo.setProviderName((String) metaMap.get(PROVIDER_NAME));
        packetInfo.setProviderVersion((String) metaMap.get(PROVIDER_VERSION));
        packetInfo.setCreationDate((String) metaMap.get(CREATION_DATE));
        return packetInfo;
    }
}
