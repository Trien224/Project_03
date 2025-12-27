package com.dttlibrary.service;

import com.dttlibrary.model.Publisher;
import com.dttlibrary.repository.PublisherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true)
    public List<Publisher> findAll() {
        return publisherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Publisher findById(Integer id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Publisher not found with ID: " + id));
    }

    @Transactional
    public Publisher save(Publisher publisher) {
        // Có thể thêm logic kiểm tra tên NXB trùng ở đây nếu cần
        return publisherRepository.save(publisher);
    }

    @Transactional
    public void delete(Integer id) {
        if (!publisherRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Publisher not found with ID: " + id);
        }
        publisherRepository.deleteById(id);
    }
}
