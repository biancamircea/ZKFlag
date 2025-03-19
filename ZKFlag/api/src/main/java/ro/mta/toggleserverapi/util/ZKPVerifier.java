package ro.mta.toggleserverapi.util;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class ZKPVerifier {
    private static final String VERIFY_SCRIPT = "zkp/verify_proof.js";
    private static final String NODE_MODULES_PATH = "src/main/resources/zkp/node_modules";
    private static final String VERIFICATION_KEY_PATH = "zkp/verification_key.json";

    private Path lastPublicJsonPath;

    public boolean verifyProof(String proofJson) throws Exception {
        Path tempDir = Files.createTempDirectory("zkp_verify");

        String proof = extractJsonField(proofJson, "proof");
        String publicSignals = extractJsonField(proofJson, "publicSignals");

        Path proofFile = tempDir.resolve("proof.json");
        lastPublicJsonPath = tempDir.resolve("public.json"); 

        Files.write(proofFile, proof.getBytes());
        Files.write(lastPublicJsonPath, publicSignals.getBytes());

        ProcessBuilder pb = new ProcessBuilder(
                "node",
                getResourcePath(VERIFY_SCRIPT),
                getResourcePath(VERIFICATION_KEY_PATH),
                proofFile.toString(),
                lastPublicJsonPath.toString()
        );

        return executeProcess(pb);
    }

    public Path getLastPublicJsonPath() {
        return lastPublicJsonPath;
    }

    private String extractJsonField(String json, String field) {
        int startIndex = json.indexOf("\"" + field + "\":") + field.length() + 3;
        int endIndex = json.indexOf("}", startIndex) + 1;
        return json.substring(startIndex, endIndex);
    }

    private String getResourcePath(String resource) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(resource);
        Path tempFile = Files.createTempFile("zkp_", "_temp");
        Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile.toAbsolutePath().toString();
    }

    private boolean executeProcess(ProcessBuilder pb) throws IOException, InterruptedException {
        Map<String, String> env = pb.environment();
        env.put("NODE_PATH", NODE_MODULES_PATH);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[ZKP] " + line);
                if (line.equals("valid")) {
                    return true;
                }
            }
        }

        int exitCode = process.waitFor();
        return exitCode == 0;
    }
}

