package com.example.jason.groupapp.timetable;

/**
 * Created by Cécile Journeaux on 12/04/2016.
 * Project created for Abertay University.
 * Contact details: cecile.journeaux@gmail.com
 */

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Event implements Comparable {

	public static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	/* ========================================
	 * 		Private attributes
	 * ========================================
	 */
    /** the date at which the lesson starts */
    private GregorianCalendar dateStart;
    /** the date at which the lesson ends */
    private GregorianCalendar dateEnd;
    /** the location of the lesson */
    private String location;
    /** the name of the lesson */
    private String className;
    /** the type of lesson (lecture, practical, etc) */
    private String classType;
    /** the surname of the lesson's teacher */
    private String teacherSurname;
    /** the first name of the lesson's teacher */
    private String teacherFirstname;

	/* ========================================
	 * 		Constructor
	 * ========================================
	 */
    /**
     * @param dateStart the date at which the lesson starts
     * @param dateEnd the date at which the lesson ends
     * @param location the location of the lesson
     * @param className the name of the lesson
     * @param classType the type of lesson (lecture, practical, etc)
     * @param teacherSurname the surname of the lesson's teacher
     * @param teacherFirstname the first name of the lesson's teacher
     */
    public Event(GregorianCalendar dateStart, GregorianCalendar dateEnd, String location, String className,
                  String classType, String teacherSurname, String teacherFirstname) {
        super();
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.location = location;
        this.className = className;
        this.classType = classType;
        this.teacherSurname = teacherSurname.toUpperCase();
        // TODO we should verify composed names, such as "Charles-edouard" for example
		/*String tempFirstLetter = teacherFirstname.substring(0, 1).toUpperCase();
		String tempRestOfName = teacherFirstname.substring(1).toLowerCase();
		this.teacherFirstname = tempFirstLetter + tempRestOfName;*/
        this.teacherFirstname = teacherFirstname;

    } // ---------------------------------------- VEvent()

    /* ========================================
     * 		Event's own methods
     * ========================================
     */
    public String displayClass () {
        String message = "Next lesson: " + className;
        message += " (" + classType + ")";
        message += " in " + location;
        //message += " ~ " + teacherFirstname + " " + teacherSurname;
        //TODO check if Toast on two lines is possible
        return message;
    } // ---------------------------------------- displayClass()

    /* ========================================
     * 		Comparable's implemented methods
     * ========================================
     */
    @Override
    public int compareTo(Object o) {

        int comparison = 0;
        if( o instanceof Event ) {
            Event ve = (Event)o;
            comparison = dateStart.compareTo( ve.dateStart );
            if ( comparison == 0 ) {
                comparison = dateEnd.compareTo( ve.dateEnd );
            }
        }
        else {
            comparison = -1;
        }
        return comparison;

    } // ---------------------------------------- compareTo()

	/* ========================================
	 * 		Date converters
	 * ========================================
	 */
    public static String getDateToString ( GregorianCalendar gregDate ) {
        return DATE_FORMAT.format( gregDate.getTime() );
    }
    public static GregorianCalendar getStringToDate ( String stringDate ) {
        Date tmpDate = null;
        try {
            tmpDate = DATE_FORMAT.parse(stringDate);
        } catch (ParseException e) {
            Log.e("Event", e.getMessage());
        }
        GregorianCalendar gregDate = (GregorianCalendar)GregorianCalendar.getInstance();
        gregDate.setTime(tmpDate);
        return gregDate;
    }

    /* ========================================
	 * 		Getters
	 * ========================================
	 */
    /**
     * @return the dateStart
     */
    public GregorianCalendar getDateStart() {
        return dateStart;
    }

    /**
     * @return the dateEnd
     */
    public GregorianCalendar getDateEnd() {
        return dateEnd;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the classType
     */
    public String getClassType() {
        return classType;
    }

    /**
     * @return the teacherSurname
     */
    public String getTeacherSurname() {
        return teacherSurname;
    }

    /**
     * @return the teacherFirstname
     */
    public String getTeacherFirstname() {
        return teacherFirstname;
    }

	/* ========================================
	 * 		Setters
	 * ========================================
	 */
    /**
     * @param dateStart the dateStart to set
     */
    public void setDateStart(GregorianCalendar dateStart) {
        this.dateStart = dateStart;
    }

    /**
     * @param dateEnd the dateEnd to set
     */
    public void setDateEnd(GregorianCalendar dateEnd) {
        this.dateEnd = dateEnd;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @param classType the classType to set
     */
    public void setClassType(String classType) {
        this.classType = classType;
    }

    /**
     * @param teacherSurname the teacherSurname to set
     */
    public void setTeacherSurname(String teacherSurname) {
        this.teacherSurname = teacherSurname;
    }

    /**
     * @param teacherFirstname the teacherFirstname to set
     */
    public void setTeacherFirstname(String teacherFirstname) {
        this.teacherFirstname = teacherFirstname;
    }

}

