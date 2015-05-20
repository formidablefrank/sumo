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
 * Annotation record for the Doppler centroid quality information.
 * 
 * <p>Java class for dopplerCentroidQualityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dopplerCentroidQualityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dcMethod" type="{}dcMethodType"/>
 *         &lt;element name="dopplerCentroidUncertainFlag" type="{}bool"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dopplerCentroidQualityType", propOrder = {
    "dcMethod",
    "dopplerCentroidUncertainFlag"
})
public class DopplerCentroidQualityType {

    @XmlElement(required = true)
    protected DcMethodType dcMethod;
    protected boolean dopplerCentroidUncertainFlag;

    /**
     * Gets the value of the dcMethod property.
     * 
     * @return
     *     possible object is
     *     {@link DcMethodType }
     *     
     */
    public DcMethodType getDcMethod() {
        return dcMethod;
    }

    /**
     * Sets the value of the dcMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link DcMethodType }
     *     
     */
    public void setDcMethod(DcMethodType value) {
        this.dcMethod = value;
    }

    /**
     * Gets the value of the dopplerCentroidUncertainFlag property.
     * 
     */
    public boolean isDopplerCentroidUncertainFlag() {
        return dopplerCentroidUncertainFlag;
    }

    /**
     * Sets the value of the dopplerCentroidUncertainFlag property.
     * 
     */
    public void setDopplerCentroidUncertainFlag(boolean value) {
        this.dopplerCentroidUncertainFlag = value;
    }

}