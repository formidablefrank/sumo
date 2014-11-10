//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.24 at 04:43:09 PM CEST 
//


package jrc.it.annotation.reader.jaxb;

import java.math.BigInteger;
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
 *         &lt;element ref="{}baqBlockLength"/>
 *         &lt;element ref="{}echoFormat"/>
 *         &lt;element ref="{}noiseFormat"/>
 *         &lt;element ref="{}calibrationFormat"/>
 *         &lt;element ref="{}meanBitRate"/>
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
    "baqBlockLength",
    "echoFormat",
    "noiseFormat",
    "calibrationFormat",
    "meanBitRate"
})
@XmlRootElement(name = "dataFormat")
public class DataFormat {

    @XmlElement(required = true)
    protected BigInteger baqBlockLength;
    @XmlElement(required = true)
    protected String echoFormat;
    @XmlElement(required = true)
    protected String noiseFormat;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String calibrationFormat;
    protected double meanBitRate;

    /**
     * Gets the value of the baqBlockLength property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBaqBlockLength() {
        return baqBlockLength;
    }

    /**
     * Sets the value of the baqBlockLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBaqBlockLength(BigInteger value) {
        this.baqBlockLength = value;
    }

    /**
     * Gets the value of the echoFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEchoFormat() {
        return echoFormat;
    }

    /**
     * Sets the value of the echoFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEchoFormat(String value) {
        this.echoFormat = value;
    }

    /**
     * Gets the value of the noiseFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoiseFormat() {
        return noiseFormat;
    }

    /**
     * Sets the value of the noiseFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoiseFormat(String value) {
        this.noiseFormat = value;
    }

    /**
     * Gets the value of the calibrationFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalibrationFormat() {
        return calibrationFormat;
    }

    /**
     * Sets the value of the calibrationFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalibrationFormat(String value) {
        this.calibrationFormat = value;
    }

    /**
     * Gets the value of the meanBitRate property.
     * 
     */
    public double getMeanBitRate() {
        return meanBitRate;
    }

    /**
     * Sets the value of the meanBitRate property.
     * 
     */
    public void setMeanBitRate(double value) {
        this.meanBitRate = value;
    }

}
