package com.follysitou.patient_service.service;

import com.follysitou.patient_service.dto.PatientRequestDto;
import com.follysitou.patient_service.dto.PatientResponseDto;
import com.follysitou.patient_service.exceptions.EmailAlreadyExistsException;
import com.follysitou.patient_service.exceptions.PatientNotFoundException;
import com.follysitou.patient_service.grpc.BillingServiceGrpcClient;
import com.follysitou.patient_service.kafka.KafkaProducer;
import com.follysitou.patient_service.mappers.PatientMapper;
import com.follysitou.patient_service.models.Patient;
import com.follysitou.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    private final BillingServiceGrpcClient billingServiceGrpcClient;

    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDto> getPatient() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        if(patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists : "
                    + patientRequestDto.getEmail());
        }

        Patient newPatient = patientRepository.save(
                PatientMapper.toModel(patientRequestDto));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getName(),
                                                              newPatient.getEmail() );

        kafkaProducer.sendEvent(newPatient);

        return PatientMapper.toDto(newPatient);
    }

    public PatientResponseDto updatePatient(UUID patientId, PatientRequestDto patientRequestDto) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new
                        PatientNotFoundException("Patient not found with ID: " + patientId));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), patientId)) {
            throw new EmailAlreadyExistsException("A patient with this email already exists : "
                    + patientRequestDto.getEmail());
        }

        patient.setName(patientRequestDto.getName());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
