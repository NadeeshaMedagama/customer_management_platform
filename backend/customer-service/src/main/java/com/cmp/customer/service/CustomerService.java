package com.cmp.customer.service;

import com.cmp.customer.dto.*;
import com.cmp.customer.entity.Address;
import com.cmp.customer.entity.City;
import com.cmp.customer.entity.Country;
import com.cmp.customer.entity.Customer;
import com.cmp.customer.exception.BadRequestException;
import com.cmp.customer.exception.NotFoundException;
import com.cmp.customer.repository.CityRepository;
import com.cmp.customer.repository.CountryRepository;
import com.cmp.customer.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CustomerService(CustomerRepository customerRepository,
                           CityRepository cityRepository,
                           CountryRepository countryRepository) {
        this.customerRepository = customerRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        customerRepository.findByNicNumber(request.getNicNumber()).ifPresent(existing -> {
            throw new BadRequestException("NIC already exists: " + request.getNicNumber());
        });
        Customer customer = new Customer();
        applyRequest(customer, request, null);
        return toResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found for id: " + id));
        if (customerRepository.existsByNicNumberAndIdNot(request.getNicNumber(), id)) {
            throw new BadRequestException("NIC already exists: " + request.getNicNumber());
        }
        applyRequest(customer, request, id);
        return toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found for id: " + id));
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> list(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public BulkUpsertResponse bulkUpsert(BulkUpsertRequest request) {
        Set<String> nics = request.getCustomers().stream().map(BulkUpsertItem::getNicNumber).collect(Collectors.toSet());
        Map<String, Customer> existing = customerRepository.findByNicNumberIn(nics).stream()
                .collect(Collectors.toMap(Customer::getNicNumber, Function.identity()));

        int created = 0;
        int updated = 0;
        List<Customer> saveList = new ArrayList<Customer>();
        for (BulkUpsertItem item : request.getCustomers()) {
            Customer customer = existing.get(item.getNicNumber());
            if (customer == null) {
                customer = new Customer();
                customer.setNicNumber(item.getNicNumber());
                created++;
            } else {
                updated++;
            }
            customer.setName(item.getName());
            customer.setDateOfBirth(item.getDateOfBirth());
            saveList.add(customer);
        }
        customerRepository.saveAll(saveList);
        return new BulkUpsertResponse(created, updated);
    }

    private void applyRequest(Customer customer, CustomerRequest request, Long currentCustomerId) {
        customer.setName(request.getName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNicNumber(request.getNicNumber());

        customer.getMobileNumbers().clear();
        if (request.getMobileNumbers() != null) {
            customer.getMobileNumbers().addAll(request.getMobileNumbers());
        }

        applyFamilyMembers(customer, request.getFamilyMemberNics(), currentCustomerId);
        applyAddresses(customer, request.getAddresses());
    }

    private void applyFamilyMembers(Customer customer, List<String> familyMemberNics, Long currentCustomerId) {
        customer.getFamilyMembers().clear();
        if (familyMemberNics == null || familyMemberNics.isEmpty()) {
            return;
        }
        List<Customer> family = customerRepository.findByNicNumberIn(familyMemberNics);
        if (family.size() != new HashSet<String>(familyMemberNics).size()) {
            throw new BadRequestException("One or more family member NICs do not exist");
        }
        for (Customer familyMember : family) {
            if (currentCustomerId != null && currentCustomerId.equals(familyMember.getId())) {
                throw new BadRequestException("Customer cannot be their own family member");
            }
            customer.getFamilyMembers().add(familyMember);
        }
    }

    private void applyAddresses(Customer customer, List<AddressRequest> addressRequests) {
        customer.getAddresses().clear();
        if (addressRequests == null || addressRequests.isEmpty()) {
            return;
        }

        Set<String> cityCodes = addressRequests.stream().map(AddressRequest::getCityCode)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> countryCodes = addressRequests.stream().map(AddressRequest::getCountryCode)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<String, City> cityMap = cityRepository.findByCodeIn(cityCodes).stream()
                .collect(Collectors.toMap(City::getCode, Function.identity()));
        Map<String, Country> countryMap = countryRepository.findByCodeIn(countryCodes).stream()
                .collect(Collectors.toMap(Country::getCode, Function.identity()));

        for (AddressRequest addressRequest : addressRequests) {
            Address address = new Address();
            address.setAddressLine1(addressRequest.getAddressLine1());
            address.setAddressLine2(addressRequest.getAddressLine2());
            if (addressRequest.getCityCode() != null) {
                City city = cityMap.get(addressRequest.getCityCode());
                if (city == null) {
                    throw new BadRequestException("Invalid city code: " + addressRequest.getCityCode());
                }
                address.setCity(city);
            }
            if (addressRequest.getCountryCode() != null) {
                Country country = countryMap.get(addressRequest.getCountryCode());
                if (country == null) {
                    throw new BadRequestException("Invalid country code: " + addressRequest.getCountryCode());
                }
                address.setCountry(country);
            }
            address.setCustomer(customer);
            customer.getAddresses().add(address);
        }
    }

    private CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setNicNumber(customer.getNicNumber());
        response.setMobileNumbers(new ArrayList<String>(customer.getMobileNumbers()));

        List<AddressResponse> addresses = customer.getAddresses().stream().map(address -> {
            AddressResponse item = new AddressResponse();
            item.setAddressLine1(address.getAddressLine1());
            item.setAddressLine2(address.getAddressLine2());
            item.setCityCode(address.getCity() == null ? null : address.getCity().getCode());
            item.setCity(address.getCity() == null ? null : address.getCity().getName());
            item.setCountryCode(address.getCountry() == null ? null : address.getCountry().getCode());
            item.setCountry(address.getCountry() == null ? null : address.getCountry().getName());
            return item;
        }).collect(Collectors.toList());
        response.setAddresses(addresses);

        List<String> familyNics = customer.getFamilyMembers().stream()
                .map(Customer::getNicNumber)
                .collect(Collectors.toList());
        response.setFamilyMemberNics(familyNics);
        return response;
    }
}

