package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.organisation.OrganisationType
import no.stunor.origo.eventorapi.model.person.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled("JPA test - needs migration to JDBC")
@SpringBootTest
@ActiveProfiles("test")
open class MembershipPersistenceTest {

    @Autowired lateinit var personRepository: PersonRepository
    @Autowired lateinit var organisationRepository: OrganisationRepository
    @Autowired lateinit var membershipRepository: MembershipRepository

    @Test
    fun `persist person with membership cascade`() {
        val organisation = organisationRepository.save(
            Organisation(eventorId = "NOR", eventorRef = "123", name = "Test Org", type = OrganisationType.Club, country = "NOR")
        )
        val person = Person(eventorId = "NOR", eventorRef = "999", name = PersonName("Family","Given"), birthYear = 1990, nationality = "NOR")
        val membership = Membership(
            id = MembershipKey(personId = person.id, organisationId = organisation.id),
            person = person,
            organisation = organisation,
            type = MembershipType.Admin
        )
        person.memberships.add(membership)
        personRepository.save(person)

        // val fetched = membershipRepository.findById(membership.id)
        // assertNotNull(fetched.orElse(null))
    }
}

