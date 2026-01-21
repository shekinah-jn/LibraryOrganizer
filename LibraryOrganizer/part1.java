//-------------------------------------------------------------------------
//Assignment 2
//Part: 1
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
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

/**
 * This class processes csv files containing book data and checks for syntax errors.
 * It handles too many fields, too few fields, missing fields, invalid genre types, and writes valid entries to specific csv files.
 */

public class part1 {
    
    public static void do_part1(){
        //initializing 
        int numberOfFiles = 0;
        String[] fileNames = null;
        Scanner sc = null;

        // reading the input file list (part1_input_file_names.txt) 
        try{
            sc = new Scanner(new FileInputStream("Comp249_F25_Assg2/part1_input_file_names.txt"));
            numberOfFiles = sc.nextInt(); //storing number of files to read 
            sc.nextLine(); // move scanner to next line 
            fileNames = new String[numberOfFiles];

            for(int i=0; i<fileNames.length; i++){
                fileNames[i] = sc.nextLine();// read each file name and store it
            }
        }
        catch(FileNotFoundException e){
            System.out.println("Error: File could not be found");
            return;
        }

        if(sc!= null){
            sc.close();
        }

        // Create output files 
        PrintWriter pw_cartoons = null;
        PrintWriter pw_hobbies = null;
        PrintWriter pw_movies = null;
        PrintWriter pw_music = null;
        PrintWriter pw_nostalgia = null;
        PrintWriter pw_old = null;
        PrintWriter pw_sports = null;
        PrintWriter pw_trains = null;
        PrintWriter pw_syntax = null;

        try{
            pw_cartoons = new PrintWriter(new FileOutputStream("Cartoons_Comics.csv"));
            pw_hobbies = new PrintWriter(new FileOutputStream("Hobbies_Collectibles.csv"));
            pw_movies = new PrintWriter(new FileOutputStream("Movies_TV_Books.csv"));
            pw_music = new PrintWriter(new FileOutputStream("Music_Radio_Books.csv"));
            pw_nostalgia = new PrintWriter(new FileOutputStream("Nostalgia_Eclectic_Books.csv"));
            pw_old = new PrintWriter(new FileOutputStream("Old_Time_Radio_Books.csv"));
            pw_sports = new PrintWriter(new FileOutputStream("Sports_Sports_Memorabilia.csv"));
            pw_trains = new PrintWriter(new FileOutputStream("Trains_Planes_Automobiles.csv"));
            pw_syntax = new PrintWriter(new FileOutputStream("syntax_error_file.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("Error: Could not create output file to write to.");
            return;
        }

        //processing each csv files
        Scanner fileScanner = null;
        boolean isValidRecord = true;

        for(int i=0; i<fileNames.length; i++){
            try{
                fileScanner = new Scanner(new FileInputStream("Comp249_F25_Assg2/" + fileNames[i]));

                while(fileScanner.hasNextLine()){
                    String line = fileScanner.nextLine();
                    isValidRecord = true;

                    // if title is surrounded by quotes
                    if(line.contains("\"")){
                        int firstQuote = line.indexOf("\"");
                        int lastQuote = line.lastIndexOf("\"");

                        //extract title
                        String title = line.substring(firstQuote + 1, lastQuote);
                    
                        // get everything after the closing quote and skip comma 
                        String restOfLine = line.substring(lastQuote +2);
                        
                        // count how many commas in rest of line
                        int commaCount = 0;
                        for(int j=0; j<restOfLine.length();j++){
                            if(restOfLine.charAt(j) == ','){
                                commaCount++;
                            }
                        }

                        // check for too many fields
                        try{
                            if(commaCount >4){
                                throw new TooManyFieldsException();
                            }
                        }
                        catch(TooManyFieldsException e){
                            isValidRecord = false;
                            pw_syntax.println("syntax error in file: " + fileNames[i]);
                            pw_syntax.println("====================");
                            pw_syntax.println("Error: too many fields");
                            pw_syntax.println("Record: " + line);
                            pw_syntax.println();
                            continue; // Skip to the next line if there are too many fields
                        }

                        // check for too few fields
                        try{
                            if(commaCount<4){
                                throw new TooFewFieldsException();
                            }
                        }
                        catch(TooFewFieldsException e){
                            isValidRecord = false;
                            pw_syntax.println("syntax error in file: " + fileNames[i]);
                            pw_syntax.println("====================");
                            pw_syntax.println("Error: too few fields");            
                            pw_syntax.println("Record: " + line);
                            pw_syntax.println();
                            continue; // Skip to the next line if there are too few fields
                        }

                        // if correct number of commas
                        if(commaCount ==4){
                            String[] fields = new String[6];
                            fields[0] = title;
                            String[] rest = restOfLine.split(",", -1);//split the rest and -1 to preserve last empty field as well 

                            // filling array of fields with right values
                            for(int j=1; j<fields.length; j++){
                                int restIndex = j-1; // rest[0] while fields[1]
                                if(restIndex<rest.length){ // check if corresponding element in rest
                                    fields[j] = rest[restIndex];
                                }
                                else{ // handle empty fields
                                    fields[j] = "";
                                }
                            }
                            String genre = fields[4].trim();// get genre code, trim spaces

                            //check for missing fields
                            for(int j=0; j<fields.length; j++){
                                try{
                                    if(fields[j].trim().isEmpty()){ //check if any field is missing
                                        throw new MissingFieldException();
                                    }
                                }
                                catch(MissingFieldException e){
                                    isValidRecord = false;
                                    pw_syntax.println("syntax error in file: " + fileNames[i]);
                                    pw_syntax.println("====================");

                                    switch (j){
                                        case 0:
                                            pw_syntax.println("Error: missing title");
                                            break;
                                        case 1: 
                                            pw_syntax.println("Error: missing authors");
                                            break;
                                        case 2:
                                            pw_syntax.println("Error: missing price");
                                            break;
                                        case 3: 
                                            pw_syntax.println("Error: missing isbn");
                                            break;
                                        case 4:
                                            pw_syntax.println("Error: missing genre");
                                            break;
                                        case 5:
                                            pw_syntax.println("Error: missing year");
                                            break;
                                    }
                                    pw_syntax.println("Record: " + line);
                                    pw_syntax.println();
                                    break;// stop checking for more missing fields
                                }

                            }
                            // if any field was missing, don't try to categorize this record
                            if(!isValidRecord){
                                continue;
                            }

                            // validate genre
                            try{
                                if(!(genre.equals("CCB") || genre.equals("HCB") || genre.equals("MTV") || genre.equals("MRB") || genre.equals("NEB") || genre.equals("OTR") || genre.equals("SSM") || genre.equals("TPA"))){
                                    throw new UnknownGenreException();
                                }
                            }
                            catch(UnknownGenreException e){
                                isValidRecord = false;
                                pw_syntax.println("syntax error in file: " + fileNames[i]);
                                pw_syntax.println("====================");
                                pw_syntax.println("Error: invalid genre");
                                pw_syntax.println("Record: " + line);
                                pw_syntax.println();
                                continue; // skip to next line
                            }

                            if(isValidRecord){
                                switch (genre){
                                    case "CCB":
                                        pw_cartoons.println(line);
                                        break;
                                    case "HCB":
                                        pw_hobbies.println(line);
                                        break;
                                    case "MTV":
                                        pw_movies.println(line);
                                        break;
                                    case "MRB": 
                                        pw_music.println(line);
                                        break;
                                    case "NEB":
                                        pw_nostalgia.println(line);
                                        break;
                                    case "OTR":
                                        pw_old.println(line);
                                        break;
                                    case "SSM":
                                        pw_sports.println(line);
                                        break;
                                    case "TPA":
                                        pw_trains.println(line);
                                        break;
                                }
                            }
                        }
                    }
                    //if title is NOT surrounded by quotes
                    else{
                        // count how many commas in line
                        int commaCount = 0;
                        for (int j=0; j<line.length(); j++) {
                            if (line.charAt(j) == ',') {
                                commaCount++;
                            }
                        }

                        // check for too many fields
                        try{
                            if(commaCount >5){
                                throw new TooManyFieldsException();
                            }
                        }
                        catch(TooManyFieldsException e){
                            isValidRecord = false;
                            pw_syntax.println("syntax error in file: " + fileNames[i]);
                            pw_syntax.println("====================");
                            pw_syntax.println("Error: too many fields");
                            pw_syntax.println("Record: " + line);
                            pw_syntax.println();
                            continue; // Skip to the next line if there are too many fields
                        }

                        // check for too few fields
                        try{
                            if(commaCount<5){
                                throw new TooFewFieldsException();
                            }
                        }
                        catch(TooFewFieldsException e){
                            isValidRecord = false;
                            pw_syntax.println("syntax error in file: " + fileNames[i]);
                            pw_syntax.println("====================");
                            pw_syntax.println("Error: too few fields");            
                            pw_syntax.println("Record: " + line);
                            pw_syntax.println();
                            continue; // Skip to the next line if there are too few fields
                        }

                        // if correct number of commas
                        if(commaCount ==5){
                            String[] fields = line.split(",",-1); // Split and store the fields and -1 to preserve last empty field as well
                            String genre = fields[4].trim();// get genre code, trim spaces

                            //check for missing fields
                            for(int j=0; j<fields.length; j++){
                                try{
                                    if(fields[j].trim().isEmpty()){ //check if any field is missing
                                        throw new MissingFieldException();
                                    }
                                }
                                catch(MissingFieldException e){
                                    isValidRecord = false;
                                    pw_syntax.println("syntax error in file: " + fileNames[i]);
                                    pw_syntax.println("====================");

                                    switch (j){
                                        case 0:
                                            pw_syntax.println("Error: missing title");
                                            break;
                                        case 1: 
                                            pw_syntax.println("Error: missing authors");
                                            break;
                                        case 2:
                                            pw_syntax.println("Error: missing price");
                                            break;
                                        case 3: 
                                            pw_syntax.println("Error: missing isbn");
                                            break;
                                        case 4:
                                            pw_syntax.println("Error: missing genre");
                                            break;
                                        case 5:
                                            pw_syntax.println("Error: missing year");
                                            break;
                                    }
                                    pw_syntax.println("Record: " + line);
                                    pw_syntax.println();
                                    break;// stop checking for more missing fields
                                }

                            }
                            // if any field was missing, don't try to categorize this record
                            if(!isValidRecord){
                                continue;
                            }

                            // validate genre
                            try{
                                if(!(genre.equals("CCB") || genre.equals("HCB") || genre.equals("MTV") || genre.equals("MRB") || genre.equals("NEB") || genre.equals("OTR") || genre.equals("SSM") || genre.equals("TPA"))){
                                    throw new UnknownGenreException();
                                }
                            }
                            catch(UnknownGenreException e){
                                isValidRecord = false;
                                pw_syntax.println("syntax error in file: " + fileNames[i]);
                                pw_syntax.println("====================");
                                pw_syntax.println("Error: invalid genre");
                                pw_syntax.println("Record: " + line);
                                pw_syntax.println();
                                continue;// skip to next line
                            }

                            // write valid record to correct file
                            if(isValidRecord){
                                switch (genre){
                                    case "CCB":
                                        pw_cartoons.println(line);
                                        break;
                                    case "HCB":
                                        pw_hobbies.println(line);
                                        break;
                                    case "MTV":
                                        pw_movies.println(line);
                                        break;
                                    case "MRB": 
                                        pw_music.println(line);
                                        break;
                                    case "NEB":
                                        pw_nostalgia.println(line);
                                        break;
                                    case "OTR":
                                        pw_old.println(line);
                                        break;
                                    case "SSM":
                                        pw_sports.println(line);
                                        break;
                                    case "TPA":
                                        pw_trains.println(line);
                                        break;
                                }
                            }
                        }
                    }
                }
                // close scanner
                if(fileScanner!=null){
                    fileScanner.close();
                }
            }
            catch(FileNotFoundException e){
                System.out.println(fileNames[i] + " could not be opened");
            }
        }// close writers 
        pw_cartoons.close();
        pw_hobbies.close();
        pw_movies.close();
        pw_music.close();
        pw_nostalgia.close();
        pw_old.close();
        pw_sports.close();
        pw_trains.close();
        pw_syntax.close();

    }
}
