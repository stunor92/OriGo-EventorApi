package no.stunor.origo.eventorapi.testdata

import no.stunor.origo.eventorapi.model.person.*

class PersonFactory {
    companion object {
        fun createTestPerson(): Person {
            return Person(
                eventorId = "NOR",
                personId = "123",
                name = PersonName(
                    given = "Ola",
                    family = "Nordmann",
                ),
                birthYear = 2000,
                nationality = "NOR",
                gender = Gender.Man,
                mobilePhone = "+4712345678",
                email = "test@test.no",
                memberships = listOf(
                    Membership(
                        eventorId = "NOR",
                        personId = "123",
                        organisationId = "141",
                        type = MembershipType.Admin
                    ),
                    Membership(
                        eventorId = "NOR",
                        personId = "123",
                        organisationId = "8",
                        type = MembershipType.Organiser
                    )
                ),
            )
        }
    }
}