package com.follysitou.analytics_service.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;


@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumerEvent(byte[] event) throws InvalidProtocolBufferException {

        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            // ... perform any business related to analytics here

            log.info("Received Patient Event: [PatientId={}, PatientName={}, PatientEmail={}]",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail());

        } catch (Exception e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }
}
