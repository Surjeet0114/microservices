package com.surjeet.paymentservice.repository;

import com.surjeet.paymentservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, Long> {

    Optional<ProcessedEvent> findByEventId(Long eventId);

    boolean existsByEventId(Long eventId);
}