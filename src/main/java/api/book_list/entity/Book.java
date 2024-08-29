package api.book_list.entity;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Book")
public class Book {

    public interface PostAndPatchBookWithAuthorIdView {}
    public interface PutBookWithAuthorIdView extends PostAndPatchBookWithAuthorIdView {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookID")
    @JsonView(PutBookWithAuthorIdView.class)
    @Schema(description = "ID of the book", example = "1")
    private int id;

    @Column(name = "Title")
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 1, max = 100, message = "Title can have a maximum of 100 characters")
    @JsonView(PostAndPatchBookWithAuthorIdView.class)
    @Schema(description = "Title of the book", example = "Book Title")
    private String title;

    @Column(name = "ISBN")
    @NotBlank(message = "ISBN cannot be empty")
    @Size(min = 1, max = 17, message = "ISBN can have a maximum of 17 characters")
    @JsonView(PostAndPatchBookWithAuthorIdView.class)
    @Schema(description = "ISBN of the book", example = "978-83-01-00000-1")
    private String isbn;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "Book_Author",
            joinColumns = @JoinColumn(name = "BookID"),
            inverseJoinColumns = @JoinColumn(name = "AuthorID"))
    @JsonView({PutBookWithAuthorIdView.class, PostAndPatchBookWithAuthorIdView.class})
    private Set<Author> authors;

    public Book() {
        this.authors = new LinkedHashSet<>();
    }

    public Book(String title, String isbn) {
        this();
        this.title = title;
        this.isbn = isbn;
    }

    public Book(String title, String isbn, Set<Author> authors) {
        this();
        this.title = title;
        this.isbn = isbn;
        this.authors = authors;
    }

}
