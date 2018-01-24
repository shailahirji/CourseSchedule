/*============================================================================
 Name        :BCSchedule.java
 Author      :Shaila Hirji
 Instructor: Dr Fatma Serce
 Date:January 22nd 2018
 Description : The purpose of this application is to extract course information about courses offered at Bellevue College.
               The data about each course if obtained via the Bellevue College website which is then directed to specific pages of the website using user input.
               The user navigates through the website implicitly by answering prompts about:
                        -Quarter they are interested in
                        -The year they want to base course information on
                        -the initial of the program of their interest
               The application wil then print out the available programs at BC under the specified initial and quarter, from which the user will chose a program of their interest
               The user will also be prompted to enter the course code of the class they are interested in. The program will pull out all courses offered at BC under this course ID.
               It will also list out all the Item number of the course, Title of course, Instructor name and the day the class is iffered.
============================================================================*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BCSchedule {

    public static void main(String[] args) throws IOException {

        //vars for userInput
        String quarter = "";
        String year = "";
        String initial = "";


        Scanner input = new Scanner(System.in);
        System.out.print("Enter Quarter:");
        quarter = input.next();

        // while input isn't a valid quarter, prompt user to enter again
        while(!(quarter.equals("Winter") || quarter.equals("winter") || quarter.equals("Summer") || quarter.equals("summer") || quarter.equals("Winter") || quarter.equals("Fall") || quarter.equals("fall") || quarter.equals("Spring") || quarter.equals("Spring")) || !isLetter(quarter) ) {

            System.out.println("Please enter a valid Quarter. Winter, Spring, Summer or Fall");
            System.out.print("Enter Quarter:");
            quarter = input.next();
        }

        System.out.print("Enter Year:");
        year = input.next();

        // while input isnt a valid year, prompt user to enter again
        while (!isDigit(year)) {
            System.out.println("Please enter a valid year.");
            System.out.print("Enter Year:");
            year = input.next();
        }

        System.out.print("Enter initial for the program:");
        initial = input.next();

        // while input isnt a valid initial, prompt user to enter again
        while (!isLetter(initial)) {
            System.out.println("Please enter a valid initial.");
            System.out.print("Enter initial for the program:");
            initial = input.next();

        }
        System.out.println();

        //now we will extract classes offered during the requested quarter, using course inital through getCourses() method
        // collects course information based on term,year, Course initial
        HashMap<String, String> offeredCourses = getCourses(quarter, year, initial);

        //get offerings of the desired class based on the quarter and year and initial

        getClasses(offeredCourses, quarter, year);

    }

    public static HashMap getCourses(String _quarter, String _year, String _initial) throws IOException {

        URL url = new URL("https://www.bellevuecollege.edu/classes/" + _quarter + _year + "?letter=" + _initial);
        //read text put files with file readers, connect to network, connect to get in byte stream
        //1. set connection
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));//read data in byte from, buffer reader is a wrap giving us chance to read line by line rather char by char
        //2. read
        String data = "";
        String text = "";
        while ((data = in.readLine()) != null) {
            text += data + "\n";
        }


        //grabbing courses under specific letter

        Pattern pattern = Pattern.compile("<a href=.\\/classes\\/\\w*\\/\\w*.>(.*)</a>\\s(\\(.*\\))");
        Matcher matcher = pattern.matcher(text);

        // store the inputs of courses matching the inital in a hashMap, <Course Name, Acronym>
        HashMap<String, String> courseMap = new HashMap<>();

        System.out.println("Programs:");
        while (matcher.find()) {

            System.out.print((matcher.group(1)));//Full name of course based on initial
            System.out.println((matcher.group(2)));//Course Acronyms

            String courseName = matcher.group(1);
            String courseAcronym = matcher.group(2).substring(1, matcher.group(2).length() - 1);//get rid of ()

            String[] split = courseAcronym.split(","); // break course name into just 1st acronym

            courseMap.put(courseName, split[0]); //load courseMap with <courseName, splitAcronym>

        }

        System.out.println();

        return courseMap;

    }

    public static void getClasses(HashMap<String, String> courseMap, String _quarter, String _year) throws IOException {


        System.out.print("Enter the program's name:");
        Scanner input = new Scanner(System.in);
        String programRequest = input.nextLine();

        while (!isLetter(programRequest) || !courseMap.containsKey(programRequest)) {
            System.out.println("Please enter a valid program name from the list above.");
            System.out.print("Enter the program's name:");
            programRequest = input.nextLine();

        }

        //fetch classes data for desired course
        URL url = new URL("https://www.bellevuecollege.edu/classes/" + _quarter + _year + "/" + courseMap.get(programRequest));

        //1. set connection
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));//read data in byte from, buffer reader is a wrap giving us chance to read line by line rather char by char

        //2. read
        String data = "";
        String text = "";
        while ((data = in.readLine()) != null) {
            text += data + "\n";
        }

        System.out.print("Enter Course ID:");
        String courseID = input.nextLine();


        //1. This pattern is used to retrieve data related to the desired course ID ONLY
        Pattern patternClass = Pattern.compile(".*<span class=.*courseID.*>" + courseID + ".*<span class=.courseTitle.>(.*)</span>[\\S\\s]*?<h2 class=.*classHeading.*");
        Matcher matcherClass = patternClass.matcher(text);

        String title = "";

        //2.This pattern is used to retrieve the course that has only one offering using a slightly different regex than the one above
        Pattern patternOnlyClass = Pattern.compile(".*<span class=.*courseID.*>" + courseID + ".*<span class=.courseTitle.>(.*)</span>[\\S\\s]*?<h2>General Information</h2>");
        Matcher matcherOnlyClass = patternOnlyClass.matcher(text);

        //check if course ID requested by user exists at Bellevue College
        // reduce html script to only course ID related information, grab the desired course's course title

        boolean courseFound = false;

        while (matcherClass.find()) { //this loop picks out all the class offered under desired course id, if the course id exists
            courseFound = true;
            text = matcherClass.group(0);// reduce html script to only that relevant to course info
            title = matcherClass.group(1);//extract title of course, if found
        }

        //if the above pattern (patternClass) fails,there might only be 1 class that's offered try this pattern

        if (!courseFound) {
            while (matcherOnlyClass.find()) {
                courseFound = true;
                text = matcherOnlyClass.group(0);// reduce html script to only that relevant to course info

                // System.out.println("Title:" + matcherClass.group(1));// get tittle of course
                title = matcherOnlyClass.group(1);
            }
        }

        //no course was found?
        if (!courseFound) {
            System.out.println("Sorry. This course is not offered at Bellevue College");
            return;
        }


//3. This pattern looks at course data as individual items, only selects data till the href with the Instructor's name
        //extract Item# , Instructor's name for this class and Day's it is offered
        Pattern patternItem = Pattern.compile("Item number: </span>([0-9]*)</span>[\\s\\S]*?<a href=(.*)");
        Matcher matcherItem = patternItem.matcher(text);

        //helper variables

        int start = 0;//marks beginning of data used to extract class schedule
        int count = 0;//keeps cound of how many different class offerings have been retrieved
        int end = 0;//marks end of data used to extract class schedule
        String textDays = "";//variable used to store information about what days each specific class is offered

        // int temp = 0;

        //4. This pattern is used to match classes that are offered online
        Pattern patternOnline = Pattern.compile("<span class=.days online.>(\\w*)</span>");
        Matcher matcherOnline;

        //5. this pattern isused to match classes that are offered on campus during days of the week
        Pattern patternCampus = Pattern.compile("<span class=.days.>\\s*<abbr title=.(\\w*/?\\w*/?\\w*/?\\w*/?\\w*)[\\s\\S]");
        Matcher matcherCampus;

        System.out.println();
        System.out.println(programRequest + " Courses in " + _quarter + " " + _year);
        System.out.println("==============================================");

        //Fence post approach used to solve the complication encountered when extracting the days the classes are offered from html text

        while (matcherItem.find()) { //if an item(Course offering) is found

            count++;

            if (count == 1) { //This enables us to print the first class offered, or the ONLY class offered

                start = matcherItem.end();//mark position where the current course's href link ends

                System.out.println("Code:" + courseID);
                System.out.println("Item #: " + matcherItem.group(1));
                System.out.println("Title: " + title);

                //use href link to extract instructor name
                String instructorName = matcherItem.group(2).substring(matcherItem.group(2).indexOf('>') + 1, (matcherItem.group(2).indexOf('<')));
                System.out.println("Instructor: " + instructorName);

                //if only one course under this course code, we have all the data to print days the course is offered
                textDays = text;
            }

            if (count >= 2) {// if more than one class is being offered for the desired course

                end = matcherItem.start();//mark the end of first class/item's days schedule

                //select info between these 2 indexes from initial text. By doing so, we are only selecting the data from html that contains the class schedule for a specific class
                textDays = text.substring(start, end);

                //initialize the Online and Campus class patterns using the condensed text
                matcherOnline = patternOnline.matcher(textDays);
                matcherCampus = patternCampus.matcher(textDays);

                //if class is offered on campus
                while (matcherCampus.find()) {
                    System.out.println("Days: " + matcherCampus.group(1));
                }

                //if class is offered online
                while (matcherOnline.find()) {
                    System.out.println("Days: " + matcherOnline.group(1));
                }

                System.out.println("==============================================");
                System.out.println("Code:" + courseID);
                System.out.println("Item #: " + matcherItem.group(1));
                System.out.println("Title: " + title);

                //use href link to extract instructor name

                String instructorName = matcherItem.group(2).substring(matcherItem.group(2).indexOf('>') + 1, (matcherItem.group(2).indexOf('<')));
                System.out.println("Instructor: " + instructorName);
                start = matcherItem.end();//re assign starting point for the day schedule of next class
            }
        }

        textDays=text.substring(start,text.length());

        // Print information about the  the last most class or the only class (if only one class is offered)
        matcherOnline = patternOnline.matcher(textDays);
        matcherCampus = patternCampus.matcher(textDays);

        //for classes offered on campus
        while (matcherCampus.find()) {

            System.out.println("Days: " + matcherCampus.group(1));
        }
        //for classes offered online
        while (matcherOnline.find()) {
            //for online classes
            System.out.println("Days: " + matcherOnline.group(1));
        }
        System.out.println("==============================================");


    }

    /*
    This method returns a boolean value and takes in a String parameter
    This method is a helper method used to check whether or not user input are qualified digits
     */
    public static boolean isDigit(String digit) {
        char c;

        // Iterate through the string one number at a time.
        for (int i = 0; i < digit.length(); i++) {
            c = digit.charAt(i);         // Get a char from string
            // if it's NOT within these bounds, then it's not a character
            if (!(c >= '0' && c <= '9')) {
                return false;        //number used instead of alphabet
            }
        }
        return true;        //all ok!
    }

    /*
    This method returns a boolean value and takes in a String parameter
    This method is a helper method used to check whether or not user input are qualified letters or words
     */

    public static boolean isLetter(String text) {
        char c;

        // Iterate through the string one letter at a time.
        for (int i = 0; i < text.length(); i++) {
            c = text.charAt(i);         // Get a char from string
            // if it's NOT within these bounds, then it's not a character
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c ==' '))) {
                return false;        //number used instead of alphabet
            }
        }

        return true;        //all ok!

    }
}

