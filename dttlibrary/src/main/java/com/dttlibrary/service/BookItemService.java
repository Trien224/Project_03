package com.dttlibrary.service;

import com.dttlibrary.model.Book;
import com.dttlibrary.model.BookItem;
import com.dttlibrary.repository.BookItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookItemService {

    private final BookItemRepository bookItemRepository;
    private final BookService bookService;

    public BookItemService(BookItemRepository bookItemRepository, @Lazy BookService bookService) {
        this.bookItemRepository = bookItemRepository;
        this.bookService = bookService;
    }

    @Transactional
    public BookItem saveAdminItem(BookItem item, Integer bookId) {
        Book book = bookService.findById(bookId);
        item.setBook(book);

        if (item.getStatus() == null) {
            item.setStatus(BookItem.Status.available);
        }
        
        return bookItemRepository.save(item);
    }

    @Transactional
    public void addCopies(Integer bookId, int quantityToAdd) {
        Book book = bookService.findById(bookId);
        for (int i = 0; i < quantityToAdd; i++) {
            BookItem newItem = new BookItem();
            newItem.setBook(book);
            String barcode = (book.getIsbn() != null ? book.getIsbn() : "N/A") + "-" + UUID.randomUUID().toString().substring(0, 4);
            newItem.setBarcode(barcode);
            newItem.setStatus(BookItem.Status.available);
            bookItemRepository.save(newItem);
        }
    }
    
    @Transactional
    public BookItem save(BookItem item) {
        return bookItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<BookItem> findAll() {
        return bookItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BookItem findById(Integer id) {
        return bookItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book Item not found with ID: " + id));
    }

    @Transactional
    public void delete(Integer id) {
        BookItem item = findById(id);
        if (item.getStatus() != BookItem.Status.available) {
            throw new IllegalStateException("Cannot delete a book item that is not available.");
        }
        bookItemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countItems() {
        return bookItemRepository.count();
    }

    @Transactional(readOnly = true)
    public BookItem findFirstAvailable(Integer bookId) {
        return bookItemRepository.findFirstByBook_IdAndStatus(bookId, BookItem.Status.available).orElse(null);
    }

    @Transactional(readOnly = true)
    public long countAvailableByBookId(Integer bookId) {
        return bookItemRepository.countByBook_IdAndStatus(bookId, BookItem.Status.available);
    }
}
