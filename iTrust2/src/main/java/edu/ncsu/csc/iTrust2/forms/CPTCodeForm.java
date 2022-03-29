package edu.ncsu.csc.iTrust2.forms;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * Form used when adding CPT codes. Used to help create CPT codes and has all of
 * the same fields and methods.
 *
 * @author Leah Whaley
 *
 */
public class CPTCodeForm {

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
     * Empty constructor for GSON
     */
    public CPTCodeForm () {

    }

    /**
     * Create and fill out a form based on an existing CPT code
     *
     * @param code
     *            the code to create a form of.
     */
    public CPTCodeForm ( final CPTCode code ) {
        setCode( code.getCode() );
        setDescription( code.getDescription() );
        setCost( code.getCost() );
        setVersion( code.getVersion() );
        setIsArchived( code.getIsArchived() );
        setTimeRangeMin( code.getTimeRangeMin() );
        setTimeRangeMax( code.getTimeRangeMax() );
    }

    /**
     * Sets the id of the CPT code to the parameter.
     *
     * @param id
     *            the value to set the id to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the id of the CPT code
     *
     * @return the id of the CPT code
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the code field of the CPT code to the parameter
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
     * @return the value of the code field of the CPT code
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
     * @return the cost of the CPT code in cents
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
     *            the value to set the isArchived field of the CPT code to
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
     *            the value to set the minimum of the time range to
     */
    public void setTimeRangeMin ( final int min ) {
        this.timeRangeMin = min;
    }

    /**
     * Gets the minimum of the time range of the CPT code
     *
     * @return the minimum of the time range of the CPT code
     */
    public int getTimeRangeMin () {
        return timeRangeMin;
    }

    /**
     * Sets the maximum of the time range of the CPT code to the parameter
     *
     * @param max
     *            the maximum of the time range of the CPT code
     */
    public void setTimeRangeMax ( final int max ) {
        this.timeRangeMax = max;
    }

    /**
     * Gets the maximum of the time range of the CPT code
     *
     * @return the maximum of the time range of the CPT code
     */
    public int getTimeRangeMax () {
        return timeRangeMax;
    }

    /**
     * Generates the hashcode of the CPT code and includes every field except
     * for the id field.
     *
     * @return the hashcode of the CPT code
     */
    @Override
    public int hashCode () {
        return Objects.hash( code, cost, description, isArchived, timeRangeMax, timeRangeMin, version );
    }

    /**
     * Checks if two CPTCodeForms are equals. Checks every field except for id.
     *
     * @obj the object to check for equality
     * @return true if the given object is equal to the current CPTCodeForm and
     *         false otherwise
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
        final CPTCodeForm other = (CPTCodeForm) obj;
        return Objects.equals( code, other.code ) && cost == other.cost
                && Objects.equals( description, other.description ) && isArchived == other.isArchived
                && timeRangeMax == other.timeRangeMax && timeRangeMin == other.timeRangeMin
                && Objects.equals( version, other.version );
    }

}
