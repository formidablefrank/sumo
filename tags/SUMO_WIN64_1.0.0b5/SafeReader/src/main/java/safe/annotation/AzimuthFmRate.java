//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.24 at 04:43:09 PM CEST 
//


package safe.annotation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}azimuthTime"/>
 *         &lt;element ref="{}t0"/>
 *         &lt;element ref="{}c0"/>
 *         &lt;element ref="{}c1"/>
 *         &lt;element ref="{}c2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "azimuthTime",
    "t0",
    "c0",
    "c1",
    "c2"
})
@XmlRootElement(name = "azimuthFmRate")
public class AzimuthFmRate {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String azimuthTime;
    protected double t0;
    protected double c0;
    protected double c1;
    protected double c2;

    /**
     * Gets the value of the azimuthTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAzimuthTime() {
        return azimuthTime;
    }

    /**
     * Sets the value of the azimuthTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAzimuthTime(String value) {
        this.azimuthTime = value;
    }

    /**
     * Gets the value of the t0 property.
     * 
     */
    public double getT0() {
        return t0;
    }

    /**
     * Sets the value of the t0 property.
     * 
     */
    public void setT0(double value) {
        this.t0 = value;
    }

    /**
     * Gets the value of the c0 property.
     * 
     */
    public double getC0() {
        return c0;
    }

    /**
     * Sets the value of the c0 property.
     * 
     */
    public void setC0(double value) {
        this.c0 = value;
    }

    /**
     * Gets the value of the c1 property.
     * 
     */
    public double getC1() {
        return c1;
    }

    /**
     * Sets the value of the c1 property.
     * 
     */
    public void setC1(double value) {
        this.c1 = value;
    }

    /**
     * Gets the value of the c2 property.
     * 
     */
    public double getC2() {
        return c2;
    }

    /**
     * Sets the value of the c2 property.
     * 
     */
    public void setC2(double value) {
        this.c2 = value;
    }

}
