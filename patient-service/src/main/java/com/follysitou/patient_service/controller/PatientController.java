package com.follysitou.patient_service.controller;

import com.follysitou.patient_service.dto.PatientRequestDto;
import com.follysitou.patient_service.dto.PatientResponseDto;
import com.follysitou.patient_service.dto.validators.CreatePatientValidationGroup;
import com.follysitou.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "API for managing Patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get all Patients")
    public ResponseEntity<List<PatientResponseDto>> getPatients() {
        List<PatientResponseDto> patients = patientService.getPatient();

        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    @Operation(summary = "Create a new Patient")
    public ResponseEntity<PatientResponseDto> createPatient(
            @Validated({Default.class, CreatePatientValidationGroup.class})
            @RequestBody PatientRequestDto patientRequestDto) {

        PatientResponseDto patientResponseDto = patientService.createPatient(
                patientRequestDto);

        return ResponseEntity.ok().body(patientResponseDto);

    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Patient")
    public ResponseEntity<PatientResponseDto> updatePatient(@PathVariable UUID id, @Validated({Default.class})
                                                                @RequestBody PatientRequestDto patientRequestDto) {

        PatientResponseDto patientResponseDto = patientService.updatePatient(id, patientRequestDto);

        return ResponseEntity.ok().body(patientResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing Patients")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }
}
