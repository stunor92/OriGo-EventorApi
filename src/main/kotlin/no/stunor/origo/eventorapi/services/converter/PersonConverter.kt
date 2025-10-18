package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.person.*

class PersonConverter {

    companion object {

        fun convertPerson(eventorPerson: org.iof.eventor.Person, eventor: Eventor): Person {
            return Person(
                eventorId = eventor.eventorId,
                personId = eventorPerson.personId.content,
                name = convertPersonName(eventorPerson.personName),
                birthYear = eventorPerson.birthDate.date.content.substring(0, 4).toInt(),
                nationality = eventorPerson.nationality.country.alpha3.value,
                gender = convertGender(eventorPerson.sex),
                mobilePhone = ContactConverter.convertPhone(eventorPerson.tele),
                email = ContactConverter.convertEmail(eventorPerson.tele),
                memberships = convertMemberships(eventorPerson.personId.content, eventor.eventorId, eventorPerson.role)
            )
        }

        fun convertGender(sex: String?): Gender {
            if (sex == null) {
                return Gender.Other
            }
            return when (sex) {
                "M" -> Gender.Man
                "F" -> Gender.Woman
                else -> Gender.Other
            }
        }

        fun convertPersonName(personName: org.iof.eventor.PersonName): PersonName {
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

        private fun convertMemberships(
            personId: String,
            eventorId: String,
            roles: List<org.iof.eventor.Role>
        ): List<Membership> {
            val highestRole: MutableMap<String, Int> = HashMap()

            for (role in roles) {
                if (role.roleTypeId.content == "2") {
                    role.roleTypeId.content = "10"
                }

                if (!highestRole.containsKey(role.organisationId.content)
                    || role.roleTypeId.content.toInt() > highestRole.getValue(role.organisationId.content)
                ) {
                    highestRole[role.organisationId.content] = role.roleTypeId.content.toInt()
                }
            }

            val memberships: MutableList<Membership> = mutableListOf()

            for (orgId in highestRole.keys.stream().toList()) {
                when {
                    highestRole[orgId] == 1 -> {
                        memberships.add(
                            Membership(
                                organisationId = orgId,
                                personId = personId,
                                eventorId = eventorId,
                                type = MembershipType.Member
                            )
                        )
                    }

                    highestRole[orgId] == 3 || highestRole[orgId] == 5 -> {
                        memberships.add(
                            Membership(
                                organisationId = orgId,
                                personId = personId,
                                eventorId = eventorId,
                                type = MembershipType.Organiser
                            )
                        )
                    }

                    highestRole[orgId] == 10 -> {
                        memberships.add(
                            Membership(
                                organisationId = orgId,
                                personId = personId,
                                eventorId = eventorId,
                                type = MembershipType.Admin
                            )
                        )
                    }
                }
            }


            return memberships
        }
    }
}
