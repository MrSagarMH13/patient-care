package com.makeen.patientcare.service;

import com.makeen.patientcare.dto.PatientDTO;

public interface IPatientService {
    PatientDTO upsertPatient(PatientDTO patientDTO);

    PatientDTO getPatient(String id);

    PatientDTO updatePatient(String id, PatientDTO patientDTO);

    Void deletePatient(String id);
}
