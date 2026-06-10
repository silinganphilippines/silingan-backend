package com.ria.olita.tech.silingan.repository;

import com.ria.olita.tech.silingan.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
}
