package org.osteopathy.back.entities
import jakarta.persistence.*
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.util.Date

@Entity
@Table(name="patient")
class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_id_seq")
    @SequenceGenerator(name="patient_id_seq", sequenceName = "patient_id_seq", allocationSize = 1)
    var id: Int? = null

    var tenant: String? = null

    var firstName: String? = null

    var lastName: String? = null

    var middleName: String? = null

    var phone: String? = null

    var birthDate: String? = null

    var description: String? = null

    var nextVisit: Timestamp? = null

    @OneToMany
    @JoinColumn(name = "visit_id")
    var visits: List<Visit>? = null
}

@Entity
@Table(name="visit")
class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visit_id_seq")
    @SequenceGenerator(name="visit_id_seq", sequenceName = "visit_id_seq", allocationSize = 1)
    var id: Int? = null

    var tenant: String? = null

    var visitDate: Timestamp = Timestamp(Date().time)

    var complaints: String? = null
    var anamnesis: String? = null
    var globBio: Int? = null
    var globRithmKarnial: Int? = null
    var globRithmKardio: Int? = null
    var globRightBreath: Int? = null
    var globNeiroPsihosomat: Int? = null
    var globNeiroPostural: Int? = null
    var regionHeadStruct: Int? = null
    var regionNeckStruct: Int? = null
    var regionNeckVistz: Int? = null
    var regionHandsStruct: Int? = null
    var regionBrestStruct: Int? = null
    var regionBrestVistz: Int? = null
    var regionLowerBackStruct: Int? = null
    var regionLowerBackVistz: Int? = null
    var regionPelvicStruct: Int? = null
    var regionPelvicVistz: Int? = null
    var regionLegsStruct: Int? = null
    var regionDuraMaterStruct: Int? = null
    var regionCr: Int? = null
    var regionC1C3Vistz: Int? = null
    var regionC1C3Som: Int? = null
    var regionC4C6Vistz: Int? = null
    var regionC4C6Som: Int? = null
    var regionC7Th1Vistz: Int? = null
    var regionC7Th1Som: Int? = null
    var regionTh2Th5Vistz: Int? = null
    var regionTh2Th5Som: Int? = null
    var regionTh6Th9Vistz: Int? = null
    var regionTh6Th9Som: Int? = null
    var regionTh10L1Vistz: Int? = null
    var regionTh10L1Som: Int? = null
    var regionL2L5Vistz: Int? = null
    var regionL2L5Som: Int? = null
    var localDisfunction: String? = null
    var dominant: String? = null
    var treatmentPlan: String? = null
    var recommendations: String? = null
    var specialists: String? = null
}

@Entity
@Table(name="audit")
class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_id_seq")
    @SequenceGenerator(name="audit_id_seq", sequenceName = "audit_id_seq", allocationSize = 1)
    var id: Int? = null

    var tenant: String? = null

    var tm: OffsetDateTime = OffsetDateTime.now()

    var recType: String? = null

    var patientId: Int? = null

}
