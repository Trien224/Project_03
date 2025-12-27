package com.dttlibrary.repository;

import com.dttlibrary.model.Borrowing;
import com.dttlibrary.model.Borrowing.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, Integer> {

    /**
     * Tải danh sách lượt mượn của một user, kèm theo thông tin BookItem và Book.
     * Sắp xếp theo ngày mượn gần nhất.
     */
    @EntityGraph(attributePaths = {"bookItem", "bookItem.book"})
    List<Borrowing> findByUser_UsernameOrderByBorrowDateDesc(String username);

    /**
     * Tải tất cả các lượt mượn, kèm thông tin user, bookitem và book.
     * Dùng cho trang quản lý của Admin.
     */
    @Override
    @EntityGraph(attributePaths = {"user", "bookItem", "bookItem.book"})
    List<Borrowing> findAll();

    long countByStatus(Status status);

    long countByStatusAndDueDateBefore(Status status, LocalDateTime date);
}
