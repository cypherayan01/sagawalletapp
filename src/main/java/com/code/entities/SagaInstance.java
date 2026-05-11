package com.code.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saga_instance")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="status" , nullable = false)
    private SagaStatus status = SagaStatus.STARTED;


    @Column(name="context", columnDefinition = "TEXT")
    private String context;

    @Column(name="current_step", nullable = false)
    private String currentStep;

    

}
