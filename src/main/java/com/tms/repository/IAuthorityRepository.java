package com.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.model.Authority;
@Repository
public interface IAuthorityRepository extends JpaRepository<Authority, Long> {

}
