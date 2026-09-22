package com.surjeet.paymentservice.controller;

import com.surjeet.paymentservice.event.OrderCreatedEvent;
import com.surjeet.paymentservice.service.DltRecoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments/dlt")
public class DltRecoveryController {

    private final DltRecoveryService dltRecoveryService;

    public DltRecoveryController(
            DltRecoveryService dltRecoveryService
    ) {
        this.dltRecoveryService = dltRecoveryService;
    }

    @PostMapping("/reprocess")
    public ResponseEntity<String> reprocess(
            @RequestBody OrderCreatedEvent event
    ) throws Exception {

        dltRecoveryService.reprocess(event);

        return ResponseEntity.ok(
                "Event sent for reprocessing: "
                        + event.eventId()
        );
    }
}