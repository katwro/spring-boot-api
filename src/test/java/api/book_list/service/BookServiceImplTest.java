package api.book_list.service;

import api.book_list.entity.Author;
import api.book_list.entity.Book;
import api.book_list.repository.AuthorRepository;
import api.book_list.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BookServiceImplTest {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final JdbcTemplate jdbc;

    @Autowired
    public BookServiceImplTest(BookService bookService, BookRepository bookRepository, AuthorRepository authorRepository, JdbcTemplate jdbcTemplate) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.jdbc = jdbcTemplate;
    }

    @BeforeEach
    void setup() {
        jdbc.execute("ALTER TABLE Author ALTER COLUMN AuthorID RESTART WITH 1");
        jdbc.execute("ALTER TABLE Book ALTER COLUMN BookID RESTART WITH 1");

        jdbc.execute("INSERT INTO Author(FirstName, LastName) VALUES ('John', 'Doe')");
        jdbc.execute("INSERT INTO Book(Title, ISBN) VALUES ('First Book', '978-83-01-00000-1')");
        jdbc.execute("INSERT INTO Book_Author(BookID, AuthorID) VALUES (1, 1)");
    }

    @AfterEach
    void teardown() {
        jdbc.execute("DELETE FROM Book_Author");
        jdbc.execute("DELETE FROM Author");
        jdbc.execute("DELETE FROM Book");
    }

    @Test
    void testSaveBook() {
        // Given
        Book book = new Book("Second Book", "978-83-01-00000-2",
                Set.of(bookService.findAuthorById(1)));

        // When
        Book savedBook = bookService.saveBook(book);

        // Then
        assertTrue(savedBook.getId() > 0);
        assertEquals("Second Book", savedBook.getTitle());
        assertEquals("978-83-01-00000-2", savedBook.getIsbn());
        assertEquals(1, savedBook.getAuthors().size());

    }

    @Test
    void testSaveBook_SomeAuthorsNotFound() {
        // Given
        Author author1 = bookService.findAuthorById(1);
        Author author2 = new Author();
        author2.setId(999);
        Book book = new Book("Second Book", "978-83-01-00000-2",
                Set.of(author1, author2));

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                bookService.saveBook(book));
        assertEquals("Some authors were not found", exception.getMessage());
        assertFalse(bookRepository.existsById(book.getId()));
    }

    @Test
    void testFindBookById() {
        // When
        Book foundBook = bookService.findBookById(1);

        // Then
        assertNotNull(foundBook);
        assertEquals("First Book", foundBook.getTitle());
    }

    @Test
    void testFindBookById_BookDoesNotExist() {
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> bookService.findBookById(999));
    }

    @Test
    void testFindAllBooks() {
        // Given
        Book book1 = new Book("Second Book", "978-83-01-00000-2");
        bookRepository.save(book1);

        Book book2 = new Book(" Third Book", "978-83-01-00000-3");
        bookRepository.save(book2);

        // When
        List<Book> books = bookService.findAllBooks();

        // Then
        assertEquals(3, books.size());
    }

    @Test
    void testFindBooksByAuthorId() {
        // When
        List<Book> books = bookService.findBooksByAuthorId(1);

        // Then
        assertEquals(1, books.size());
        assertEquals("First Book", books.get(0).getTitle());
    }

    @Test
    void testFindBooksByAuthorId_AuthorNotFound() {
        // When & Then
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                bookService.findBooksByAuthorId(999));

        assertEquals("No result found for author with ID: 999", thrown.getMessage());
    }

    @Test
    void testChangeBook() {
        // Given
        Book existingBook = bookService.findBookById(1);

        Book editedBook = new Book("New Title", null,
                Set.of(bookService.findAuthorById(1)));

        // When
        bookService.changeBook(existingBook, editedBook);

        // Then
        Book updatedBook = bookService.findBookById(existingBook.getId());
        assertEquals("New Title", updatedBook.getTitle());
        assertEquals("978-83-01-00000-1", updatedBook.getIsbn());
        assertEquals(1, updatedBook.getAuthors().size());
    }

    @Test
    void testDeleteBook() {
        // Given
        Book deletedBook = bookService.findBookById(1);
        assertEquals(1, authorRepository.count());
        assertEquals(1, bookRepository.count());

        // When
        bookService.deleteBook(deletedBook);

        // Then
        assertEquals(1, authorRepository.count());
        assertEquals(0, bookRepository.count());
        assertFalse(bookRepository.existsById(deletedBook.getId()));
    }

    @Test
    void testSaveAuthor() {
        // Given
        Author author = new Author("Jane", "Smith");

        // When
        Author savedAuthor = bookService.saveAuthor(author);

        // Then
        assertTrue(savedAuthor.getId() > 0);
        assertEquals("Jane", savedAuthor.getFirstName());
        assertEquals("Smith", savedAuthor.getLastName());
    }

    @Test
    void testFindAuthorById_AuthorExists() {
        // When
        Author foundAuthor = bookService.findAuthorById(1);

        // Then
        assertNotNull(foundAuthor);
        assertEquals("John", foundAuthor.getFirstName());
        assertEquals("Doe", foundAuthor.getLastName());
    }

    @Test
    void testFindAuthorById_AuthorDoesNotExist() {
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> bookService.findAuthorById(999));
    }

    @Test
    void testFindAllAuthors() {
        // Given
        Author author1 = new Author("Jane", "Smith");
        authorRepository.save(author1);

        Author author2 = new Author("David", "Wilson");
        authorRepository.save(author2);

        // When
        List<Author> authors = bookService.findAllAuthors();

        // Then
        assertEquals(3, authors.size());
        assertEquals("Jane", authors.get(1).getFirstName());
    }

    @Test
    void testFindAuthorsByBookId() {
        // When
        List<Author> authors = bookService.findAuthorsByBookId(1);

        // Then
        assertEquals(1, authors.size());
        assertEquals("John", authors.get(0).getFirstName());
    }

    @Test
    void testFindAuthorsByBookId_BookNotFound() {
        // When & Then
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                bookService.findAuthorsByBookId(999));

        assertEquals("No result found for book with ID: 999", thrown.getMessage());
    }

    @Test
    void testChangeAuthor() {
        // Given
        Author existingAuthor = bookService.findAuthorById(1);
        Author editedAuthor = new Author(null, "NewLastName");

        // When
        bookService.changeAuthor(existingAuthor, editedAuthor);

        // Then
        assertEquals("John", existingAuthor.getFirstName());
        assertEquals("NewLastName", existingAuthor.getLastName());
    }

    @Test
    void testDeleteAuthor() {
        // Given
        Author author = bookService.findAuthorById(1);
        assertEquals(1, authorRepository.count());
        assertEquals(1, bookRepository.count());

        // When
        bookService.deleteAuthor(author);

        // Then
        assertEquals(0, authorRepository.count());
        assertEquals(1, bookRepository.count());
    }

}
