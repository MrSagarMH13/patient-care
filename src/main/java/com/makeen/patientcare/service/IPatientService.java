package com.makeen.patientcare.service;

import com.makeen.patientcare.dto.PatientDTO;

import java.io.IOException;

public interface IPatientService {
    PatientDTO upsertPatient(PatientDTO patientDTO);

    PatientDTO getPatient(String id);

    PatientDTO updatePatient(String id, PatientDTO patientDTO);

    PatientDTO deletePatient(String id);
}
