package api.book_list.service;

import api.book_list.entity.Author;
import api.book_list.entity.Book;
import api.book_list.repository.AuthorRepository;
import api.book_list.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    @Transactional
    public Book saveBook(Book book) {
        if (!book.getAuthors().isEmpty()) {
            Set<Integer> authorIds = book.getAuthors().stream()
                    .map(Author::getId)
                    .collect(Collectors.toSet());
            List<Author> fetchedAuthors = authorRepository.findAllById(authorIds);
            if (fetchedAuthors.size() != authorIds.size()) {
                throw new EntityNotFoundException("Some authors were not found");
            }
            book.setAuthors(new LinkedHashSet<>(fetchedAuthors));
        }
        return bookRepository.save(book);
    }

    @Override
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Book findBookById(int id) {
        Optional<Book> result = bookRepository.findById(id);
        Book book;
        if (result.isPresent()) {
            book = result.get();
        } else {
            throw new EntityNotFoundException("No result found for book with ID: " + id);
        }
        return book;
    }

    @Override
    public Author findAuthorById(int id) {
        Optional<Author> result = authorRepository.findById(id);
        Author author;
        if (result.isPresent()) {
            author = result.get();
        } else {
            throw new EntityNotFoundException("No result found for author with ID: " + id);
        }
        return author;
    }

    @Override
    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }

    @Override
    @Transactional
    public void deleteAuthor(Author author) {
        Author fullAuthor = authorRepository.findAuthorWithBooksById(author.getId())
                .orElseThrow(() -> new EntityNotFoundException("No result found for author with ID: " + author.getId()));
        Set<Book> authorBooks = fullAuthor.getBooks();
        if (!authorBooks.isEmpty()) {
            authorBooks.forEach(book -> book.getAuthors().remove(fullAuthor));
            bookRepository.saveAll(authorBooks);
        }
        authorRepository.delete(fullAuthor);
    }

    @Override
    public List<Author> findAuthorsByBookId(int id) {
        Optional<Book> result = bookRepository.findById(id);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("No result found for book with ID: " + id);
        }
        return authorRepository.findByBooksId(id);
    }

    @Override
    public List<Book> findBooksByAuthorId(int id) {
        Optional<Author> result = authorRepository.findById(id);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("No result found for author with ID: " + id);
        }
        return bookRepository.findByAuthorsId(id);
    }

    @Override
    @Transactional
    public void changeBook(Book existingBook, Book editedBook) {
        if (!editedBook.getAuthors().isEmpty()) {
            Set<Integer> authorIds = editedBook.getAuthors().stream()
                    .map(Author::getId)
                    .collect(Collectors.toSet());
            List<Author> fetchedAuthors = authorRepository.findAllById(authorIds);
            if (fetchedAuthors.size() != authorIds.size()) {
                throw new EntityNotFoundException("Some authors were not found");
            }
            existingBook.setAuthors(new LinkedHashSet<>(fetchedAuthors));
        }
        if (editedBook.getIsbn() != null) {
            existingBook.setIsbn(editedBook.getIsbn());
        }
        if (editedBook.getTitle() != null) {
            existingBook.setTitle(editedBook.getTitle());
        }
        bookRepository.save(existingBook);
    }

    @Override
    public void changeAuthor(Author existingAuthor, Author editedAuthor) {
        if (editedAuthor.getFirstName() != null) {
            existingAuthor.setFirstName(editedAuthor.getFirstName());
        }
        if (editedAuthor.getLastName() != null) {
            existingAuthor.setLastName(editedAuthor.getLastName());
        }
        authorRepository.save(existingAuthor);
    }

}

