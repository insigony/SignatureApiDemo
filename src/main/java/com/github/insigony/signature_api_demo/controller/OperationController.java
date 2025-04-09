package com.github.insigony.signature_api_demo.controller;

import com.github.insigony.signature_api_demo.config.SignatureProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/operations")
public class OperationController {
    private final SignatureProperties signatureProperties;

    public OperationController(SignatureProperties signatureProperties) {
        this.signatureProperties = signatureProperties;
    }

    @PostMapping("/{operationId}")
    public ResponseEntity<Map<String, Object>> processForm(
            @PathVariable String operationId,
            @RequestParam MultiValueMap<String, String> formParams
    ) {

        TreeMap<String, String> sortedParams = new TreeMap<>();
        formParams.forEach((key, values) -> sortedParams.put(key, values.get(0)));

        StringBuilder data = new StringBuilder();
        sortedParams.forEach((k, v) -> {
            if (data.length() > 0) data.append("&");
            data.append(k).append("=").append(v);
        });
        String signature = hmacSha256(data.toString(), signatureProperties.getSecret());

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("result", List.of(Map.of("signature", signature)));

        return ResponseEntity.ok(result);
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(keySpec);
            byte[] hashBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error with HMAC", e);
        }
    }
}