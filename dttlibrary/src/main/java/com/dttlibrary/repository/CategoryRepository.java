package com.dttlibrary.repository;

import com.dttlibrary.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String name);

    Optional<Category> findBySlug(String slug);
}
