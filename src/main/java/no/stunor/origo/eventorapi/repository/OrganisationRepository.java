package no.stunor.origo.eventorapi.repository;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.firestore.Organisation;

@Repository
public interface OrganisationRepository extends FirestoreReactiveRepository<Organisation> {
    Organisation findByOrganisationIdAndEventor(String organisationId, String eventor);

}