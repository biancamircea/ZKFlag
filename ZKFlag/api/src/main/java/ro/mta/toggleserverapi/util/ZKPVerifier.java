package ro.mta.toggleserverapi.util;

import java.io.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class ZKPVerifier {
    private static final String SERVER_URL_NORMAL = "http://localhost:4000/verifyProof";
    private static final String SERVER_URL_LOCATION = "http://localhost:4000/verifyLocationProof";
    private static final String PROOF_FILE_PATH = "src/main/resources/zkp/proof.json";
    private static final String PUBLIC_SIGNALS_FILE_PATH = "src/main/resources/zkp/publicSignals.json";

    public boolean verifyProof(JsonNode combinedJson,String type) throws Exception {
        System.out.println("Verifying proof...");

        JsonNode proofNode = combinedJson.get("proof");
        JsonNode publicSignalsNode = combinedJson.get("publicSignals");

        if (proofNode == null || publicSignalsNode == null) {
            throw new IllegalArgumentException("Invalid JSON: Missing 'proof' or 'publicSignals' fields");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String proofJson = objectMapper.writeValueAsString(proofNode);
        String publicSignalsJson = objectMapper.writeValueAsString(publicSignalsNode);

        writeToFile(proofJson, PROOF_FILE_PATH);
        writeToFile(publicSignalsJson, PUBLIC_SIGNALS_FILE_PATH);

        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.set("proof", proofNode);
        requestPayload.set("publicSignals", publicSignalsNode);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestPayload), headers);

        ResponseEntity<String> response;
        if(type.equals("normal")) {
            response = restTemplate.exchange(SERVER_URL_NORMAL, HttpMethod.POST, entity, String.class);
        } else {
            response = restTemplate.exchange(SERVER_URL_LOCATION, HttpMethod.POST, entity, String.class);
        }

        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        System.out.println("Proof verification result: " + jsonResponse.get("isValid").asBoolean());
        return jsonResponse.get("isValid").asBoolean();
    }

    private void writeToFile(String jsonContent, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(jsonContent);
        }
    }
}