//-------------------------------------------------------------------------
//Assignment 2
//Part: 3
//Written by: Shekinah Nagarasa 40287073 
//-------------------------------------------------------------------------

/**
 * Assignment Information:
 *
 * Shekinah Nagarasa - 40287073 
 * Course: COMP249
 * Assignment #2
 * Due Date: 17 November 2025
 *
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

/**
 * This class provides functionality for displaying and interacting with serialized book data.
 * It allows the user to view, select, and navigate through different book files containing serialized books.
 *
 * It offers a main menu that allows the user to either view the selected file, select a different file, or exit the program.
 * The selected file's records can be viewed sequentially or navigated backward, based on user input.
 *
 * This program assumes that the binary files are properly serialized and contain Book objects.
 */

public class part3 {

    public static void do_part3(){
        // array holding 8 ser files
        String[] binaryFiles = {"Cartoons_Comics.csv.ser", "Hobbies_Collectibles.csv.ser", "Movies_TV_Books.csv.ser", "Music_Radio_Books.csv.ser", "Nostalgia_Eclectic_Books.csv.ser", "Old_Time_Radio_Books.csv.ser", "Sports_Sports_Memorabilia.csv.ser", "Trains_Planes_Automobiles.csv.ser"};

        // array to store each deserialized Book array for each file
        Book[][] bookCollections = new Book[binaryFiles.length][];

        // current index for each file when viewing (one index per file)
        int[] currentIndexes = new int[binaryFiles.length];

        //deserialize all 8 binary files
        for(int i=0; i<binaryFiles.length; i++){
            ObjectInputStream ois = null;
            try{
                ois = new ObjectInputStream(new FileInputStream(binaryFiles[i]));
                Book[] books = (Book[]) ois.readObject(); // reads object and casts it to a Book[] array
                bookCollections[i] = books; // store whole array of books in bookcollections
            }
            catch(FileNotFoundException e){
                System.out.println("Error: could not open " + binaryFiles[i]);
                bookCollections[i] = new Book[0]; // use empty array instead of null to avoid later crashes
            }
            catch(IOException e){
                System.out.println("Error: reading from " + binaryFiles[i]);
                bookCollections[i] = new Book[0];// use empty array instead of null to avoid later crashes
            }
            catch(ClassNotFoundException e){
                System.out.println("Error: class not found when reading: " + binaryFiles[i]);
                bookCollections[i] = new Book[0];// use empty array instead of null to avoid later crashes
            }
            finally{
                if(ois != null){
                    try{
                        ois.close();
                    }
                    catch(IOException e){}
                }
            }
        }

        Scanner sc = new Scanner(System.in);

        int selectedFileIndex = 0;
        boolean exit = false;

        // main loop runs until user chooses to exit
        while(!exit){
            int recordCount = 0;
            if(bookCollections[selectedFileIndex] != null){
                recordCount = bookCollections[selectedFileIndex].length;
            }

            // main menu
            System.out.println("\n-----------------------------");
            System.out.println("Main Menu");
            System.out.println("-----------------------------");
            System.out.println("v View the selected file: " + binaryFiles[selectedFileIndex] + " (" + recordCount + " records)");
            System.out.println("s Select a file to view");
            System.out.println("x Exit");
            System.out.println("-----------------------------");
            System.out.print("Enter your choice: ");

            String choice = sc.next();//collect user's choice

            // exit program
            if(choice.equalsIgnoreCase("x")){
                System.out.println("Exiting program . . .");
                exit = true;
            }
            // select file option 
            else if(choice.equalsIgnoreCase("s")){
                System.out.println("\n-----------------------------");
                System.out.println("File Sub-Menu");
                System.out.println("-----------------------------");
                for(int i=0; i<binaryFiles.length;i++){//for loop to print menu options 1-8
                    int count = 0;
                    if(bookCollections[i] !=null){
                        count = bookCollections[i].length;// count nbr of records per file
                    }
                    System.out.println((i+1) + " " + binaryFiles[i] + " (" + count + " records)");// menu starts at 1
                }        
                // print exit option           
                System.out.println("9 Exit");
                System.out.println("-----------------------------");
                System.out.print("Enter your choice: ");

                int fileChoice = sc.nextInt();

                // if valid nbr selected, update selected file
                if(fileChoice >=1 && fileChoice<=8){
                    selectedFileIndex = fileChoice -1;
                }
                else if(fileChoice ==9){
                    continue;// goes back to start of main while loop
                }
                else{
                    System.out.println("Invalid file choice.");
                }
            }
            // view selected file
            else if(choice.equalsIgnoreCase("v")){
                Book[] currentBooks = bookCollections[selectedFileIndex];
                if (currentBooks == null || currentBooks.length == 0) {
                    System.out.println("The selected file is empty or could not be loaded.");
                    continue; // if null skip printing, go back to main menu
                }

                int currentIndex = currentIndexes[selectedFileIndex];
                
                boolean viewing = true;
                while(viewing){
                    System.out.println("\n-----------------------------");
                    System.out.println("Viewing: " + binaryFiles[selectedFileIndex] + " (" + currentBooks.length + " records)");
                    System.out.println("-----------------------------");
                
                    // Ask user how many records to display
                    System.out.print("Enter the number of records you want to display, or 0 to return to the main menu: ");
                    int n = sc.nextInt();

                    if(n==0){
                        //end viewing, go back to main menu
                        currentIndexes[selectedFileIndex] = currentIndex;//save current record index for next time
                        viewing = false;
                        break;
                    }

                    int len = currentBooks.length;// shortcut 

                    if(n>0){
                        int start = currentIndex;
                        int end = currentIndex + (n-1);

                        if( end>=len){// if end goes beyond the array, correct it back to last index
                            end = len-1;
                        }

                        for(int j= start; j<=end;j++){// display all records start to end
                            System.out.println("\nRecord " + (j+1) + ":");
                            System.out.println(currentBooks[j]);// calls toString method to print all book infos
                        }

                        if(currentIndex + (n-1) >= len){
                            System.out.println("\nEOF has been reached");
                        }

                        currentIndex = end;// update 
                    }
                    else if (n < 0){
                        int step =-n;
                        int start = currentIndex -(step-1);
                        int end = currentIndex;// ends with current object

                        if(start <0){// if start goes beyond the array, correct it back to first index
                            start = 0;
                        }

                        if(currentIndex -(step-1) <0){
                            System.out.println("\nBOF has been reached");
                        }

                        for(int j=start; j<=end; j++){// display all records start to end
                            System.out.println("\nRecord " + (j+1)+ ":");
                            System.out.println(currentBooks[j]);// calls toString method to print all book infos
                        }

                        currentIndex = start;// update
                    }
                    else{
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        }
        sc.close();
    }
}

