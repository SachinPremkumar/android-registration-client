package io.mosip.registration.packetmanager.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StorageUtilsTest {

    private Context mockContext;
    private MockedStatic<ConfigService> mockConfigService;
    private MockedStatic<Log> mockLog;
    private final List<File> tempDirs = new ArrayList<>();

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        mockLog = Mockito.mockStatic(Log.class);
        mockConfigService = Mockito.mockStatic(ConfigService.class);
    }

    @After
    public void tearDown() {
        mockConfigService.close();
        mockLog.close();
        for (File dir : tempDirs) {
            deleteRecursively(dir);
        }
    }

    private void deleteRecursively(File file) {
        if (file == null || !file.exists()) return;
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    private File createTempDir(String prefix) throws Exception {
        File dir = Files.createTempDirectory(prefix).toFile();
        tempDirs.add(dir);
        return dir;
    }

    @Test
    public void getPacketStorageDir_configNullDefaultsToPackets() throws Exception {
        mockConfigService.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn(null);
        when(mockContext.getExternalFilesDirs(null)).thenReturn(new File[]{});
        File internalDir = createTempDir("internal_");
        when(mockContext.getFilesDir()).thenReturn(internalDir);

        try (MockedStatic<Environment> envMock = Mockito.mockStatic(Environment.class)) {
            // Use a regular file (not dir) so mkdirs fails for the documents path
            File docsAsFile = File.createTempFile("docs_", ".tmp");
            docsAsFile.deleteOnExit();
            envMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
                    .thenReturn(docsAsFile);
            when(mockContext.getExternalFilesDir("packets")).thenReturn(null);

            File result = StorageUtils.getPacketStorageDir(mockContext);

            assertNotNull(result);
            assertEquals("packets", result.getName());
            assertTrue(result.getAbsolutePath().startsWith(internalDir.getAbsolutePath()));
        }
    }

    @Test
    public void getPacketStorageDir_customConfigLocation_usedAsSubdir() throws Exception {
        mockConfigService.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn("custom-packets");
        when(mockContext.getExternalFilesDirs(null)).thenReturn(new File[]{});
        File internalDir = createTempDir("internal_");
        when(mockContext.getFilesDir()).thenReturn(internalDir);

        try (MockedStatic<Environment> envMock = Mockito.mockStatic(Environment.class)) {
            File docsAsFile = File.createTempFile("docs_", ".tmp");
            docsAsFile.deleteOnExit();
            envMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
                    .thenReturn(docsAsFile);
            when(mockContext.getExternalFilesDir("custom-packets")).thenReturn(null);

            File result = StorageUtils.getPacketStorageDir(mockContext);

            assertNotNull(result);
            assertEquals("custom-packets", result.getName());
        }
    }

    @Test
    public void getPacketStorageDir_externalFilesDir_usedWhenDocumentsNotWritable() throws Exception {
        mockConfigService.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn("packets");
        when(mockContext.getExternalFilesDirs(null)).thenReturn(new File[]{});

        try (MockedStatic<Environment> envMock = Mockito.mockStatic(Environment.class)) {
            File docsAsFile = File.createTempFile("docs_", ".tmp");
            docsAsFile.deleteOnExit();
            envMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
                    .thenReturn(docsAsFile);
            File externalDir = createTempDir("external_");
            when(mockContext.getExternalFilesDir("packets")).thenReturn(externalDir);

            File result = StorageUtils.getPacketStorageDir(mockContext);

            assertNotNull(result);
            assertEquals(externalDir, result);
        }
    }

    @Test
    public void getPacketStorageDir_documentsDir_usedWhenSdCardSkipped() throws Exception {
        mockConfigService.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn("packets");
        // SD card entry exists but path has no "/Android/data/" → skipped
        File plainDir = createTempDir("nosdcard_");
        when(mockContext.getExternalFilesDirs(null)).thenReturn(new File[]{plainDir});

        try (MockedStatic<Environment> envMock = Mockito.mockStatic(Environment.class)) {
            envMock.when(() -> Environment.isExternalStorageRemovable(plainDir)).thenReturn(true);
            File docsParent = createTempDir("docs_parent_");
            envMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
                    .thenReturn(docsParent);

            File result = StorageUtils.getPacketStorageDir(mockContext);

            assertNotNull(result);
            assertEquals("packets", result.getName());
            assertTrue(result.getAbsolutePath().startsWith(docsParent.getAbsolutePath()));
        }
    }

    @Test
    public void getPacketStorageDir_documentsDir_usedWhenExternalDirsNull() throws Exception {
        mockConfigService.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn("packets");
        when(mockContext.getExternalFilesDirs(null)).thenReturn(null);

        try (MockedStatic<Environment> envMock = Mockito.mockStatic(Environment.class)) {
            File docsParent = createTempDir("docs_parent_");
            envMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
                    .thenReturn(docsParent);

            File result = StorageUtils.getPacketStorageDir(mockContext);

            assertNotNull(result);
            assertEquals("packets", result.getName());
            assertTrue(result.getAbsolutePath().startsWith(docsParent.getAbsolutePath()));
        }
    }

    @Test
    public void getPacketStorageDir_nullEntryInExternalDirs_handledGracefully() throws Exception {
        mockConfigService.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn("packets");
        when(mockContext.getExternalFilesDirs(null)).thenReturn(new File[]{null});

        try (MockedStatic<Environment> envMock = Mockito.mockStatic(Environment.class)) {
            File docsParent = createTempDir("docs_parent_");
            envMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
                    .thenReturn(docsParent);

            File result = StorageUtils.getPacketStorageDir(mockContext);

            assertNotNull(result);
            assertEquals("packets", result.getName());
        }
    }

    @Test
    public void getPacketStorageDir_sdCardNotRemovable_fallsToDocuments() throws Exception {
        mockConfigService.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn("packets");
        File sdCardDir = createTempDir("sdcard_");
        when(mockContext.getExternalFilesDirs(null)).thenReturn(new File[]{sdCardDir});

        try (MockedStatic<Environment> envMock = Mockito.mockStatic(Environment.class)) {
            envMock.when(() -> Environment.isExternalStorageRemovable(sdCardDir)).thenReturn(false);
            File docsParent = createTempDir("docs_parent_");
            envMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
                    .thenReturn(docsParent);

            File result = StorageUtils.getPacketStorageDir(mockContext);

            assertNotNull(result);
            assertEquals("packets", result.getName());
            assertTrue(result.getAbsolutePath().startsWith(docsParent.getAbsolutePath()));
        }
    }

    @Test
    public void getPacketStorageDir_sdCardRemovableWithAndroidDataPath_exercisesSdCardBranch() throws Exception {
        mockConfigService.when(() -> ConfigService.getProperty("objectstore.base.location", mockContext))
                .thenReturn("packets");

        // Construct a path that contains "/Android/data/" (works on Linux CI; falls through on Windows)
        File sdRoot = createTempDir("sdcard_");
        File sdCardAppDir = new File(sdRoot, "Android/data/io.mosip.registration");
        sdCardAppDir.mkdirs();

        when(mockContext.getExternalFilesDirs(null)).thenReturn(new File[]{sdCardAppDir});

        try (MockedStatic<Environment> envMock = Mockito.mockStatic(Environment.class)) {
            envMock.when(() -> Environment.isExternalStorageRemovable(sdCardAppDir)).thenReturn(true);
            File docsParent = createTempDir("docs_parent_");
            envMock.when(() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
                    .thenReturn(docsParent);

            File result = StorageUtils.getPacketStorageDir(mockContext);

            assertNotNull(result);
            assertEquals("packets", result.getName());
        }
    }
}