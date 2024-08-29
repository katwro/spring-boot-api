package api.book_list.controller;

import api.book_list.entity.Author;
import api.book_list.entity.Book;
import api.book_list.service.BookService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    @Operation(summary = "Get all books")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public List<Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    @GetMapping("/authors")
    @Operation(summary = "Get all authors")
    @ApiResponse(responseCode = "200", description = "Authors retrieved successfully")
    public List<Author> getAllAuthors() {
        return bookService.findAllAuthors();
    }

    @GetMapping("/books/{id}")
    @Operation(summary = "Get book by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public Book getBook(@PathVariable int id) {
        return bookService.findBookById(id);
    }

    @GetMapping("/authors/{id}")
    @Operation(summary = "Get author by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public Author getAuthor(@PathVariable int id) {
        return bookService.findAuthorById(id);
    }

    @GetMapping("/books/{id}/authors")
    @Operation(summary = "Get authors by book ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authors retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public List<Author> getAuthorsByBookId(@PathVariable int id) {
        return bookService.findAuthorsByBookId(id);
    }

    @GetMapping("/authors/{id}/books")
    @Operation(summary = "Get books by author ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public List<Book> getBooksByAuthorId(@PathVariable int id) {
        return bookService.findBooksByAuthorId(id);
    }

    @PostMapping("/books")
    @Operation(summary = "Create a new book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation error or malformed JSON"),
            @ApiResponse(responseCode = "404", description = "Some authors not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody @JsonView(Book.PostAndPatchBookWithAuthorIdView.class) Book book) {
        book.setId(0);
        return bookService.saveBook(book);
    }

    @PostMapping("/authors")
    @Operation(summary = "Create a new author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Author created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation error or malformed JSON")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Author createAuthor(@RequestBody @JsonView(Author.PostAndPatchView.class) Author author) {
        author.setId(0);
        return bookService.saveAuthor(author);
    }

    @PutMapping("/books")
    @Operation(summary = "Update an existing book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation error or malformed JSON"),
            @ApiResponse(responseCode = "404", description = "Book not found or some authors not found")
    })
    public Book updateBook(@RequestBody @JsonView(Book.PutBookWithAuthorIdView.class) Book book) {
        bookService.findBookById(book.getId());
        return bookService.saveBook(book);
    }

    @PutMapping("/authors")
    @Operation(summary = "Update an existing author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation error or malformed JSON"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public Author updateAuthor(@RequestBody Author author) {
        bookService.findAuthorById(author.getId());
        return bookService.saveAuthor(author);
    }

    @PatchMapping("/books/{id}")
    @Operation(summary = "Partially update an existing book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation error or malformed JSON"),
            @ApiResponse(responseCode = "404", description = "Book not found or some authors not found")
    })
    public Book patchBook(@PathVariable int id, @RequestBody @JsonView(Book.PostAndPatchBookWithAuthorIdView.class) Book editedBook) {
        Book existingBook = bookService.findBookById(id);
        bookService.changeBook(existingBook, editedBook);
        return existingBook;
    }

    @PatchMapping("/authors/{id}")
    @Operation(summary = "Partially update an existing author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation error or malformed JSON"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public Author patchAuthor(@PathVariable int id, @RequestBody @JsonView(Author.PostAndPatchView.class) Author editedAuthor) {
        Author existingAuthor = bookService.findAuthorById(id);
        bookService.changeAuthor(existingAuthor, editedAuthor);
        return existingAuthor;
    }


    @DeleteMapping("/books/{id}")
    @Operation(summary = "Delete a book by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> deleteBookById(@PathVariable int id) {
        Book book = bookService.findBookById(id);
        bookService.deleteBook(book);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/authors/{id}")
    @Operation(summary = "Delete an author by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Void> deleteAuthorById(@PathVariable int id) {
        Author author = bookService.findAuthorById(id);
        bookService.deleteAuthor(author);
        return ResponseEntity.noContent().build();
    }

}
