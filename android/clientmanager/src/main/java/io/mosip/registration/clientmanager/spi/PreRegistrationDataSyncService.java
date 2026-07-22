package io.mosip.registration.clientmanager.spi;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.dto.ResponseDto;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;

public interface PreRegistrationDataSyncService {
    Map<String, Object> getPreRegistration(String preRegistrationId, boolean forceDownload);
    void fetchPreRegistrationIds(Runnable onFinish, String jobId);

    /**
     * Result of the most recently completed {@link #fetchPreRegistrationIds(Runnable, String)} call.
     * Empty string if it succeeded; {@code APPLICATION_ID_SYNC_FAILED} if it failed.
     * Must be read from within (or after) the {@code onFinish} callback.
     */
    String getLastFetchPreRegistrationIdsResult();
    ResponseDto fetchAndDeleteRecords();
    void deletePreRegRecords(ResponseDto responseDTO, List<PreRegistrationList> preRegList);
    PreRegistrationList getPreRegistrationRecordForDeletion(String preRegistrationId);
    Timestamp getLastPreRegPacketDownloadedTime();

    /**
     * Deletes ALL pre-registration records from the local filesystem and database,
     * regardless of appointment date. Used exclusively during center remap cleanup.
     */
    void deleteAllPreRegRecords();
}