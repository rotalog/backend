package com.rotalog.api.service;

import com.rotalog.api.dto.DistributorDTO;
import com.rotalog.api.exception.BusinessException;
import com.rotalog.api.exception.ResourceNotFoundException;
import com.rotalog.api.model.Distributor;
import com.rotalog.api.repository.DistributorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistributorService {

    private final DistributorRepository distributorRepository;

    @Transactional(readOnly = true)
    public List<DistributorDTO.Response> listAll() {
        return distributorRepository.findAll()
                .stream()
                .map(DistributorDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DistributorDTO.Response> listActive() {
        return distributorRepository.findByActiveTrue()
                .stream()
                .map(DistributorDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DistributorDTO.Response findById(Long id) {
        return distributorRepository.findById(id)
                .map(DistributorDTO.Response::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Distributor not found with id: " + id));
    }

    @Transactional
    public DistributorDTO.Response create(DistributorDTO.Request request) {
        if (request.getTaxId() != null && distributorRepository.existsByTaxId(request.getTaxId())) {
            throw new BusinessException("A distributor with this tax ID already exists: " + request.getTaxId());
        }

        Distributor distributor = Distributor.builder()
                .legalName(request.getLegalName())
                .tradeName(request.getTradeName())
                .taxId(request.getTaxId())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .phone(request.getPhone())
                .email(request.getEmail())
                .contactPerson(request.getContactPerson())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return DistributorDTO.Response.fromEntity(distributorRepository.save(distributor));
    }

    @Transactional
    public DistributorDTO.Response update(Long id, DistributorDTO.Request request) {
        Distributor distributor = distributorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distributor not found with id: " + id));

        if (request.getTaxId() != null && !request.getTaxId().equals(distributor.getTaxId())) {
            if (distributorRepository.existsByTaxId(request.getTaxId())) {
                throw new BusinessException("A distributor with this tax ID already exists: " + request.getTaxId());
            }
        }

        distributor.setLegalName(request.getLegalName());
        distributor.setTradeName(request.getTradeName());
        distributor.setTaxId(request.getTaxId());
        distributor.setAddress(request.getAddress());
        distributor.setCity(request.getCity());
        distributor.setState(request.getState());
        distributor.setPhone(request.getPhone());
        distributor.setEmail(request.getEmail());
        distributor.setContactPerson(request.getContactPerson());
        if (request.getActive() != null) distributor.setActive(request.getActive());

        return DistributorDTO.Response.fromEntity(distributorRepository.save(distributor));
    }

    @Transactional
    public void delete(Long id) {
        if (!distributorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Distributor not found with id: " + id);
        }
        distributorRepository.deleteById(id);
    }

    @Transactional
    public DistributorDTO.Response toggleStatus(Long id) {
        Distributor distributor = distributorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distributor not found with id: " + id));
        distributor.setActive(!distributor.getActive());
        return DistributorDTO.Response.fromEntity(distributorRepository.save(distributor));
    }

    public Distributor findEntityById(Long id) {
        return distributorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distributor not found with id: " + id));
    }
}
