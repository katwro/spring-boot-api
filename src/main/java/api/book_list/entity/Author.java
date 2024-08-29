package api.book_list.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Author")
public class Author {

    public interface PostAndPatchView {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuthorID")
    @JsonView({Book.PutBookWithAuthorIdView.class, Book.PostAndPatchBookWithAuthorIdView.class})
    @Schema(description = "ID of the author", example = "1")
    private int id;

    @Column(name = "FirstName")
    @NotBlank(message = "First name cannot be empty")
    @Size(min = 1, max = 25, message = "First name can have a maximum of 25 characters")
    @JsonView(PostAndPatchView.class)
    @Schema(description = "First name of the author", example = "John")
    private String firstName;

    @Column(name = "LastName")
    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 1, max = 50, message = "Last name can have a maximum of 50 characters")
    @JsonView(PostAndPatchView.class)
    @Schema(description = "Last name of the author", example = "Doe")
    private String lastName;

    @ManyToMany(mappedBy = "authors")
    @JsonIgnore
    private Set<Book> books;

    public Author() {
        this.books = new HashSet<>();
    }

    public Author(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
