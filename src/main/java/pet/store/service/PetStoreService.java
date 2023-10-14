package pet.store.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {
	@Autowired
	private PetStoreDao petStoreDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CustomerDao customerDao;

	public PetStoreData savePetStore(PetStoreData petStoreData) {
		Long petStoreId = petStoreData.getPetStoreId();
		PetStore petStore = findOrCreatePetStore(petStoreId);
		copyPetStoreFields(petStore, petStoreData);

		return new PetStoreData(petStoreDao.save(petStore));
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		// -----Didn't realize I didn't need this at first-----
		// petStore.setPetStoreId(petStoreData.getPetStoreId());
		// ----------------------------------------------------
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());

	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		PetStore petStore;
		if (Objects.isNull(petStoreId)) {
			petStore = new PetStore();
		} else {
			petStore = findByPetStoreId(petStoreId);
		}
		return petStore;
	}

	private PetStore findByPetStoreId(Long petStoreId) {
		return petStoreDao.findById(petStoreId).orElseThrow(() -> new NoSuchElementException(
				"A Pet Store with an ID of " + petStoreId + " does not exist."));
	}

	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findByPetStoreId(petStoreId);
		Long employeeId = petStoreEmployee.getEmployeeId();
		Employee employee = findOrCreateEmployee(petStoreId, employeeId);

		copyEmployeeFields(employee, petStoreEmployee);

		employee.setPetStore(petStore);

		// Set<Employee> employees = new HashSet<>(); // This would remove other
		// employees.add(employee); // employees in the set
		// petStore.setEmployees(employees);
		petStore.getEmployees().add(employee);// not sure if this works, might need to work around
												// it

//-----If the above doesn't work, I'll use this instead------
// 		it looks super messy, I might just turn it into a method.
//		Set<Employee> employees = new HashSet<>();
//		employees.addAll(petStore.getEmployees());
//		employees.add(employee);
//		petStore.setEmployees(employees);
//-----------------------------------------------------------
		Employee dbEmployee = employeeDao.save(employee);
		return new PetStoreEmployee(dbEmployee);
	}

	private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeId(petStoreEmployee.getEmployeeId());
		employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());

	}

	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
		Employee employee;
		if (Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findByEmployeeId(petStoreId, employeeId);
		}

		return employee;
	}

	private Employee findByEmployeeId(Long petStoreId, Long employeeId) {
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException(
						"Valid Employee ID of" + employeeId + " Not Found"));
		if (employee.getPetStore().getPetStoreId() != petStoreId) {
			throw new IllegalArgumentException("Store ID " + petStoreId + " does not match");
		} else {
			return employee;
		}
	}

	@Transactional
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findByPetStoreId(petStoreId);
		Long customerId = petStoreCustomer.getCustomerId();
		Customer customer = findOrCreateCustomer(petStoreId, customerId);

		copyCustomerFields(customer, petStoreCustomer);

		customer.getPetStores().add(petStore);
		
		//------------------------------------------------------
		// Saved this setup I thought of for saveEmployee,
		// makes more sense to use it here even it's a little messy.
		// Most likely don't need to use it though.
//		Set<PetStore> petStores = new HashSet<>();
//		petStores.addAll(customer.getPetStores());
//		petStores.add(petStore);
//		customer.setPetStores(petStores);
		// -----------------------------------------------------

		petStore.getCustomers().add(customer);
		// Don't think I actually need create a new Customer for this,
		// but it's best to use it to be safe.
		Customer dbCustomer = customerDao.save(customer);
		return new PetStoreCustomer(dbCustomer);
	}

	private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());

	}

	private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {
		Customer customer;
		if (Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findByCustomerId(petStoreId, customerId);
		}
		return customer;
	}

	private Customer findByCustomerId(Long petStoreId, Long customerId) {
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException(
						"Valid Customer ID of" + customerId + " Not Found"));
		// Set<PetStore> petstores = new HashSet<>();
		// petstores.addAll(customer.getPetStores());
		// List<PetStore> petStores = petStoreDao.findAll();
		// Set<PetStore> petStores =
		// petStoreDao.findAllPetStoresIn(customer.getPetStores());
		List<PetStore> petStores = petStoreDao.findAll();
		for (PetStore petStore : petStores) {
			if (petStore.getPetStoreId() != petStoreId) {
				throw new IllegalArgumentException("Store ID " + petStoreId + " does not match");
			} else {
				return customer;
			}
		}
		return customer; // this might not return the right value, may need to revamp petStore loop
	}

	@Transactional(readOnly = true)
	public List<PetStoreData> retrieveAllPetStores() {
		List<PetStore> petStores = petStoreDao.findAll();
		List<PetStoreData> response = new LinkedList<>();

		for (PetStore petStore : petStores) {
			PetStoreData petStoreData = new PetStoreData(petStore);

			petStoreData.getCustomers().clear();
			petStoreData.getEmployees().clear();

			response.add(petStoreData);
		}
		return response;
	}

	@Transactional(readOnly = true)
	public PetStoreData retrievePetStoreById(Long petStoreId) {
		PetStoreData petStoreData = new PetStoreData(findByPetStoreId(petStoreId));
		return petStoreData;
	}

	public void deletePetStoreById(Long petStoreId) {
		petStoreDao.delete(findByPetStoreId(petStoreId));
		
	}
}
