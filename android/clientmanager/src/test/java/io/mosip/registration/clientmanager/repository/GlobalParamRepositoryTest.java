package io.mosip.registration.clientmanager.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.LocalConfigDAO;
import io.mosip.registration.clientmanager.entity.GlobalParam;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GlobalParamRepositoryTest {

    private static final String GLOBAL_PARAM_STRING_ID = "mosip.lang-code";
    private static final String GLOBAL_PARAM_STRING_VALUE = "eng";

    private static final String GLOBAL_PARAM_BOOLEAN_ID = "mosip.isSyncJobActive";
    private static final Boolean GLOBAL_PARAM_BOOLEAN_VALUE = true;

    private static final String GLOBAL_PARAM_INT_ID = "mosip.syncJobId";
    private static final int GLOBAL_PARAM_INT_VALUE = 1;

    private static final String GLOBAL_PARAM_STRING_ID_NOT_CACHED = "mosip.lang-code-not-cached";

    Context appContext;
    ClientDatabase clientDatabase;
    GlobalParamRepository globalParamRepository;
    GlobalParamDao globalParamDao;
    LocalConfigDAO mockLocalConfigDAO;

    @Before
    public void setUp() {
        clearGlobalParamCache();
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();

        globalParamDao = clientDatabase.globalParamDao();
        mockLocalConfigDAO = mock(LocalConfigDAO.class);
        when(mockLocalConfigDAO.getLocalConfigurations()).thenReturn(new HashMap<String, String>());
        globalParamRepository = new GlobalParamRepository(globalParamDao, mockLocalConfigDAO);
        globalParamRepository.refreshConfigurationCache();
    }

    @After
    public void tearDown() {
        clientDatabase.close();
        clearGlobalParamCache();
    }

    private void clearGlobalParamCache() {
        try {
            Field cacheField = GlobalParamRepository.class.getDeclaredField("globalParamMap");
            cacheField.setAccessible(true);
            Map<String, String> cache = (Map<String, String>) cacheField.get(null);
            if (cache != null) {
                cache.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear global param cache", e);
        }
    }

    @Test
    public void saveGlobalParam_withStringValue_persistsAndReturnsCachedValue() {
        globalParamRepository.saveGlobalParam(GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_VALUE);

        String globalParamValue = globalParamRepository.getGlobalParamValue(GLOBAL_PARAM_STRING_ID);
        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamValue);

        String globalParamCachedValue = globalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID);
        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamCachedValue);
    }

    @Test
    public void saveGlobalParams_withParamList_persistsAllParams() {
        List<GlobalParam> globalParamList = new ArrayList<>();
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_BOOLEAN_ID, GLOBAL_PARAM_BOOLEAN_ID, GLOBAL_PARAM_BOOLEAN_VALUE.toString(), true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_VALUE, true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_INT_ID, GLOBAL_PARAM_INT_ID, String.valueOf(GLOBAL_PARAM_INT_VALUE), true));

        globalParamRepository.saveGlobalParams(globalParamList);
        List<GlobalParam> globalParams = globalParamRepository.getGlobalParams();
        assertEquals(3, globalParams.size());
    }

    @Test
    public void getCachedGlobalParams_afterSavingList_returnsCachedValues() {
        List<GlobalParam> globalParamList = new ArrayList<>();
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_BOOLEAN_ID, GLOBAL_PARAM_BOOLEAN_ID, GLOBAL_PARAM_BOOLEAN_VALUE.toString(), true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_VALUE, true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_INT_ID, GLOBAL_PARAM_INT_ID, String.valueOf(GLOBAL_PARAM_INT_VALUE), true));
        globalParamRepository.saveGlobalParams(globalParamList);

        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID));
        assertEquals(GLOBAL_PARAM_BOOLEAN_VALUE, globalParamRepository.getCachedBooleanGlobalParam(GLOBAL_PARAM_BOOLEAN_ID));
        assertEquals(GLOBAL_PARAM_INT_VALUE, globalParamRepository.getCachedIntegerGlobalParam(GLOBAL_PARAM_INT_ID));
    }

    @Test
    public void getCachedGlobalParams_withUnknownKey_returnsNullOrZero() {
        assertNull(globalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID_NOT_CACHED));
        assertNull(globalParamRepository.getCachedBooleanGlobalParam(GLOBAL_PARAM_STRING_ID_NOT_CACHED));
        assertEquals(0, globalParamRepository.getCachedIntegerGlobalParam(GLOBAL_PARAM_STRING_ID_NOT_CACHED));
    }

    @Test
    public void getMandatoryLanguageCodes_withDuplicatesAndMixedCase_returnsDedupedList() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MANDATORY_LANGUAGES_KEY, "ENG, eng, hin , , HIN");
        List<String> codes = globalParamRepository.getMandatoryLanguageCodes();
        assertEquals(Arrays.asList("eng", "hin"), codes);
    }

    @Test
    public void getOptionalLanguageCodes_withDuplicatesAndMixedCase_returnsDedupedList() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.OPTIONAL_LANGUAGES_KEY, "kan, tam, KAN");
        List<String> codes = globalParamRepository.getOptionalLanguageCodes();
        assertEquals(Arrays.asList("kan", "tam"), codes);
    }

    @Test
    public void getMaxLanguageCount_withValidPositiveValue_returnsConfiguredCount() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MAX_LANGUAGES_COUNT_KEY, "5");
        assertEquals(5, globalParamRepository.getMaxLanguageCount());
    }

    @Test
    public void getMaxLanguageCount_withZeroValue_returnsDefaultOne() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MAX_LANGUAGES_COUNT_KEY, "0");
        assertEquals(1, globalParamRepository.getMaxLanguageCount());
    }

    @Test
    public void getMinLanguageCount_withValidPositiveValue_returnsConfiguredCount() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MIN_LANGUAGES_COUNT_KEY, "2");
        assertEquals(2, globalParamRepository.getMinLanguageCount());
    }

    @Test
    public void getMinLanguageCount_withNegativeValue_returnsDefaultOne() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MIN_LANGUAGES_COUNT_KEY, "-1");
        assertEquals(1, globalParamRepository.getMinLanguageCount());
    }

    @Test
    public void getSelectedHandles_withDuplicateEntries_returnsDedupedList() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.SELECTED_HANDLES, "phone, email , phone");
        List<String> handles = globalParamRepository.getSelectedHandles();
        assertEquals(Arrays.asList("phone", "email"), handles);
    }

    @Test
    public void getCachedStringDefaults_whenNotConfigured_returnsPredefinedDefaults() {
        assertEquals("applicanttype.mvel", globalParamRepository.getCachedStringMAVELScript());
        assertNull(globalParamRepository.getCachedStringPreRegPacketLocation());
    }

    @Test
    public void refreshConfigurationCache_withLocalOverrides_overridesRemoteValues() {
        globalParamRepository.saveGlobalParam("param1", "remote");
        Map<String, String> overrides = new HashMap<>();
        overrides.put("param1", "local");
        overrides.put("param2", "localOnly");
        when(mockLocalConfigDAO.getLocalConfigurations()).thenReturn(overrides);

        globalParamRepository.refreshConfigurationCache();

        assertEquals("local", globalParamRepository.getCachedStringGlobalParam("param1"));
        assertEquals("localOnly", globalParamRepository.getCachedStringGlobalParam("param2"));

        when(mockLocalConfigDAO.getLocalConfigurations()).thenReturn(new HashMap<String, String>());
    }

    @Test
    public void refreshConfigurationCache_whenDaoThrowsException_completesWithoutThrowing() {
        // Test that refreshConfigurationCache handles exceptions gracefully:
        // 1. Method completes without throwing
        // 2. Exception is logged
        // 3. Cache remains usable after exception
        
        // Save a param before the exception to verify cache state
        globalParamRepository.saveGlobalParam("testParam", "testValue");
        
        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class))).thenReturn(0);
            logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString())).thenReturn(0);

            RuntimeException testException = new RuntimeException("boom");
            Mockito.doThrow(testException).when(mockLocalConfigDAO).getLocalConfigurations();
            
            // Assertion 1: Method should complete without throwing (implicit - test would fail if exception propagated)
            globalParamRepository.refreshConfigurationCache();
            
            // Assertion 2: Verify exception was logged (with Throwable parameter)
            logMock.verify(() -> Log.e(
                    Mockito.anyString(),
                    Mockito.eq("Error refreshing configuration cache"),
                    Mockito.any(Throwable.class)));
            
            // Assertion 3: Verify cache is still usable (contains previously saved param)
            assertEquals("testValue", globalParamRepository.getCachedStringGlobalParam("testParam"));
        }
        
        // Restore mock for other tests
        Mockito.doReturn(new HashMap<String, String>()).when(mockLocalConfigDAO).getLocalConfigurations();
    }

    @Test
    public void getGlobalParamsByPattern_withValueHavingSpaces_returnsTrimmedValue() {
        GlobalParamDao mockDao = mock(GlobalParamDao.class);
        LocalConfigDAO mockLocal = mock(LocalConfigDAO.class);
        when(mockDao.getGlobalParams()).thenReturn(Collections.emptyList());
        when(mockLocal.getLocalConfigurations()).thenReturn(Collections.emptyMap());

        GlobalParamRepository repositoryWithMocks = new GlobalParamRepository(mockDao, mockLocal);

        GlobalParam param = new GlobalParam("id", "demo", " value ", true);
        when(mockDao.findByNameLikeAndIsActiveTrueAndValIsNotNull("demo")).thenReturn(Collections.singletonList(param));

        Map<String, Object> result = repositoryWithMocks.getGlobalParamsByPattern("demo");
        assertEquals("value", result.get("demo"));
    }

    @Test
    public void getCachedStringAgeGroup_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringAgeGroup());
    }

    @Test
    public void getCachedStringForgotPassword_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringForgotPassword());
    }

    @Test
    public void getCachedStringIdleTime_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringIdleTime());
    }

    @Test
    public void getCachedStringRefreshedLoginTime_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringRefreshedLoginTime());
    }

    @Test
    public void getCachedStringGpsDeviceEnableFlag_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringGpsDeviceEnableFlag());
    }

    @Test
    public void getCachedStringMachineToCenterDistance_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringMachineToCenterDistance());
    }

    @Test
    public void getCachedStringOperatorOnboardingBioAttributes_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringOperatorOnboardingBioAttributes());
    }

    @Test
    public void getCachedStringOnboardYourselfUrl_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringOnboardYourselfUrl());
    }

    @Test
    public void getCachedStringRegisteringIndividualUrl_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringRegisteringIndividualUrl());
    }

    @Test
    public void getCachedStringSyncDataUrl_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringSyncDataUrl());
    }

    @Test
    public void getCachedStringMappingDevicesUrl_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringMappingDevicesUrl());
    }

    @Test
    public void getCachedStringUploadingDataUrl_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringUploadingDataUrl());
    }

    @Test
    public void getCachedStringUpdatingBiometricsUrl_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringUpdatingBiometricsUrl());
    }

    @Test
    public void getCachedStringPasswordLength_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringPasswordLength());
    }

    @Test
    public void getCachedStringDocumentSize_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringDocumentSize());
    }

    @Test
    public void getCachedStringDOBAgeLimit_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringDOBAgeLimit());
    }

    @Test
    public void getCachedStringDocType_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringDocType());
    }

    @Test
    public void getCachedStringAppName_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringAppName());
    }

    @Test
    public void getCachedStringAppId_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringAppId());
    }

    @Test
    public void getCachedStringDefaultHostIp_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringDefaultHostIp());
    }

    @Test
    public void getCachedStringDefaultHostName_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringDefaultHostName());
    }

    @Test
    public void getCachedStringFieldsToRetainOnPridFetch_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringFieldsToRetainOnPridFetch());
    }

    @Test
    public void getCachedStringPacketStoreLocation_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringPacketStoreLocation());
    }

    @Test
    public void getCachedStringJobsOffline_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringJobsOffline());
    }

    @Test
    public void getCachedStringJobsUntagged_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringJobsUntagged());
    }

    @Test
    public void getCachedStringJobsRestart_whenNotSet_returnsNull() {
        assertNull(globalParamRepository.getCachedStringJobsRestart());
    }

    @Test
    public void getCachedReadTimeout_withValidValue_returnsConfiguredLong() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.HTTP_API_READ_TIMEOUT, "30000");
        assertEquals(30000L, globalParamRepository.getCachedReadTimeout());
    }

    @Test
    public void getCachedReadTimeout_withNoValue_returnsZero() {
        assertEquals(0L, globalParamRepository.getCachedReadTimeout());
    }

    @Test
    public void getCachedReadTimeout_withNonNumericValue_returnsZero() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.HTTP_API_READ_TIMEOUT, "not-a-number");
        assertEquals(0L, globalParamRepository.getCachedReadTimeout());
    }

    @Test
    public void getCachedWriteTimeout_withValidValue_returnsConfiguredLong() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.HTTP_API_WRITE_TIMEOUT, "45000");
        assertEquals(45000L, globalParamRepository.getCachedWriteTimeout());
    }

    @Test
    public void getCachedIntCaptureTimeout_withValidValue_returnsIntCast() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.CAPTURE_TIMEOUT, "5000");
        assertEquals(5000, globalParamRepository.getCachedIntCaptureTimeout());
    }

    @Test
    public void getCachedIntCaptureTimeout_withZeroValue_returnsDefault() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.CAPTURE_TIMEOUT, "0");
        int defaultTimeout = Integer.parseInt(RegistrationConstants.DEFAULT_CAPTURE_TIMEOUT);
        assertEquals(defaultTimeout, globalParamRepository.getCachedIntCaptureTimeout());
    }

    @Test
    public void getCachedIntCaptureTimeout_withNoValue_returnsDefault() {
        int defaultTimeout = Integer.parseInt(RegistrationConstants.DEFAULT_CAPTURE_TIMEOUT);
        assertEquals(defaultTimeout, globalParamRepository.getCachedIntCaptureTimeout());
    }

    @Test
    public void getCachedIntegerDiskSpaceSize_withValidValue_returnsValue() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.DISK_SPACE, "500");
        assertEquals(500, globalParamRepository.getCachedIntegerDiskSpaceSize());
    }

    @Test
    public void getCachedIntegerPRIDLength_withValidValue_returnsValue() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.PRID_LENGTH, "14");
        assertEquals(14, globalParamRepository.getCachedIntegerPRIDLength());
    }

    @Test
    public void getCachedIntegerUINLength_withValidValue_returnsValue() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.UIN_LENGTH, "12");
        assertEquals(12, globalParamRepository.getCachedIntegerUINLength());
    }

    @Test
    public void getCachedIntegerVIDLength_withValidValue_returnsValue() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.VID_LENGTH, "16");
        assertEquals(16, globalParamRepository.getCachedIntegerVIDLength());
    }

    @Test
    public void getCachedIntRegMaxCountApproveLimit_withValidValue_returnsValue() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_APPRV_LIMIT, "10");
        assertEquals(10, globalParamRepository.getCachedIntRegMaxCountApproveLimit());
    }

    @Test
    public void getCachedStringInvalidLoginCount_whenSet_returnsValue() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.INVALID_LOGIN_COUNT, "5");
        assertEquals("5", globalParamRepository.getCachedStringInvalidLoginCount());
    }

    @Test
    public void getCachedStringInvalidLoginTime_whenSet_returnsValue() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.INVALID_LOGIN_TIME, "3");
        assertEquals("3", globalParamRepository.getCachedStringInvalidLoginTime());
    }

    @Test
    public void getGlobalParamValue_delegatesToDao() {
        GlobalParamDao mockDao = mock(GlobalParamDao.class);
        LocalConfigDAO mockLocal = mock(LocalConfigDAO.class);
        when(mockDao.getGlobalParams()).thenReturn(Collections.emptyList());
        when(mockLocal.getLocalConfigurations()).thenReturn(Collections.emptyMap());
        when(mockDao.getGlobalParam("some.key")).thenReturn("some-value");

        GlobalParamRepository repo = new GlobalParamRepository(mockDao, mockLocal);
        assertEquals("some-value", repo.getGlobalParamValue("some.key"));
    }

    @Test
    public void getGlobalParams_delegatesToDao() {
        GlobalParamDao mockDao = mock(GlobalParamDao.class);
        LocalConfigDAO mockLocal = mock(LocalConfigDAO.class);
        GlobalParam param = new GlobalParam("k", "k", "v", true);
        when(mockDao.getGlobalParams()).thenReturn(Collections.singletonList(param));
        when(mockLocal.getLocalConfigurations()).thenReturn(Collections.emptyMap());

        GlobalParamRepository repo = new GlobalParamRepository(mockDao, mockLocal);
        List<GlobalParam> result = repo.getGlobalParams();
        assertEquals(1, result.size());
    }

    @Test
    public void getBiometricProviderConfig_withProviderParams_returnsNestedMap() {
        globalParamRepository.saveGlobalParam(
                "mosip.biometric.sdk.providers.face.vendor1.classname", "com.example.FaceSDK");
        globalParamRepository.saveGlobalParam(
                "mosip.biometric.sdk.providers.finger.vendor2.classname", "com.example.FingerSDK");

        Map<String, Map<String, Map<String, String>>> config =
                globalParamRepository.getBiometricProviderConfig();

        assertNotNull(config);
        assertTrue(config.containsKey("face"));
        assertTrue(config.get("face").containsKey("vendor1"));
        assertEquals("com.example.FaceSDK", config.get("face").get("vendor1").get("classname"));
    }

    @Test
    public void getBiometricProviderConfig_secondCall_returnsCachedResult() {
        globalParamRepository.saveGlobalParam(
                "mosip.biometric.sdk.providers.face.vendor1.classname", "com.example.FaceSDK");

        Map<String, Map<String, Map<String, String>>> first =
                globalParamRepository.getBiometricProviderConfig();
        Map<String, Map<String, Map<String, String>>> second =
                globalParamRepository.getBiometricProviderConfig();

        assertNotNull(first);
        // Verify same object returned (cache hit)
        assertTrue(first == second);
    }

    @Test
    public void getBiometricProviderConfig_refreshClearsCacheForNextCall() {
        globalParamRepository.saveGlobalParam(
                "mosip.biometric.sdk.providers.face.vendor1.classname", "com.example.FaceSDK");
        Map<String, Map<String, Map<String, String>>> before =
                globalParamRepository.getBiometricProviderConfig();

        globalParamRepository.refreshConfigurationCache();

        Map<String, Map<String, Map<String, String>>> after =
                globalParamRepository.getBiometricProviderConfig();
        assertNotNull(before);
        assertNotNull(after);
    }
}