package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.person.Competitor
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.MembershipType
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.model.person.PersonName

object PersonConverter {
    @JvmStatic
    fun convertPerson(eventorPerson: org.iof.eventor.Person, eventor: Eventor): Person {
        return Person(
                null,
                eventor.eventorId,
                eventorPerson.personId.content,
                convertPersonName(eventorPerson.personName),
                eventorPerson.birthDate.date.content.substring(0, 4).toInt(),
                eventorPerson.nationality.country.alpha3.value,
                convertGender(eventorPerson.sex),
                ArrayList(),
                ContactConverter.convertPhone(eventorPerson.tele),
                ContactConverter.convertEmail(eventorPerson.tele),
                convertMemberships(eventorPerson.role)
        )
    }

    @JvmStatic
    fun convertCompetitor(person: org.iof.eventor.Person, eventor: Eventor): Competitor {
        return Competitor(
                eventor.eventorId,
                person.personId.content,
                convertPersonName(person.personName),
                person.birthDate.date.content.substring(0, 4).toInt(),
                person.nationality.country.alpha3.value,
                convertGender(person.sex)
        )
    }

    private fun convertGender(sex: String?): Gender {
        if (sex == null) {
            return Gender.OTHER
        }
        return when (sex) {
            "M" -> Gender.MAN
            "F" -> Gender.WOMAN
            else -> Gender.OTHER
        }
    }

    private fun convertPersonName(personName: org.iof.eventor.PersonName): PersonName {
        val given = StringBuilder()
        for (i in personName.given.indices) {
            for (j in 1..personName.given.size) {
                if (personName.given[i].sequence.toInt() == j) {
                    if (given.toString() != "") {
                        given.append(" ")
                    }
                    given.append(personName.given[i].content)
                }
            }
        }
        return PersonName(personName.family.content, given.toString())
    }

    private fun convertMemberships(roles: List<org.iof.eventor.Role>): Map<String, MembershipType> {
        val highestRole: MutableMap<String, Int> = HashMap()

        for (role in roles) {
            if (role.roleTypeId.content == "2") {
                role.roleTypeId.content = "10"
            }

            if (!highestRole.containsKey(role.organisationId.content)) {
                highestRole[role.organisationId.content] = role.roleTypeId.content.toInt()
            } else if (role.roleTypeId.content.toInt() > highestRole[role.organisationId.content]!!) {
                highestRole[role.organisationId.content] = role.roleTypeId.content.toInt()
            }
        }

        val memberships: MutableMap<String, MembershipType> = HashMap()

        for (orgId in highestRole.keys.stream().toList()) {
            if (highestRole[orgId] == 1) {
                memberships[orgId] = MembershipType.MEMBER
            } else if (highestRole[orgId] == 3 || highestRole[orgId] == 5) {
                memberships[orgId] = MembershipType.ORGANISER
            } else if (highestRole[orgId] == 10) {
                memberships[orgId] = MembershipType.ADMIN
            }
        }


        return memberships
    }
}
