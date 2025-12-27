package com.dttlibrary.service;

import com.dttlibrary.model.BookItem;
import com.dttlibrary.model.Borrowing;
import com.dttlibrary.model.User;
import com.dttlibrary.repository.BorrowingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookItemService bookItemService;
    private final UserService userService;

    public BorrowingService(BorrowingRepository borrowingRepository, @Lazy BookItemService bookItemService, @Lazy UserService userService) {
        this.borrowingRepository = borrowingRepository;
        this.bookItemService = bookItemService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Borrowing> findByUsername(String username) {
        return borrowingRepository.findByUser_UsernameOrderByBorrowDateDesc(username);
    }

    @Transactional(readOnly = true)
    public List<Borrowing> findAll() {
        return borrowingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Borrowing findById(Integer id) {
        return borrowingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing record not found with ID: " + id));
    }

    @Transactional
    public void borrowBook(Integer bookItemId, String username) {
        BookItem item = bookItemService.findById(bookItemId);
        if (item.getStatus() != BookItem.Status.available) {
            throw new IllegalStateException("Book is not available for borrowing.");
        }

        User user = userService.findByUsername(username);

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setBookItem(item);
        borrowing.setBorrowDate(LocalDateTime.now());
        borrowing.setDueDate(LocalDateTime.now().plusDays(14));
        borrowing.setStatus(Borrowing.Status.borrowed);
        borrowingRepository.save(borrowing);

        item.setStatus(BookItem.Status.borrowed);
        bookItemService.save(item);
    }

    @Transactional
    public void returnBook(Integer borrowingId) {
        Borrowing borrowing = findById(borrowingId);
        if (borrowing.getStatus() == Borrowing.Status.returned) {
            throw new IllegalStateException("This book has already been returned.");
        }

        borrowing.setStatus(Borrowing.Status.returned);
        borrowing.setReturnDate(LocalDateTime.now());
        borrowingRepository.save(borrowing);

        BookItem item = borrowing.getBookItem();
        item.setStatus(BookItem.Status.available);
        bookItemService.save(item);
    }

    @Transactional(readOnly = true)
    public long countBorrowed() {
        return borrowingRepository.countByStatus(Borrowing.Status.borrowed);
    }

    @Transactional(readOnly = true)
    public long countOverdue() {
        return borrowingRepository.countByStatusAndDueDateBefore(Borrowing.Status.borrowed, LocalDateTime.now());
    }
}
