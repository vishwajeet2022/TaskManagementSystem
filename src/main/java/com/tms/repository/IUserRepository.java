package com.tms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.model.TMSUser;

@Repository
public interface IUserRepository extends JpaRepository<TMSUser, Long> {
	Optional<TMSUser> findByEmail(String email);
}
