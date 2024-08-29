package api.book_list.service;

import api.book_list.entity.Author;
import api.book_list.entity.Book;

import java.util.List;

public interface BookService {

    List<Book> findAllBooks();

    List<Author> findAllAuthors();

    Book saveBook(Book book);

    Author saveAuthor(Author author);

    Book findBookById(int id);

    Author findAuthorById(int id);

    void deleteBook(Book book);

    void deleteAuthor(Author author);

    List<Author> findAuthorsByBookId(int id);

    List<Book> findBooksByAuthorId(int id);

    void changeBook(Book existingBook, Book editedBook);

    void changeAuthor(Author existingAuthor, Author editedAuthor);

}
