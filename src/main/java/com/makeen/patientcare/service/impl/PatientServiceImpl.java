package com.makeen.patientcare.service.impl;

import com.makeen.patientcare.client.AidboxClient;
import com.makeen.patientcare.dto.PatientDTO;
import com.makeen.patientcare.service.IPatientService;
import com.makeen.patientcare.utils.FhirConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
public class PatientServiceImpl implements IPatientService {

    private final AidboxClient aidboxClient;

    @Override
    public PatientDTO upsertPatient(PatientDTO patientDTO) {
        log.info("Upserting patient into Aidbox: {}", patientDTO);
        return aidboxClient.createPatient(patientDTO);
    }

    @Override
    public PatientDTO getPatient(String id) {
        log.info("Getting patient with id: {}", id);
        return aidboxClient.getPatient(id);
    }

    @Override
    public PatientDTO updatePatient(String id, PatientDTO patientDTO) {
        log.info("Updating patient with id: {}", id);
        return aidboxClient.updatePatient(id, patientDTO);
    }

    @Override
    public PatientDTO deletePatient(String id) {
        log.info("Deleting patient with id: {}", id);
        return aidboxClient.deletePatient(id);
    }
}
