package org.osteopathy.back.repositories

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.osteopathy.back.entities.Patient
import org.springframework.stereotype.Service
import java.sql.Timestamp

interface PatientService {
    fun createPatient(entityPatient: Patient): Long
    fun updatePatient(entityPatient: Patient)
    fun readPatient(id: Long, doAudit: Boolean): Patient
    fun latestPatients(): List<Patient>
}

@Service
class PatientServiceImpl(
    private val patientRepository: PatientRepository,
    private val visitRepository: VisitRepository,
    private val audit: AuditService,
    private val em: EntityManager
): PatientService {
    @Transactional
    override fun createPatient(entityPatient: Patient): Long {
        entityPatient.visits?.forEach { visitRepository.save(it) }
        patientRepository.save(entityPatient)
        audit.createPatient(entityPatient.id!!)

        return entityPatient.id!!
    }

    @Transactional
    override fun updatePatient(entityPatient: Patient) {
        entityPatient.visits?.forEach { visitRepository.save(it) }
        patientRepository.save(entityPatient)
    }

    @Transactional
    override fun readPatient(id: Long, doAudit: Boolean): Patient {
        val entityPatient = patientRepository.findById(id).orElseThrow { IllegalArgumentException("Invalid id: $id") }
        if (doAudit) {
            audit.enterPatient(entityPatient.id!!)
        }

        return entityPatient
    }

    override fun latestPatients(): List<Patient> {
        val q = em.createNativeQuery("select p.* from (select top 10 patient_id as pid, max(id) as id from Audit group by patient_id order by id desc) a join patient p on a.pid = p.id order by a.id desc")
        return q.resultList.map {
            val rs = it as Array<Any>
            Patient().let {
                var ind = 0
                it.id = rs[ind++] as Long
                it.description = rs[ind++] as String?
                it.firstName = rs[ind++] as String?
                it.lastName = rs[ind++] as String?
                it.middleName = rs[ind++] as String?
                it.phone = rs[ind++] as String?
                it.birthDate = rs[ind++] as String?
                it.nextVisit = rs[ind++] as Timestamp

                it
            }
        }
    }
}