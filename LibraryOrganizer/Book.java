import java.io.Serializable;

/**
 * Represents a book with details such as title, authors, price, ISBN, genre, and year of publication.
 * This class implements Serializable to allow object serialization.
 * It provides methods for accessing and modifying the book's attributes, as well as comparing and displaying book details.
 */

public class Book implements Serializable{
    
    // attributes
    private String title;
    private String authors;
    private double price;
    private String isbn;
    private String genre;
    private int year;

    // parameterized constructor
    public Book(String title, String authors, double price, String isbn, String genre, int year){
        this.title = title;
        this.authors = authors;
        this.price = price;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
    }

    // getters 
    public String getTitle(){
        return title;
    }

    public String getAuthors(){
        return authors;
    }

    public double getPrice(){
        return price;
    }

    public String getIsbn(){
        return isbn;
    }

    public String getGenre(){
        return genre;
    }

    public int getYear(){
        return year;
    }

    // setters
    public void setTitle(String title){
        this.title = title;
    }

    public void setAuthors(String authors){
        this.authors = authors;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public void setIsbn(String isbn){
        this.isbn = isbn;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public void setYear(int year){
        this.year = year;
    }

    // equals method
    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        Book other = (Book) obj;
        return (this.title.equals(other.title) && this.authors.equals(other.authors) && this.price==other.price && this.isbn.equals(other.isbn) && this.genre.equals(other.genre) && this.year == other.year);
    }

    // toString method
    @Override
    public String toString(){
        return "Book: title: " + title + " author(s): " + authors + " price: " + price + " isbn: " + isbn + " genre: " + genre + " year: " + year; 
    }

}

