package sae.semestre.six.domain.medicalRecord;

import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_number", unique = true, nullable = false)
    private String recordNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "treatment")
    private String treatment;

    @Column(name = "prescription")
    private String prescription;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "record_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordDate;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "blood_pressure")
    private String bloodPressure;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "weight")
    private Float weight;

    
    public MedicalRecord() {
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }


} 