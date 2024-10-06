package com.makeen.patientcare.service;

import com.makeen.patientcare.dto.PatientDTO;

import java.util.List;

public interface ISearchService {
    void upsertPatient(PatientDTO patientDTO);

    void deletePatient(String patientId);

    List<PatientDTO> searchPatientByName();

    List<PatientDTO> searchPatientByName(String name);
}
