 /**
 * Part 2 of the program is responsible for checking semantic errors in book records
 * from various CSV files and serializing valid records into binary files.
 *
 * The program reads data from CSV files, performs validation checks on each record,
 * such as verifying ISBN formats and price validity, and then writes valid records
 * to binary files. Any errors encountered during the validation process are written
 * to a semantic error file.
 */
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class part2 {

    public static void do_part2(){
        PrintWriter pw_semantic = null;

        // create semantic error file
        try{
            pw_semantic = new PrintWriter(new FileOutputStream("semantic_error_file.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("Error: Could not create output file to write to.");
            return;
        }
        
        // array of input files 
        String[] inputFiles ={"Cartoons_Comics.csv", "Hobbies_Collectibles.csv", "Movies_TV_Books.csv", "Music_Radio_Books.csv", "Nostalgia_Eclectic_Books.csv", "Old_Time_Radio_Books.csv", "Sports_Sports_Memorabilia.csv", "Trains_Planes_Automobiles.csv"};

        // array of corresponding output files(binary)
        String[] outputFiles = {"Cartoons_Comics.csv.ser", "Hobbies_Collectibles.csv.ser", "Movies_TV_Books.csv.ser", "Music_Radio_Books.csv.ser", "Nostalgia_Eclectic_Books.csv.ser", "Old_Time_Radio_Books.csv.ser", "Sports_Sports_Memorabilia.csv.ser", "Trains_Planes_Automobiles.csv.ser"};

        Scanner fileScanner = null;
        
        //process each genre file
        for(int i=0; i<inputFiles.length; i++){
            try{
                fileScanner = new Scanner(new FileInputStream(inputFiles[i]));
            }
            catch(FileNotFoundException e){
                System.out.println(inputFiles[i] + " could not be opened.");
                continue; // go to next file
            }

            // big temporary array to hold all valid books
            Book[] books = new Book[1000000];
            int bookCount = 0;

            // read file line by line
            while(fileScanner.hasNextLine()){
                String line = fileScanner.nextLine();
                boolean isValid = true;

                // if line is completely empty, skip it
                if(line.trim().isEmpty()){
                    continue;
                }

                // parse line into the 6 fields
                String[] fields = parseLine(line);

                // extract and trim main fields needed for check
                String title = fields[0].trim();
                String authors = fields[1].trim();
                String priceStr = fields[2].trim();
                String isbn = fields[3].trim();
                String genre = fields[4].trim();
                String yearStr = fields[5].trim();

                // check price
                double price = Double.parseDouble(priceStr);
                try{
                    if(price<0){
                        throw new BadPriceException();
                    }
                }
                catch(BadPriceException e){
                    isValid = false;
                    pw_semantic.println("semantic error in file: " + inputFiles[i]);
                    pw_semantic.println("====================");
                    pw_semantic.println("Error: invalid price");
                    pw_semantic.println("Record: " + line);
                    pw_semantic.println();
                }

                // check year 
                int year = Integer.parseInt(yearStr);
                try{
                    if(year<1995 || year>2010){
                        throw new BadYearException();
                    }
                }
                catch(BadYearException e){
                    isValid = false;
                    pw_semantic.println("semantic error in file: " + inputFiles[i]);
                    pw_semantic.println("====================");
                    pw_semantic.println("Error: invalid year");
                    pw_semantic.println("Record: " + line);
                    pw_semantic.println("");

                }

                // check isbn's
                // check if 10 digit isbn 
                if(isbn.length() == 10){
                    try{
                        if(!isValidIsbn10(isbn)){// call helper method
                            throw new BadIsbn10Exception();
                        }
                    }
                    catch(BadIsbn10Exception e){
                        isValid = false;
                        pw_semantic.println("semantic error in file: " + inputFiles[i]);
                        pw_semantic.println("====================");
                        pw_semantic.println("Error: invalid 10-digit ISBN");
                        pw_semantic.println("Record: " + line);
                        pw_semantic.println("");
                    }
                }
                // check if 13 digit isbn
                else if(isbn.length() ==13){
                    try{
                        if(!isValidIsbn13(isbn)){// call helper method
                            throw new BadIsbn13Exception();
                        }
                    }
                    catch(BadIsbn13Exception e){
                        isValid = false;
                        pw_semantic.println("semantic error in file: " + inputFiles[i]);
                        pw_semantic.println("====================");
                        pw_semantic.println("Error: invalid 13-digit ISBN");
                        pw_semantic.println("Record: " + line);
                        pw_semantic.println("");
                    }
                }
                // wrong isbn length
                else{
                    isValid = false;
                    pw_semantic.println("semantic error in file: " + inputFiles[i]);
                    pw_semantic.println("====================");
                    pw_semantic.println("Error: invalid ISBN length");
                    pw_semantic.println("Record: " + line);
                    pw_semantic.println("");
                }

                // if any error happened, skip book creation
                if (!isValid) {
                    continue;
                }

                // if no semantic error create Book object and add to array
                Book b = new Book(title, authors, price, isbn, genre, year);
                books[bookCount] = b;
                bookCount++;
            }

            // close scanner
            if(fileScanner != null){
                fileScanner.close();
            }

            // serialize valid Book[] into a binary file
            ObjectOutputStream oos = null;
            try{
                oos = new ObjectOutputStream(new FileOutputStream(outputFiles[i]));
                
                // make a smaller array with only the valid books
                Book[] finalBooks = new Book[bookCount];
                for(int j=0; j<bookCount;j++){
                    finalBooks[j] = books[j];
                }

                // write array of books into binary file
                oos.writeObject(finalBooks);
            } 
            catch (IOException e){
                System.out.println("Error: could not write to " + outputFiles[i]);
            }
            finally{
                if(oos != null){
                    try{
                        oos.close();
                    }
                    catch(IOException e){}
                }
            }
        }
        // close writer
        if(pw_semantic !=null){
            pw_semantic.close();
        }
    }

    // helper method to parse line from CSV into 6 fields
    private static String[] parseLine(String line){
        String[] fields = new String[6];

        // if title is surrounded by quotes
        if(line.contains("\"")){
            int firstQuote = line.indexOf("\"");
            int lastQuote = line.lastIndexOf("\"");

            //extract title and assign
            String title = line.substring(firstQuote + 1, lastQuote);
            fields[0] = title;

            // get everything after the closing quote and skip comma
            String restOfLine = line.substring(lastQuote + 2);

            //split the rest and -1 to preserve last empty field as well 
            String[] rest = restOfLine.split(",", -1);

            // filling array of fields with right values
            for(int j=1; j<fields.length;j++){
                int restIndex = j-1; // rest[0] while fields[1], shift by one since title was already added
                if(restIndex<rest.length){ // check if corresponding element in rest
                    fields[j] = rest[restIndex];
                }
                else{ // handle empty fields
                    fields[j] = "";
                }
            }
        }
        else{ //if title is NOT surrounded by quotes
            String[] fieldsTemp = line.split(",", -1); // temporary array, split entire line
            for (int j = 0; j < fields.length; j++) {
                if (j < fieldsTemp.length) {
                    fields[j] = fieldsTemp[j];
                } else {// handle empty fields
                    fields[j] = "";
                }
            }
        }
        return fields;
    } 

    // helper method to validate 10-digit isbn's
    private static boolean isValidIsbn10(String isbn){
        if(isbn.length() !=10){// check if 10 digits
            return false;
        }

        int sum =0;
        for(int i=0; i<10; i++){
            int digit = Character.getNumericValue(isbn.charAt(i));// get i'th character and convert to int
            sum +=(10-i)* digit;
        }
        return (sum% 11==0); // return true if sum is divisible by 11
    }

    // helper method to validate 13-digit isbn's
    private static boolean isValidIsbn13(String isbn){
        if(isbn.length() !=13){// check if 13 digits
            return false;
        }

        int sum =0;
        for(int i=0; i<13; i++){
            int digit = Character.getNumericValue(isbn.charAt(i));// get i'th character and convert to int
            if(i%2 ==0){// checks if even
                sum += digit;
            }
            else{
                sum += 3*digit;
            }
        }
        return (sum% 10==0);// return true if sum is divisible by 10
    }
}                   



        
