package com.dttlibrary.service;

import com.dttlibrary.model.Category;
import com.dttlibrary.repository.CategoryRepository;
import com.github.slugify.Slugify;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Slugify slugify;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.slugify = Slugify.builder().build();
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
    }

    @Transactional
    public void save(Category category) {
        // Tự động tạo hoặc chuẩn hóa slug
        if (category.getSlug() == null || category.getSlug().isBlank()) {
            category.setSlug(slugify.slugify(category.getName()));
        } else {
            category.setSlug(slugify.slugify(category.getSlug()));
        }

        // Kiểm tra trùng lặp tên và slug
        categoryRepository.findByName(category.getName())
            .ifPresent(existing -> {
                if (!existing.getId().equals(category.getId())) {
                    throw new IllegalStateException("Category name already exists: " + category.getName());
                }
            });

        categoryRepository.findBySlug(category.getSlug())
            .ifPresent(existing -> {
                if (!existing.getId().equals(category.getId())) {
                    throw new IllegalStateException("Category slug already exists: " + category.getSlug());
                }
            });

        categoryRepository.save(category);
    }

    @Transactional
    public void delete(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Category not found with ID: " + id);
        }
        // Có thể thêm logic kiểm tra xem category có đang được sách nào sử dụng không trước khi xóa
        categoryRepository.deleteById(id);
    }
}
