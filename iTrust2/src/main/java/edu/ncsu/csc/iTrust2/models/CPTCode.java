package edu.ncsu.csc.iTrust2.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;

/**
 * Class for CPT Codes. A CPT Code is stored as a Long value and has a
 * description, a cost, a version, a boolean value for if it is archived, and
 * two values for the time range associated with it. If the code has no time
 * range associated with it, both of those values would be zero.
 *
 * @author Leah Whaley
 *
 */
@Entity
public class CPTCode extends DomainObject {

    /**
     * ID of this CPTCode
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long    id;

    /**
     * Value of the CPTCode. Number that is associated with the code
     */
    private long    code;

    /**
     * Description of the CPTCode
     */
    private String  description;

    /**
     * The price associated with this CPTCode
     */
    private long    cost;

    /**
     * The version number of this CPTCode
     */
    private String  version;

    /**
     * If the code is archived or active
     */
    private boolean isArchived;

    /**
     * The minimum of the time range associated with this code.
     */
    private int     timeRangeMin;

    /**
     * The maximum of the time range associated with this code.
     */
    private int     timeRangeMax;

    /**
     * Empty constructor for Hibernate
     */
    public CPTCode () {

    }

    /**
     * Constructs a CPT code from a CPTCodeForm. Checks appropriate values from
     * the form to make sure they are valid before creating a CPT code. If any
     * value is not valid, an illegal argument exception is thrown.
     *
     * @param form
     *            The form to get the values from.
     */
    public CPTCode ( final CPTCodeForm form ) {

        // Validates the code field
        if ( form.getCode() > 0 ) {
            setCode( form.getCode() );
        }
        else {
            throw new IllegalArgumentException( "CPT Code must be a positive number: " + form.getCode() );
        }

        // Validates the description field
        if ( form.getDescription().length() < 250 ) {
            setDescription( form.getDescription() );
        }
        else {
            throw new IllegalArgumentException( "Description length must be less than 250 characters!" );
        }

        // Validates the cost field
        if ( form.getCost() > 0 ) {
            setCost( form.getCost() );
        }
        else {
            throw new IllegalArgumentException( "Cost must be a positive number: " + form.getCost() );
        }

        // Validates the version field
        if ( form.getVersion() != null ) {
            final String s = form.getVersion();
            for ( int i = 0; i < s.length(); i++ ) {
                if ( !Character.isDigit( s.charAt( i ) ) && s.charAt( i ) != '.' ) {
                    throw new IllegalArgumentException( "Versions can only have digits or decimal points: " + s );
                }
            }
            setVersion( s );
        }

        // Validates the minimum time range field
        if ( form.getTimeRangeMin() > 0 ) {
            setTimeRangeMin( form.getTimeRangeMin() );
        }
        else {
            throw new IllegalArgumentException(
                    "The minimum number of minutes for the time range has to be positive: " + form.getTimeRangeMin() );
        }

        // Validates the maximum time range field
        if ( form.getTimeRangeMax() > 0 && form.getTimeRangeMax() > timeRangeMin ) {
            setTimeRangeMax( form.getTimeRangeMax() );
        }
        else {
            throw new IllegalArgumentException(
                    "The maximum number of minutes for the time range has to be positive and greater than the minimum for the time range: "
                            + form.getTimeRangeMax() );
        }

    }

    /**
     * Sets the id of the CPTCode to the parameter.
     *
     * @param id
     *            the value to set the id to.
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the id of the CPT code
     *
     * @return the id of the CPT code
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the code of the CPT code to the given parameter
     *
     * @param code
     *            the value to set the code field to
     */
    public void setCode ( final long code ) {
        this.code = code;
    }

    /**
     * Gets the code field of the CPT code
     *
     * @return the code field of the CPT code
     */
    public long getCode () {
        return code;
    }

    /**
     * Sets the description of the CPT code to the parameter
     *
     * @param description
     *            the value to set the description to
     */
    public void setDescription ( final String description ) {
        this.description = description;
    }

    /**
     * Gets the description of the CPT code
     *
     * @return the description of the CPT code
     */
    public String getDescription () {
        return description;
    }

    /**
     * Sets the cost of the CPT code to the parameter
     *
     * @param price
     *            the value to set the cost to
     */
    public void setCost ( final long price ) {
        this.cost = price;
    }

    /**
     * Gets the cost of the CPT code
     *
     * @return the cost of the CPT code
     */
    public long getCost () {
        return cost;
    }

    /**
     * Sets the version of the CPT code to the parameter
     *
     * @param version
     *            the value to set the version to
     */
    public void setVersion ( final String version ) {
        this.version = version;
    }

    /**
     * Gets the version of the CPT code
     *
     * @return the version of the CPT code
     */
    public String getVersion () {
        return version;
    }

    /**
     * Sets the isArchived field of the CPT code to the parameter
     *
     * @param archived
     *            the value to set the isArchived field to
     */
    public void setIsArchived ( final boolean archived ) {
        this.isArchived = archived;
    }

    /**
     * Gets the isArchived field of the CPT code
     *
     * @return the value of the isArchived field of the CPT code
     */
    public boolean getIsArchived () {
        return isArchived;
    }

    /**
     * Sets the minimum of the time range of the CPT code to the parameter
     *
     * @param min
     *            the value to set the field to
     */
    public void setTimeRangeMin ( final int min ) {
        this.timeRangeMin = min;
    }

    /**
     * Gets the minimum of the time range of the CPT code
     *
     * @return the minimum amount of minutes in the time range of the CPT code
     */
    public int getTimeRangeMin () {
        return timeRangeMin;
    }

    /**
     * Sets the maximum of the time range of the CPT code to the parameter
     *
     * @param max
     *            the value to set the field to
     */
    public void setTimeRangeMax ( final int max ) {
        this.timeRangeMax = max;
    }

    /**
     * Gets the maximum of the time range of the CPT code
     *
     * @return the maximum amount of minutes in the time range of the CPT code
     */
    public int getTimeRangeMax () {
        return timeRangeMax;
    }

    /**
     * Generates the hashcode of the CPT code including all of the fields except
     * for the id field
     *
     * @return the hashcode of the CPT code
     */
    @Override
    public int hashCode () {
        return Objects.hash( code, cost, description, isArchived, timeRangeMax, timeRangeMin, version );
    }

    /**
     * Checks if the parameter object is equal to the CPT code. This checks all
     * fields except for the id field.
     *
     * @param obj
     *            the object to check for equality
     * @return true if it is equal and false otherwise
     */
    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final CPTCode other = (CPTCode) obj;
        return Objects.equals( code, other.code ) && cost == other.cost
                && Objects.equals( description, other.description ) && isArchived == other.isArchived
                && timeRangeMax == other.timeRangeMax && timeRangeMin == other.timeRangeMin
                && Objects.equals( version, other.version );
    }

}
