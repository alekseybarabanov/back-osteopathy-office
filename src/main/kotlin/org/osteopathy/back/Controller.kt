package org.osteopathy.back

import org.hibernate.SessionFactory
import org.osteopathy.back.dto.Visit
import org.osteopathy.back.entities.Patient
import org.osteopathy.back.mapper.PatientMapper
import org.osteopathy.back.repositories.PatientRepository
import org.osteopathy.back.repositories.VisitRepository
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
class Controller(
    private val patientRepository: PatientRepository,
    private val visitRepository: VisitRepository,
    private val sessionFactory: SessionFactory,
    private val mapper: PatientMapper
) {

    @PostMapping("/api/patient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addPatient(@RequestBody patient: org.osteopathy.back.dto.Patient): org.osteopathy.back.dto.Patient? {
        if (patient.id != null) {
            throw IllegalArgumentException("cannot add patient with existing id")
        }
        val entityPatient = mapper.dtoToEntity(patient)
        entityPatient.visits?.forEach { visitRepository.save(it) }
        patientRepository.save(entityPatient)
        patient.id = entityPatient.id

        return mapper.entityToDto(entityPatient)
    }

    @PutMapping("/api/patient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePatient(@RequestBody patient: org.osteopathy.back.dto.Patient): org.osteopathy.back.dto.Patient? {
        if (patient.id == null) {
            throw IllegalArgumentException("cannot update patient without id")
        }
        if (patient.currentVisit != null) {
            if (patient.visits == null) {
                patient.visits = Collections.singletonList(patient.currentVisit)
            } else {
                if (patient.currentVisit!!.id == null) {
                    patient.visits!!.add(patient.currentVisit!!)
                } else {
                    val ind = patient.visits!!.indexOfFirst { it.id == patient.currentVisit!!.id }
                    patient.visits!![ind] = patient.currentVisit!!
                }
            }
        }
        val entityPatient = mapper.dtoToEntity(patient)
        entityPatient.visits?.forEach { visitRepository.save(it) }
        patientRepository.save(entityPatient)

        return mapper.entityToDto(entityPatient)
    }

    @GetMapping("/api/patient/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPatient(@PathVariable("id") id: Long): org.osteopathy.back.dto.Patient {
        val entityPatient = patientRepository.findById(id).orElseThrow { IllegalArgumentException("Invalid id: $id") }

        return mapper.entityToDto(entityPatient)
    }

    @GetMapping("/api/patient/search")
    fun findByName(@RequestParam("name") name: String): List<org.osteopathy.back.dto.Patient> {
        val result = ArrayList<Patient>()
        result.addAll(patientRepository.findByFirstNameOrMiddleNameOrLastName(name))

        return result.map { mapper.entityToDto(it) }
    }

    @GetMapping("/api/patient/latest")
    fun latestPatients(): List<org.osteopathy.back.dto.Patient> {
        return patientRepository.latestPatients().map { mapper.entityToDto(it) }
    }
}