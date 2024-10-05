package com.makeen.patientcare.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeen.patientcare.dto.PatientDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FhirConverter {
    public Map<String, Object> convertToFhirPatient(PatientDTO patientDTO) {
        Map<String, Object> fhirPatient = new HashMap<>();

        // Setting resource type
        fhirPatient.put("resourceType", "Patient");

        // Setting patient ID (if available)
        if (patientDTO.getId() != null) {
            fhirPatient.put("id", patientDTO.getId());
        }

        // Split the name into given and family (assumes "First Last" format)
        String[] nameParts = patientDTO.getName().split(" ");
        String familyName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
        String givenName = nameParts.length > 1 ? nameParts[0] : patientDTO.getName();

        // Name section
        Map<String, Object> name = new HashMap<>();
        name.put("use", "official");
        name.put("family", familyName);
        name.put("given", new String[]{givenName});
        fhirPatient.put("name", new Map[]{name});  // FHIR expects an array of names

        // Gender section
        if (patientDTO.getGender() != null) {
            fhirPatient.put("gender", patientDTO.getGender().toLowerCase());
        }

        // Birthdate section
        if (patientDTO.getBirthdate() != null) {
            fhirPatient.put("birthDate", patientDTO.getBirthdate());
        }

        // Telecom section (for phone)
        Map<String, Object> telecom = new HashMap<>();
        telecom.put("system", "phone");
        telecom.put("value", patientDTO.getPhone());
        telecom.put("use", "mobile");
        fhirPatient.put("telecom", new Map[]{telecom});  // FHIR expects an array of telecoms

        // You can add more fields as necessary, such as address, etc.

        return fhirPatient;
    }

    public PatientDTO convertFhirPatientToPatientDTO(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(rootNode.get("id").asText());
        patientDTO.setGender(rootNode.get("gender").asText());
        patientDTO.setBirthdate(rootNode.get("birthDate").asText());
        // Assuming name is the concatenation of given and family
        String name = rootNode.get("name").get(0).get("given").get(0).asText() + " "
            + rootNode.get("name").get(0).get("family").asText();
        patientDTO.setName(name);

        // Fetch phone value from telecom where system is "phone"
        JsonNode telecoms = rootNode.get("telecom");
        for (JsonNode telecom : telecoms) {
            if (telecom.get("system").asText().equals("phone")) {
                patientDTO.setPhone(telecom.get("value").asText());
                break;
            }
        }
        return patientDTO;
    }
}
