package it.aulab.progetto_finale_bruzzone.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.aulab.progetto_finale_bruzzone.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
