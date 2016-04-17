package com.example.jason.groupapp.timetable;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cécile Journeaux on 12/04/2016.
 * Project created for Abertay University.
 * Contact details: cecile.journeaux@gmail.com
 */
public class RegexTask extends AsyncTask<File, Integer, Void> {

    private Context context;
    private ProgressBar progressBar;

    public RegexTask( Context context, ProgressBar progressBar ) {
        this.context = context;
        this.progressBar = progressBar;
    }

    /* ==========================================
     *      Task's lifecycle
     * ==========================================
     */
    @Override
    protected void onPreExecute() {
        Log.w("Regex", "Start");
        // clearing previous data in database
        DatabaseHelper DB = DatabaseHelper.getInstance( context );
        DB.cleanData();
        progressBar.setProgress(0);
    }

    @Override
    protected Void doInBackground(File... params) {

        int count;
        File calendarFile = params[0];
        try {

            Log.w("Regex", "Running");

            // fetching data from file
            CalendarReader calReader = new CalendarReader( calendarFile );
            Log.w("Regex", "Start of reading file");
            ArrayList<Event> classes = calReader.dataSeeder(); // TODO
            //ArrayList<Event> classes = calReader.readCalendar();
            Log.w("Regex", "End of reading file");
            // storing data in database
            DatabaseHelper DB = DatabaseHelper.getInstance( context );
            Integer[] progressData = new Integer[ 2 ];
            progressData[0] = classes.size();
            for ( int i = 0; i < classes.size(); i++ ) {
                DB.addEvent( classes.get( i ) );
                Log.w("ADDED", classes.get(i).displayClass());
                progressData[1] = i;
                publishProgress(progressData);
            }

            // deleting file
            calendarFile.delete();
            Log.w("Regex", "Completed");

        } catch (FileNotFoundException e) {
            Log.e("Regex", e.getMessage());
        } catch (IOException e) {
            Log.e("Regex", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.w("Regex", "Finished");
        if ( context instanceof TimetableActivity ) {
            ((TimetableActivity) context).finish();
        } else {
            Log.e("Regex", "Activity can't finish");
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.w("Regex", "Running..."+ values[1]);
        progressBar.setMax(values[0]);
        progressBar.setProgress(values[1]);
    }


    /* ==========================================
     *      Private class
     * ==========================================
     */
    private class CalendarReader {

        private File sourceFile;


        private final String TO_THE_LINE = "\n";//"\\s\\s";
        private final String REGEX_VEVENT = "BEGIN:VEVENT" + TO_THE_LINE + "(\\s||\\S)*" + "END:VEVENT";

        // the markers that will allow to delimit blocks of data
        private final String START_DATE = "DTSTART:";
        private final String END_DATE = "DTEND:";
        private final String LOCATION = "LOCATION:";
        private final String END_LOCATION = TO_THE_LINE + "UID:";
        private final String SUMMARY = "SUMMARY:";
        private final String END_SUMMARY = TO_THE_LINE + "DESCRIPTION:Module";

        // the regular expressions to extract the interesting blocks of data
        // at least one character between the two markers
        private final String REGEX_DATE_START = START_DATE + "((\\s||\\S)+)" + END_DATE;
        private final String REGEX_DATE_END  = END_DATE + "((\\s||\\S)+)" + LOCATION;
        private final String REGEX_LOCATION  = LOCATION + "((\\s||\\S)+)" + END_LOCATION;
        private final String REGEX_TEACHER_SURNAME = "";
        private final String REGEX_TEACHER_FIRSTNAME = "";

        // finding the class type and name and teacher's names
        final String SEMICOLON = "\\\\;\\s";
        // a slash mark, a semicolon and a whitespace character
        final String ANY_CHARACTER = "\\s||\\S";
        // any whitespace or non-whitespace character
        final String MULTI_PART = "((" + ANY_CHARACTER + "||("+ SEMICOLON+"))+)";
        // a group of at least one ANY_CHARACTER or SEMICOLON

        final String REGEX_CLASS = MULTI_PART;
        // a group of at least one ANY_CHARACTER or SEMICOLON
        final String REGEX_TYPE = SEMICOLON + "((" + ANY_CHARACTER + ")+)" + SEMICOLON;
        // a group preceded with a semi colon followed by a whitespace containing at least one character and ending with a semicolon and a whitespace
        final String REGEX_TEACHER = "(("+ANY_CHARACTER+")+)" + "\\\\,\\s" + "(("+ANY_CHARACTER+")+)";



        /**
         * @param calendarFile
         */
        public CalendarReader(File calendarFile) throws FileNotFoundException {

            if ( calendarFile.exists()) {
                this.sourceFile = calendarFile;
            } else {
                this.sourceFile = null;
                throw new FileNotFoundException( "The file " + calendarFile.getAbsolutePath() + " could not be found." );
            }
        } // ---------------------------------------- CalendarReader()

        public ArrayList<Event> readCalendar() {

            ArrayList<Event> classes = new ArrayList<Event>();

            Log.e( "Read calendar", "Step 1" );
            try {
                // convert content of file into a CharSequence
                FileInputStream input = new FileInputStream( sourceFile );
                FileChannel channel = input.getChannel();

                // Create a read-only CharBuffer on the file
                ByteBuffer bbuf = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int) channel.size());
                CharBuffer cbuf = Charset.forName( "UTF-8" ).newDecoder().decode(bbuf);

                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                String line;
                String entireFile = "";
                try {
                    while((line = br.readLine()) != null) { // <--------- place readLine() inside loop
                        entireFile += (line + "\n"); // <---------- add each line to entireFile
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //Log.w("Test file", cbuf.toString());

                // Create matcher on file
                // we select all the characters between the begin and end tags of a VEvent in the file
                Pattern pattern = Pattern.compile( REGEX_VEVENT );
                Matcher matcher = pattern.matcher( /*cbuf*/entireFile );

                Log.e( "Read calendar", "Step 2" );
                Log.e( "Read calendar", REGEX_VEVENT );
                Log.e( "Read calendar", entireFile );
                // Find all matches in the file
                while ( matcher.find() ) {
                    Log.e( "Read calendar", "Step 2.0" );
                    // Get the matching string and create the VEvent from it
                    classes.add( extractClass( matcher.group() ) );
                    Log.e("Read calendar", "Step 3");
                } // -------------------- end of while ()
            } // -------------------- end of try ()
            catch ( IOException ex ) {
                Log.e( "Regex", ex.getMessage() );
            }

            // sorting the VEvents prior returning them
            Collections.sort( classes );
            return classes;

        } // ---------------------------------------- readClasses()

        private Event extractClass ( String theData ) {

            Log.e( "Read calendar", "Step 2.1" );

            //Log.e( "Read calendar", theData );

            // attributes to create from the data
            GregorianCalendar dateStart = null;
            GregorianCalendar dateEnd = null;
            String location = "";
            String className = "";
            String classType = "";
            String teacherSurname = "";
            String teacherFirstname = "";

            Pattern pattern;
            Matcher matcher;
            // finding the start date
            pattern = Pattern.compile(REGEX_DATE_START);
            matcher = pattern.matcher(theData);
            Log.e( "Read calendar", "Step 2.2" );
            if ( matcher.find() ) {
                Log.e("Read calendar", "Date : " + matcher.group( 1 ) );
                dateStart = extractDate( matcher.group( 1 ) );
            }
            Log.e( "Read calendar", "Step 2.3" );
            // finding the end date
            pattern = Pattern.compile( REGEX_DATE_END );
            matcher = pattern.matcher( theData );
            if ( matcher.find() ) {
                dateEnd = extractDate( matcher.group( 1 ) );
            }
            // finding the location
            pattern = Pattern.compile( REGEX_LOCATION );
            matcher = pattern.matcher( theData );
            if ( matcher.find() ) {
                location = cleanSemicolons( matcher.group( 1 ) );
                //System.out.println( "Location : " + location );
            }

            final String REGEX_LOCATION_BIS = //LOCATION + "((\\s||\\S)+)" + END_LOCATION;
                    location.charAt( 0 ) + MULTI_PART + SEMICOLON;
            //
            // (ANY_CHARACTER+) => a group containing at least one ANY_CHARACTER
            final String REGEX_CLASS_SUMMARY = SUMMARY + REGEX_CLASS + REGEX_TYPE + REGEX_LOCATION_BIS + "(((" + REGEX_TEACHER  + ")||" + SEMICOLON + ")+)" + END_SUMMARY;

            //System.out.println( REGEX_CLASS_SUMMARY);
            pattern = Pattern.compile( REGEX_CLASS_SUMMARY );
            matcher = pattern.matcher( theData );
            if ( matcher.find() ) {

                // finding the class name
                String tempClassName = cleanSemicolons( cleanSpaces(matcher.group(1)) );
                // replacing semicolons by hyphens
                className = tempClassName.replaceAll( "\\\\;\\s", " - " );
                // finding the class type
                classType = cleanSpaces( matcher.group( 4 ) );
                //System.err.println( "Class name : " + className );

                // TODO there can be more than one teacher
                // finding the teacher surname
                teacherSurname = cleanSpaces( matcher.group( 12 ) );
                // finding the teacher first name
                teacherFirstname = cleanSpaces( matcher.group( 15 ) );
                //System.err.println( teacherSurname + "~" + teacherFirstname ); // TODO find the first name, and cut correctly the surname*/

            }

            Event newEvent = new Event(dateStart, dateEnd, location, className, classType, teacherSurname, teacherFirstname );
            Log.d( "Regex", newEvent.displayClass() );
            return newEvent;

        }

        private GregorianCalendar extractDate ( String data ) {

            GregorianCalendar date = null;
            // check that the data correpsonds indeed to a formatted date
            if ( data.length() >= 16 && data.charAt( 8 ) == 'T' && data.charAt( 15 ) == 'Z' ) {

                // extracting the different elements of the date
                int year = Integer.parseInt( data.substring( 0, 3 ) );
                int month = Integer.parseInt( data.substring( 4, 5 ) );
                int day = Integer.parseInt( data.substring( 6, 7 ) );
                int hour = Integer.parseInt( data.substring( 9, 10 ) );
                int minutes = Integer.parseInt( data.substring( 11, 12 ) );
                // creating the GregorianCalendar object that stores the date
                date = new GregorianCalendar( year, month, day, hour, minutes );

            } else {
                // this should never happen
                throw new IllegalArgumentException();
            }

            return date;
        }

        /**
         * cleans a string of data by removing the "chariot return" character of the ics file
         * @param rawString the String of data that goes to the line
         * @return a String on one line
         */
        private String cleanSpaces ( String rawString ) {

            String cleanedString = rawString.replaceAll( "\\s\\s\\s", "" );

            return cleanedString;

        }

        /**
         * cleans a string of data by removing the semicolons character of the ics file
         * @param rawString the String of data with a slash mark and a semicolon
         * @return a String with an hyphen where there was a semicolon
         */
        private String cleanSemicolons ( String rawString ) {

            String cleanedString = rawString.replaceAll( "\\\\;\\s", " & " );
            return cleanedString;

        }



        private ArrayList<Event> dataSeeder () {

            ArrayList<Event> classes = new ArrayList<Event>();

    /*    GregorianCalendar dateStart
        GregorianCalendar dateEnd
        String location
        String className
        String classType
        String teacherSurname
        String teacherFirstname*/

            // Group project - practical
            classes.add( new Event( Event.getStringToDate("2016-04-12 08:00:00"),Event.getStringToDate("2016-04-12 10:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Group Project", "Practical", "ARCHIBALD", "JACKIE" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-19 08:00:00"),Event.getStringToDate("2016-04-19 10:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Group Project", "Practical", "ARCHIBALD", "JACKIE" ) );
            // Server-side web development - Practical
            classes.add( new Event( Event.getStringToDate("2016-04-11 12:00:00"),Event.getStringToDate("2016-04-11 14:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Server-Side Web Development", "Practical", "LUND", "GEOFFREY" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-18 12:00:00"),Event.getStringToDate("2016-04-18 14:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Server-Side Web Development", "Practical", "LUND", "GEOFFREY" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-25 12:00:00"),Event.getStringToDate("2016-04-25 14:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Server-Side Web Development", "Practical", "LUND", "GEOFFREY" ) );
            // Server-side web development - Lecture
            classes.add( new Event( Event.getStringToDate("2016-04-11 09:00:00"),Event.getStringToDate("2016-04-11 10:00:00"), "3006", "Server-Side Web Development", "Lecture", "LUND", "GEOFFREY" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-18 09:00:00"),Event.getStringToDate("2016-04-18 10:00:00"), "3006", "Server-Side Web Development", "Lecture", "LUND", "GEOFFREY" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-25 09:00:00"),Event.getStringToDate("2016-04-25 10:00:00"), "3006", "Server-Side Web Development", "Lecture", "LUND", "GEOFFREY" ) );
            // AI - Practical
            classes.add( new Event( Event.getStringToDate("2016-04-14 12:00:00"),Event.getStringToDate("2016-04-14 14:00:00"), "4506 Pods H - K", "Decision Support Systems ~ Intelligent Systems", "Practical", "KING", "DAVID J" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-21 12:00:00"),Event.getStringToDate("2016-04-21 14:00:00"), "4506 Pods H - K", "Decision Support Systems ~ Intelligent Systems", "Practical", "KING", "DAVID J" ) );
            // AI - Lecture
            classes.add( new Event( Event.getStringToDate("2016-04-11 10:00:00"),Event.getStringToDate("2016-04-11 11:00:00"), "4506 Pods H - K", "Decision Support Systems ~ Intelligent Systems", "Lecture", "KING", "DAVID J" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-18 10:00:00"),Event.getStringToDate("2016-04-18 11:00:00"), "4506 Pods H - K", "Decision Support Systems ~ Intelligent Systems", "Lecture", "KING", "DAVID J" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-25 10:00:00"),Event.getStringToDate("2016-04-25 11:00:00"), "4506 Pods H - K", "Decision Support Systems ~ Intelligent Systems", "Lecture", "KING", "DAVID J" ) );
            // Network Programming for Mobile Technology - Lecture
            classes.add( new Event( Event.getStringToDate("2016-04-12 10:00:00"),Event.getStringToDate("2016-04-12 11:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Network Programming for Mobile Technology", "Lecture", "BOIKO", "ANDREI" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-19 10:00:00"),Event.getStringToDate("2016-04-19 11:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Network Programming for Mobile Technology", "Lecture", "BOIKO", "ANDREI" ) );
            // Network Programming for Mobile Technology - Practical
            classes.add( new Event( Event.getStringToDate("2016-04-12 14:00:00"),Event.getStringToDate("2016-04-12 16:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Network Programming for Mobile Technology", "Practical", "BOIKO", "ANDREI" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-19 14:00:00"),Event.getStringToDate("2016-04-19 16:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Network Programming for Mobile Technology", "Practical", "BOIKO", "ANDREI" ) );
            classes.add( new Event( Event.getStringToDate("2016-04-26 14:00:00"),Event.getStringToDate("2016-04-26 16:00:00"), "4506 Pods A - G ~ 4506 Pods H - K", "Network Programming for Mobile Technology", "Practical", "BOIKO", "ANDREI" ) );

            return classes;
        }
    }


}

