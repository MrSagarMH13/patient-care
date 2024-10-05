package com.makeen.patientcare.service;

import com.makeen.patientcare.dto.PatientDTO;

import java.util.List;

public interface ISearchService {
    void upsertPatient(PatientDTO patientDTO);

    List<PatientDTO> searchPatientByName(String name);
}
