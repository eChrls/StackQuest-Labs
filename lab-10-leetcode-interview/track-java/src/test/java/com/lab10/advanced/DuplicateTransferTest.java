package com.lab10.advanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DuplicateTransferTest {

    @Autowired
    private TransferRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    private ResponseEntity<Map> post(String idempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        if (idempotencyKey != null) {
            headers.set("Idempotency-Key", idempotencyKey);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("userId", "user-1");
        body.put("amount", new BigDecimal("100.00"));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity("/api/transfers", entity, Map.class);
    }

    @Test
    @Tag("public")
    void doubleSubmitCreatesASingleTransfer() {
        ResponseEntity<Map> first = post("idem-key-1");
        ResponseEntity<Map> second = post("idem-key-1");

        assertEquals(HttpStatus.CREATED, first.getStatusCode());
        assertEquals(HttpStatus.OK, second.getStatusCode());
        assertEquals(first.getBody().get("id"), second.getBody().get("id"));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @Tag("hidden")
    void concurrentDoubleSubmitCreatesASingleTransfer() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Callable<ResponseEntity<Map>>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> post("idem-race"));
        }

        List<Future<ResponseEntity<Map>>> futures = pool.invokeAll(tasks);
        pool.shutdown();

        Set<Object> ids = new HashSet<>();
        for (Future<ResponseEntity<Map>> future : futures) {
            ids.add(future.get().getBody().get("id"));
        }
        assertEquals(1, ids.size());

        long matching = 0;
        for (Transfer transfer : repository.findAll()) {
            if ("idem-race".equals(transfer.getIdempotencyKey())) {
                matching++;
            }
        }
        assertEquals(1, matching);
    }

    @Test
    @Tag("hidden")
    void differentIdempotencyKeysCreateTwoTransfers() {
        ResponseEntity<Map> first = post("key-a");
        ResponseEntity<Map> second = post("key-b");

        assertEquals(HttpStatus.CREATED, first.getStatusCode());
        assertEquals(HttpStatus.CREATED, second.getStatusCode());
        assertNotEquals(first.getBody().get("id"), second.getBody().get("id"));
        assertEquals(2, repository.findAll().size());
    }

    @Test
    @Tag("hidden")
    void missingIdempotencyKeyIsRejected() {
        ResponseEntity<Map> response = post(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
