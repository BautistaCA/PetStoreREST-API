package pet.store.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import pet.store.controller.model.PetStoreData;
import pet.store.entity.PetStore;

public interface PetStoreDao extends JpaRepository<PetStore, Long> {

	//did not work for findbyCustomerId, Unsatisfied dependency
	//	Set<PetStore> findAllPetStoresIn(Set<PetStore> petStores);

//	Optional<PetStoreData> findByPetStoreId(Long petStoreId);
//ended up not needing this
}
