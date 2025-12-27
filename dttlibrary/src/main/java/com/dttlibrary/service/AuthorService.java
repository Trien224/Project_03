package com.dttlibrary.service;

import com.dttlibrary.model.Author;
import com.dttlibrary.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Author findById(Integer id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with ID: " + id));
    }

    @Transactional
    public Author save(Author author) {
        // Có thể thêm logic kiểm tra tên tác giả trùng ở đây nếu cần
        return authorRepository.save(author);
    }

    @Transactional
    public void delete(Integer id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Author not found with ID: " + id);
        }
        // Có thể thêm logic kiểm tra xem tác giả có sách nào không trước khi xóa
        authorRepository.deleteById(id);
    }
}
