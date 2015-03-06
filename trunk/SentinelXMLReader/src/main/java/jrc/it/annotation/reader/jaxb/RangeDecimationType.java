//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.05 at 04:36:39 PM CET 
//


package jrc.it.annotation.reader.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Annotation record to record the decimation of the radar data.
 * 
 * <p>Java class for rangeDecimationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rangeDecimationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="decimationFilterBandwidth" type="{}double"/>
 *         &lt;element name="samplingFrequencyAfterDecimation" type="{}double"/>
 *         &lt;element name="filterLength" type="{}uint32"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rangeDecimationType", propOrder = {
    "decimationFilterBandwidth",
    "samplingFrequencyAfterDecimation",
    "filterLength"
})
public class RangeDecimationType {

    @XmlElement(required = true)
    protected Double decimationFilterBandwidth;
    @XmlElement(required = true)
    protected Double samplingFrequencyAfterDecimation;
    @XmlElement(required = true)
    protected Uint32 filterLength;

    /**
     * Gets the value of the decimationFilterBandwidth property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDecimationFilterBandwidth() {
        return decimationFilterBandwidth;
    }

    /**
     * Sets the value of the decimationFilterBandwidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDecimationFilterBandwidth(Double value) {
        this.decimationFilterBandwidth = value;
    }

    /**
     * Gets the value of the samplingFrequencyAfterDecimation property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSamplingFrequencyAfterDecimation() {
        return samplingFrequencyAfterDecimation;
    }

    /**
     * Sets the value of the samplingFrequencyAfterDecimation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSamplingFrequencyAfterDecimation(Double value) {
        this.samplingFrequencyAfterDecimation = value;
    }

    /**
     * Gets the value of the filterLength property.
     * 
     * @return
     *     possible object is
     *     {@link Uint32 }
     *     
     */
    public Uint32 getFilterLength() {
        return filterLength;
    }

    /**
     * Sets the value of the filterLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Uint32 }
     *     
     */
    public void setFilterLength(Uint32 value) {
        this.filterLength = value;
    }

}