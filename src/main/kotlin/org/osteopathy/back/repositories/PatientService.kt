package org.osteopathy.back.repositories

import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import jakarta.transaction.Transactional
import org.osteopathy.back.entities.Patient
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.sql.Timestamp

interface PatientService {
    fun createPatient(entityPatient: Patient, tenant: String?): Int
    fun updatePatient(entityPatient: Patient, tenant: String?)
    fun readPatient(id: Long, doAudit: Boolean, tenant: String?): Patient
    fun latestPatients(tenant: String?): List<Patient>
}

abstract class AbstractPatientService (
    private val patientRepository: PatientRepository,
    private val visitRepository: VisitRepository,
    private val audit: AuditService,
    private val em: EntityManager
): PatientService {
    @Transactional
    override fun createPatient(entityPatient: Patient, tenant: String?): Int {
        entityPatient.tenant = tenant
        entityPatient.visits?.forEach { visitRepository.save(it.also { it.tenant = tenant }) }
        patientRepository.save(entityPatient)
        audit.createPatient(entityPatient.id!!, tenant)

        return entityPatient.id!!
    }

    @Transactional
    override fun updatePatient(entityPatient: Patient, tenant: String?) {
        entityPatient.tenant = tenant
        entityPatient.visits?.forEach { visitRepository.save(it.also { it.tenant = tenant }) }
        patientRepository.save(entityPatient)
    }

    @Transactional
    override fun readPatient(id: Long, doAudit: Boolean, tenant: String?): Patient {
        val entityPatient = patientRepository.findById(id).orElseThrow { IllegalArgumentException("Invalid id: $id") }
        if (entityPatient.tenant != tenant) {
            throw IllegalArgumentException("patient $id tenant doen't match required tenant $tenant")
        }
        if (doAudit) {
            audit.enterPatient(entityPatient.id!!, tenant)
        }

        return entityPatient
    }

    protected abstract fun latestPatientsQuery(tenant: String?): Query

    override fun latestPatients(tenant: String?): List<Patient> {
        val q = latestPatientsQuery(tenant)
        return q.resultList.map {
            val rs = it as Array<Any>
            Patient().let {
                var ind = 0
                it.id = rs[ind++] as Int
                it.tenant = rs[ind++] as String?
                it.description = rs[ind++] as String?
                it.firstName = rs[ind++] as String?
                it.lastName = rs[ind++] as String?
                it.middleName = rs[ind++] as String?
                it.phone = rs[ind++] as String?
                it.birthDate = rs[ind++] as String?
                it.nextVisit = rs[ind++] as Timestamp?

                it
            }
        }
    }
}

@Service
@Profile("h2")
class H2PatientService(
    val patientRepository: PatientRepository,
    val visitRepository: VisitRepository,
    val audit: AuditService,
    val em: EntityManager
): AbstractPatientService(patientRepository, visitRepository, audit, em) {
    override fun latestPatientsQuery(tenant: String?): Query {
        return em.createNativeQuery("select p.* from (select top 10 patient_id as pid, max(id) as id from Audit group by patient_id order by id desc) a join patient p on a.pid = p.id order by a.id desc")
    }

}

@Service
@Profile("pg")
class PgPatientService(
    val patientRepository: PatientRepository,
    val visitRepository: VisitRepository,
    val audit: AuditService,
    val em: EntityManager
): AbstractPatientService(patientRepository, visitRepository, audit, em) {
    override fun latestPatientsQuery(tenant: String?): Query {
        val query = em.createNativeQuery("select p.id, p.tenant, p.description, p.first_name, p.last_name, " +
                "p.middle_name, p.phone, p.birth_date, p.next_visit " +
                "from (select patient_id as pid, max(id) as id " +
                "from Audit where tenant = ? group by patient_id order by id desc limit 10) a " +
                "join patient p on a.pid = p.id order by a.id desc")
        query.setParameter(0, tenant)

        return query
    }

}
