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
 *         &lt;element ref="{}swath"/>
 *         &lt;element ref="{}azimuthTime"/>
 *         &lt;element ref="{}firstLineSensingTime"/>
 *         &lt;element ref="{}lastLineSensingTime"/>
 *         &lt;element ref="{}prf"/>
 *         &lt;element ref="{}bitErrorCount"/>
 *         &lt;element ref="{}downlinkValues"/>
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
    "swath",
    "azimuthTime",
    "firstLineSensingTime",
    "lastLineSensingTime",
    "prf",
    "bitErrorCount",
    "downlinkValues"
})
@XmlRootElement(name = "downlinkInformation")
public class DownlinkInformation {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String swath;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String azimuthTime;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String firstLineSensingTime;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String lastLineSensingTime;
    protected double prf;
    @XmlElement(required = true)
    protected BitErrorCount bitErrorCount;
    @XmlElement(required = true)
    protected DownlinkValues downlinkValues;

    /**
     * Gets the value of the swath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwath() {
        return swath;
    }

    /**
     * Sets the value of the swath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwath(String value) {
        this.swath = value;
    }

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
     * Gets the value of the firstLineSensingTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstLineSensingTime() {
        return firstLineSensingTime;
    }

    /**
     * Sets the value of the firstLineSensingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstLineSensingTime(String value) {
        this.firstLineSensingTime = value;
    }

    /**
     * Gets the value of the lastLineSensingTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastLineSensingTime() {
        return lastLineSensingTime;
    }

    /**
     * Sets the value of the lastLineSensingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastLineSensingTime(String value) {
        this.lastLineSensingTime = value;
    }

    /**
     * Gets the value of the prf property.
     * 
     */
    public double getPrf() {
        return prf;
    }

    /**
     * Sets the value of the prf property.
     * 
     */
    public void setPrf(double value) {
        this.prf = value;
    }

    /**
     * Gets the value of the bitErrorCount property.
     * 
     * @return
     *     possible object is
     *     {@link BitErrorCount }
     *     
     */
    public BitErrorCount getBitErrorCount() {
        return bitErrorCount;
    }

    /**
     * Sets the value of the bitErrorCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BitErrorCount }
     *     
     */
    public void setBitErrorCount(BitErrorCount value) {
        this.bitErrorCount = value;
    }

    /**
     * Gets the value of the downlinkValues property.
     * 
     * @return
     *     possible object is
     *     {@link DownlinkValues }
     *     
     */
    public DownlinkValues getDownlinkValues() {
        return downlinkValues;
    }

    /**
     * Sets the value of the downlinkValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link DownlinkValues }
     *     
     */
    public void setDownlinkValues(DownlinkValues value) {
        this.downlinkValues = value;
    }

}
