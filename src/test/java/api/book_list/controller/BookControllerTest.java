package api.book_list.controller;

import api.book_list.entity.Author;
import api.book_list.entity.Book;
import api.book_list.repository.AuthorRepository;
import api.book_list.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookControllerTest {

    private final MockMvc mockMvc;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final JdbcTemplate jdbc;

    @Autowired
    public BookControllerTest(MockMvc mockMvc, BookRepository bookRepository, AuthorRepository authorRepository, JdbcTemplate jdbc) {
        this.mockMvc = mockMvc;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.jdbc = jdbc;
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
    void setupAfterTransaction() {
        jdbc.execute("DELETE FROM Book_Author");
        jdbc.execute("DELETE FROM Author");
        jdbc.execute("DELETE FROM Book");
    }

    @Test
    void testCreateBook() throws Exception {
        // Given
        String bookJson = "{\"title\":\"Second Book\", \"isbn\":\"978-83-01-00000-2\"}";

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Second Book"))
                .andExpect(jsonPath("$.isbn").value("978-83-01-00000-2"));
    }

    @Test
    void testCreateBook_EmptyTitle() throws Exception {
        // Given
        String bookJson = "{\"title\":\"\", \"isbn\":\"978-83-01-00000-2\"}";

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBook_ToLongISBN() throws Exception {
        // Given
        String bookJson = "{\"title\":\"Book Title\", \"isbn\":\"978-83-01-00000-22\"}";

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllBooks() throws Exception {
        // Given
        Book book1 = new Book("Second Book","978-83-01-00000-2", null);
        bookRepository.save(book1);

        Book book2 = new Book("Third Book", "978-83-01-00000-3",null);
        bookRepository.save(book2);

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("First Book"))
                .andExpect(jsonPath("$[1].title").value("Second Book"))
                .andExpect(jsonPath("$[2].title").value("Third Book"));
    }

    @Test
    void testGetBookById() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("First Book"))
                .andExpect(jsonPath("$.isbn").value("978-83-01-00000-1"));
    }

    @Test
    void testGetBooksByAuthorId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/authors/1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("First Book"));
    }

    @Test
    void testGetBooksByAuthorId_AuthorNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/authors/999/books"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateBook() throws Exception {
        // Given
        String updatedBookJson = "{\"id\":1,\"title\":\"New Title\",\"isbn\":\"978-83-01-00000-1\"}";

        // When & Then
        mockMvc.perform(put("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void testPatchBook() throws Exception {
        // Given
        Author author = new Author("John", "Second");
        authorRepository.save(author);

        String patchedBookJson = "{\"authors\":[{\"id\":1},{\"id\":2}]}";

        // When & Then
        mockMvc.perform(patch("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchedBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("First Book"))
                .andExpect(jsonPath("$.authors.length()").value(2))
                .andExpect(jsonPath("$.authors[0].id").value(1))
                .andExpect(jsonPath("$.authors[1].id").value(2));
    }

    @Test
    void testDeleteBook() throws Exception {
        // Given
        assertEquals(1, bookRepository.count());

        // When & Then
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        assertEquals(0, bookRepository.count());
    }

    @Test
    void testCreateAuthor() throws Exception {
        // Given
        String authorJson = "{\"firstName\":\"Jane\", \"lastName\":\"Smith\"}";

        // When & Then
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));

        assertEquals(2, authorRepository.count());
    }

    @Test
    void testGetAllAuthors() throws Exception {
        // Given
        Author author1 = new Author("Jane", "Smith");
        authorRepository.save(author1);

        Author author2 = new Author("David", "Wilson");
        authorRepository.save(author2);

        // When & Then
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[2].firstName").value("David"));
    }

    @Test
    void testGetAuthorById() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testGetAuthorsByBookId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/books/1/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void testGetAuthorsByBookId_BookNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/books/999/authors"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAuthor() throws Exception {
        // Given
        String updatedAuthorJson = "{\"id\":1,\"firstName\":\"NewFirstName\", \"lastName\":\"NewLastName\"}";

        // When & Then
        mockMvc.perform(put("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAuthorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("NewFirstName"))
                .andExpect(jsonPath("$.lastName").value("NewLastName"));
    }

    @Test
    void testPatchAuthor() throws Exception {
        // Given
        String patchedAuthorJson = "{\"lastName\":\"NewLastName\"}";

        // When & Then
        mockMvc.perform(patch("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchedAuthorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("NewLastName"));
    }

    @Test
    void testDeleteAuthor() throws Exception {
        // Given
        assertEquals(1, authorRepository.count());

        // When & Then
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());
        assertEquals(0, authorRepository.count());
    }

}
