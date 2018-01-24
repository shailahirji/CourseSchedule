/*============================================================================
 Name        :ExtractingVariable.java
 Author      :Shaila Hirji
 Instructor: Dr Fatma Serce
 Date:January 22nd 2018
 Description : The purpose of this program is to extract all variables from within the file A.java that contains some simple java code.
                The program uses Matcher and Pattern class from Java's API
============================================================================*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractingVariables {

    public static void main(String[] args) throws FileNotFoundException {

        // read the input file
        String text = readInputToFile();

        //declare the pattern to run the code text file against
        Pattern pattern = Pattern.compile("([A-Za-z]*)\\s*([A-Za-z]*)\\s?(=(.+))?;");//maybe a space followed by maybe an =

        //("([A-Za-z]*)\\s*([A-Za-z]*)\\s?=?(.+);"); if we do this, why doesn't it work? it pick private and static?
        /*
        we can use \\w* instead of A-Z.. but since it's var names and java
        doesn't allow random chars, better to use a-z and 0-9,
        making sure java syntax rules are being followed
         */

//assign the matcher to the desired pattern and text that the pattern will run against
        Matcher matcher = pattern.matcher(text);

        //as the matcher finds a match to the desired pattern in the text file, print it out in terms of what each tokoen represents in the A.java file
        while (matcher.find()) {
            System.out.println("Type: " + matcher.group(1));
            System.out.println("Variable name: " + matcher.group(2));
            System.out.println("Value:" + matcher.group(4));//change to 3 when testing not working code
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        }

    }


    /*This method is a helper method that returns a String and take in a string as a parameter
    /The method reads off a Java file (A.java), extracts all of its contents and adds them to a string enabling us to process it using regex
    */
    public static String readInputToFile() throws FileNotFoundException {

        File file = new File("A.java");
        Scanner scanner = new Scanner(file);
        //read file into string
        String input = "";
        String text = "";//stores all input
        while (scanner.hasNextLine()) {
            input = scanner.nextLine();
            text += input + "\n";
        }

        return text;


    }

}


