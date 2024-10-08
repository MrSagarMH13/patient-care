package com.makeen.patientcare.controller;


import com.makeen.patientcare.dto.PatientDTO;
import com.makeen.patientcare.dto.ResponseDTO;
import com.makeen.patientcare.service.IPatientService;
import com.makeen.patientcare.service.ISearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final IPatientService patientService;
    private final ISearchService searchService;

    // Create or update patient
    @Operation(summary = "Upsert a patient", description = "Create or update a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient upserted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upsert")
    public ResponseEntity<ResponseDTO<PatientDTO>> upsertPatient(@RequestBody PatientDTO patientDTO) {
        log.info("Request to upsert patient: {}", patientDTO);

        PatientDTO response = patientService.upsertPatient(patientDTO);
        log.info("Response from Aidbox upsert: {}", response);

        searchService.upsertPatient(response);
        log.info("Patient upserted into Search Service.");

        return new ResponseEntity<>(
            new ResponseDTO<>(HttpStatus.OK.value(), "Patient upserted successfully", response),
            HttpStatus.OK);
    }

    // Search patients by partial name
    @Operation(summary = "Search patients by name", description = "Search patients by their partial name (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patients fetched successfully"),
        @ApiResponse(responseCode = "400", description = "Search name must have at least 3 characters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<List<PatientDTO>>> searchPatients(@RequestParam String name) {
        log.info("Request to search patient by name: {}", name);
        if (name.length() < 3) {
            log.warn("Search name must have at least 3 characters");
            return ResponseEntity.badRequest().body(
                new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), "Search name must have at least 3 characters", null));
        }

        List<PatientDTO> patients = searchService.searchPatientByName(name);
        return new ResponseEntity<>(
            new ResponseDTO<>(HttpStatus.OK.value(), "Patients fetched successfully", patients),
            HttpStatus.OK);
    }

    // Get all patients
    @Operation(summary = "Get all patients", description = "Fetches all patients from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patients fetched successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<List<PatientDTO>>> getAllPatients() {
        log.info("Request to get all patients");
        List<PatientDTO> patients = searchService.searchPatientByName(); //Assuming having a method that fetches all patients
        return new ResponseEntity<>(
            new ResponseDTO<>(HttpStatus.OK.value(), "Patients fetched successfully", patients),
            HttpStatus.OK);
    }

    // Get patient by ID
    @Operation(summary = "Retrieve a patient", description = "Get a patient by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient fetched successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid Patient ID provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<PatientDTO>> getPatient(@PathVariable String id) {
        log.info("Request to get patient with id: {}", id);
        PatientDTO patientResponse = patientService.getPatient(id);
        return new ResponseEntity<>(
            new ResponseDTO<>(HttpStatus.OK.value(), "Patient fetched successfully", patientResponse),
            HttpStatus.OK);
    }


    // Update patient by ID
    @Operation(summary = "Update a patient", description = "Update the details of a patient by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid Patient ID or request body provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<PatientDTO>> updatePatient(@PathVariable String id, @RequestBody PatientDTO patientDTO) {
        log.info("Request to update patient with id: {}", id);

        PatientDTO patientResponse = patientService.updatePatient(id, patientDTO);

        searchService.upsertPatient(patientResponse);
        log.info("Patient upserted into Search Service.");
        return new ResponseEntity<>(
            new ResponseDTO<>(HttpStatus.OK.value(), "Patient updated successfully", patientResponse),
            HttpStatus.OK);
    }

    // Delete patient by ID
    @Operation(summary = "Delete a patient", description = "Delete a patient by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid Patient ID provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<PatientDTO>> deletePatient(@PathVariable String id) {
        log.info("Request to get patient with id: {}", id);
        PatientDTO patientResponse = patientService.deletePatient(id);

        searchService.deletePatient(patientResponse.getId());
        log.info("Patient deleted from Search Service.");
        return new ResponseEntity<>(
            new ResponseDTO<>(HttpStatus.OK.value(), "Patient deleted successfully", patientResponse),
            HttpStatus.OK);
    }

}
