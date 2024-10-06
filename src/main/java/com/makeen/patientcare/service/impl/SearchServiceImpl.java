package com.makeen.patientcare.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeen.patientcare.dto.PatientDTO;
import com.makeen.patientcare.service.ISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements ISearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final ObjectMapper objectMapper;

    @Override
    public void upsertPatient(PatientDTO patientDTO) {
        log.info("Upserting patient into Elasticsearch: {}", patientDTO);

        try {
            // Build the index request with the new API
            IndexRequest<PatientDTO> request = IndexRequest.of(i -> i
                .index("patients") // Index name
                .id(String.valueOf(patientDTO.getId())) // Document ID
                .document(patientDTO) // PatientDTO as the document body
            );

            // Perform the upsert operation
            IndexResponse response = elasticsearchClient.index(request);

            log.info("Successfully upserted patient with ID: {}", response.id());
            log.info("Upsert result: {}", response.result().jsonValue());
        } catch (IOException e) {
            log.error("Failed to upsert patient into Elasticsearch", e);
        }
    }

    @Override
    public void deletePatient(String patientId) {
        log.info("Deleting patient from Elasticsearch with ID: {}", patientId);

        try {
            // Build the delete request
            DeleteRequest request = DeleteRequest.of(d -> d
                .index("patients") // Index name
                .id(patientId) // Document ID (patient ID)
            );

            // Perform the delete operation
            DeleteResponse response = elasticsearchClient.delete(request);

            log.info("Successfully deleted patient with ID: {}", response.id());
        } catch (Exception e) {
            log.error("Failed to delete patient from Elasticsearch", e);
        }
    }

    @Override
    public List<PatientDTO> searchPatientByName() {
        return searchPatientByName(null);
    }

    @Override
    public List<PatientDTO> searchPatientByName(String name) {
        log.info("Searching for patients with name: {}", name);
        try {
            SearchRequest searchRequest;
            // Check if name is null or empty
            if (name == null || name.isEmpty()) {
                // Build search request without query to return all data
                searchRequest = SearchRequest.of(s -> s.index("patients").size(100));
            } else {
                // Build the search request with match_phrase_prefix query for partial matching
                searchRequest = SearchRequest.of(s -> s
                    .index("patients")
                    .query(q -> q
                        .matchPhrasePrefix(m -> m
                            .field("name")
                            .query(name)
                        )
                    )
                    // Add size parameter with a number greater than 10 here
                    .size(100)  // customize this value as per your needs.
                );
            }
            // Execute the search request
            SearchResponse<PatientDTO> searchResponse = elasticsearchClient.search(searchRequest, PatientDTO.class);
            log.info("Elasticsearch response: {}", searchResponse);
            // Collect results from the search response
            List<PatientDTO> patients = searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();
            if (name == null || name.isEmpty()) {
                log.info("Found {} total patients", patients.size());
            } else {
                log.info("Found {} patients with the name {}", patients.size(), name);
            }
            return patients;
        } catch (IOException e) {
            log.error("Failed to search patients in Elasticsearch", e);
            return new ArrayList<>();
        }
    }
}
