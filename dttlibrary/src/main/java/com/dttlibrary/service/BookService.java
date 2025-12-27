package com.dttlibrary.service;

import com.dttlibrary.model.*;
import com.dttlibrary.repository.BookImageRepository;
import com.dttlibrary.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookItemService bookItemService;
    private final FileStorageService fileStorageService;
    private final PublisherService publisherService;

    public BookService(BookRepository bookRepository, BookImageRepository bookImageRepository,
                       CategoryService categoryService, AuthorService authorService,
                       @Lazy BookItemService bookItemService, FileStorageService fileStorageService,
                       PublisherService publisherService) {
        this.bookRepository = bookRepository;
        this.bookImageRepository = bookImageRepository;
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookItemService = bookItemService;
        this.fileStorageService = fileStorageService;
        this.publisherService = publisherService;
    }

    @Transactional
    public Book saveAdminBook(Book bookFromForm, Integer categoryId, Integer authorId, Integer publisherId,
                              MultipartFile image, Integer initialQuantity) {
        boolean isNewBook = bookFromForm.getId() == null;
        Book bookToSave = isNewBook ? new Book() : findById(bookFromForm.getId());

        bookToSave.setTitle(bookFromForm.getTitle());
        bookToSave.setDescription(bookFromForm.getDescription());
        bookToSave.setIsbn(bookFromForm.getIsbn());
        bookToSave.setLanguage(bookFromForm.getLanguage());
        bookToSave.setNumberOfPages(bookFromForm.getNumberOfPages());
        bookToSave.setPublishedYear(bookFromForm.getPublishedYear());

        bookToSave.setCategory(categoryService.findById(categoryId));
        bookToSave.setAuthor(authorId != null ? authorService.findById(authorId) : null);
        bookToSave.setPublisher(publisherId != null ? publisherService.findById(publisherId) : null);

        Book savedBook = bookRepository.save(bookToSave);

        if (isNewBook && initialQuantity > 0) {
            bookItemService.addCopies(savedBook.getId(), initialQuantity);
        }

        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.store(image);
            BookImage img = bookImageRepository.findFirstByBook_IdAndIsPrimaryTrue(savedBook.getId()).orElse(new BookImage());
            img.setBook(savedBook);
            img.setUrl("/uploads/" + fileName);
            img.setIsPrimary(true);
            bookImageRepository.save(img);
        }

        return savedBook;
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Book> findAllWithAvailableItems() {
        return bookRepository.findAllWithAvailableItems(BookItem.Status.available);
    }

    @Transactional(readOnly = true)
    public Book findById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Book findByIdWithItems(Integer id) {
        return bookRepository.findWithItemsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with items not found with ID: " + id));
    }

    @Transactional
    public void delete(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Book> findLatestBooks() {
        return bookRepository.findTop8ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Book> findByCategorySlug(String slug) {
        return bookRepository.findByCategory_Slug(slug);
    }

    @Transactional(readOnly = true)
    public BookImage getPrimaryImage(Integer bookId) {
        return bookImageRepository.findFirstByBook_IdAndIsPrimaryTrue(bookId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<BookImage> getImages(Integer bookId) {
        return bookImageRepository.findByBook_Id(bookId);
    }

    @Transactional(readOnly = true)
    public long countBooks() {
        return bookRepository.count();
    }
}
