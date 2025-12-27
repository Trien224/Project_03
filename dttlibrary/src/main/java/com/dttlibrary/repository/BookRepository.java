package com.dttlibrary.repository;

import com.dttlibrary.model.Book;
import com.dttlibrary.model.BookItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    /**
     * Sử dụng @EntityGraph để thay thế cho JOIN FETCH, giúp truy vấn gọn hơn và linh hoạt hơn.
     * Tải tất cả sách cùng với thông tin category, author, và images.
     */
    @Override
    @EntityGraph(attributePaths = {"category", "author", "images"})
    List<Book> findAll();

    /**
     * Tải một sách theo ID cùng với danh sách các bản sao (bookItems).
     */
    @EntityGraph(attributePaths = {"bookItems"})
    Optional<Book> findWithItemsById(Integer id);

    /**
     * Tải 8 cuốn sách mới nhất, kèm theo ảnh để hiển thị.
     */
    @EntityGraph(attributePaths = {"images", "author"})
    List<Book> findTop8ByOrderByCreatedAtDesc();

    /**
     * Tải các sách có ít nhất một bản sao đang 'available'.
     * DISTINCT là cần thiết để tránh trùng lặp sách.
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.bookItems bi WHERE bi.status = :status")
    @EntityGraph(attributePaths = {"images", "author"})
    List<Book> findAllWithAvailableItems(@Param("status") BookItem.Status status);

    /**
     * Tải các sách thuộc một thể loại (dựa trên slug), kèm theo ảnh.
     */
    @EntityGraph(attributePaths = {"images", "author"})
    List<Book> findByCategory_Slug(String slug);
}
