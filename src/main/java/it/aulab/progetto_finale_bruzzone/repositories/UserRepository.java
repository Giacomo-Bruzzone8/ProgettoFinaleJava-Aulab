package it.aulab.progetto_finale_bruzzone.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.aulab.progetto_finale_bruzzone.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
User findByEmail(String email);
    
} 
