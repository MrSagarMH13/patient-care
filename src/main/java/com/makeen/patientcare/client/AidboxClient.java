package com.makeen.patientcare.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeen.patientcare.dto.PatientDTO;
import com.makeen.patientcare.service.ITokenService;
import com.makeen.patientcare.utils.FhirConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AidboxClient {
    private final RestTemplate restTemplate;
    private final FhirConverter fhirConverter;
    private final ITokenService tokenService;

    @Value("${aidbox.base-url}")
    private String baseUrl;

    @Value("${aidbox.fhir.patient-endpoint}")
    private String fhirPatiendEndpoint;

    // Create or Update patient in Aidbox
    public PatientDTO createPatient(PatientDTO patientDTO) {
        log.info("Creating a patient");
        // Convert PatientDTO to FHIR-compliant format
        Map<String, Object> fhirPatientData = fhirConverter.convertToFhirPatient(patientDTO);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(fhirPatientData);
            log.debug("FHIR format data :: {}", json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenService.generateToken()); // Use the OAuth2 token

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(fhirPatientData, headers);

        try {
            log.info("Exchanging data with the server");
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + fhirPatiendEndpoint, // Ensure the correct endpoint
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Patient creation successful");
                return fhirConverter.convertFhirPatientToPatientDTO(response.getBody());

            } else {
                throw new RuntimeException("Failed to create patient: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("Error during patient creation: {}", e.getResponseBodyAsString());
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Retrieve patient by id
    public PatientDTO getPatient(String id) {
        log.info("Fetching patient with id {}", id);
        String url = baseUrl + fhirPatiendEndpoint + "/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenService.generateToken());

        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, PatientDTO.class).getBody();
    }

    // Delete patient by id
    public Void deletePatient(String id) {
        log.info("Deleting patient with id {}", id);
        String url = baseUrl + fhirPatiendEndpoint + "/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenService.generateToken());

        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class).getBody();
    }
}
