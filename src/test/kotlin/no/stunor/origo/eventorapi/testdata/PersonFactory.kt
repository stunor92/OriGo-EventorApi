package no.stunor.origo.eventorapi.testdata

import no.stunor.origo.eventorapi.model.person.*
import java.util.UUID

class PersonFactory {
    companion object {
        fun createTestPerson(): Person {
            return Person(
                id = UUID.randomUUID(),
                eventorId = "NOR",
                eventorRef = "123",
                name = PersonName(
                    given = "Ola",
                    family = "Nordmann",
                ),
                birthYear = 2000,
                nationality = "NOR",
                gender = Gender.Man,
                mobilePhone = "+4712345678",
                email = "test@test.no",
                memberships = mutableListOf(
                    Membership(
                        id = MembershipKey(
                            personId = UUID.randomUUID(),
                            organisationId = UUID.randomUUID()
                        ),
                        type = MembershipType.Admin
                    ),
                    Membership(
                        id = MembershipKey(
                            personId = UUID.randomUUID(),
                            organisationId = UUID.randomUUID()
                        ),
                        type = MembershipType.Organiser
                    )
                ),
            )
        }
    }
}