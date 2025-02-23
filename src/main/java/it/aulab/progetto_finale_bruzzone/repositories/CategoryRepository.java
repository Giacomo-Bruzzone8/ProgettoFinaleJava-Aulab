package it.aulab.progetto_finale_bruzzone.repositories;



import org.springframework.data.repository.ListCrudRepository;

import it.aulab.progetto_finale_bruzzone.models.Category;

public interface CategoryRepository extends ListCrudRepository<Category, Long> {

}
