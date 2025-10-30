package com.selimhorri.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.selimhorri.app.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query("SELECT u FROM User u WHERE u.credentialId IN (SELECT c.credentialId FROM Credential c WHERE c.username = :username)")
	Optional<User> findByCredentialUsername(@Param("username") String username);
	
}
