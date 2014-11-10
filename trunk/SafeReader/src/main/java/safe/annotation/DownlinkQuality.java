//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.24 at 04:43:09 PM CEST 
//


package safe.annotation;

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
 *         &lt;element ref="{}iInputDataMean"/>
 *         &lt;element ref="{}qInputDataMean"/>
 *         &lt;element ref="{}inputDataMeanOutsideNominalRangeFlag"/>
 *         &lt;element ref="{}iInputDataStdDev"/>
 *         &lt;element ref="{}qInputDataStdDev"/>
 *         &lt;element ref="{}inputDataStDevOutsideNominalRangeFlag"/>
 *         &lt;element ref="{}numDownlinkInputDataGaps"/>
 *         &lt;element ref="{}downlinkGapsInInputDataSignificantFlag"/>
 *         &lt;element ref="{}numDownlinkInputMissingLines"/>
 *         &lt;element ref="{}downlinkMissingLinesSignificantFlag"/>
 *         &lt;element ref="{}numInstrumentInputDataGaps"/>
 *         &lt;element ref="{}instrumentGapsInInputDataSignificantFlag"/>
 *         &lt;element ref="{}numInstrumentInputMissingLines"/>
 *         &lt;element ref="{}instrumentMissingLinesSignificantFlag"/>
 *         &lt;element ref="{}numSsbErrorInputDataGaps"/>
 *         &lt;element ref="{}ssbErrorGapsInInputDataSignificantFlag"/>
 *         &lt;element ref="{}numSsbErrorInputMissingLines"/>
 *         &lt;element ref="{}ssbErrorMissingLinesSignificantFlag"/>
 *         &lt;element ref="{}chirpSourceUsed"/>
 *         &lt;element ref="{}pgSourceUsed"/>
 *         &lt;element ref="{}rrfSpectrumUsed"/>
 *         &lt;element ref="{}replicaReconstructionFailedFlag"/>
 *         &lt;element ref="{}meanPgProductAmplitude"/>
 *         &lt;element ref="{}stdDevPgProductAmplitude"/>
 *         &lt;element ref="{}meanPgProductPhase"/>
 *         &lt;element ref="{}stdDevPgProductPhase"/>
 *         &lt;element ref="{}pgProductDerivationFailedFlag"/>
 *         &lt;element ref="{}invalidDownlinkParamsFlag"/>
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
    "iInputDataMean",
    "qInputDataMean",
    "inputDataMeanOutsideNominalRangeFlag",
    "iInputDataStdDev",
    "qInputDataStdDev",
    "inputDataStDevOutsideNominalRangeFlag",
    "numDownlinkInputDataGaps",
    "downlinkGapsInInputDataSignificantFlag",
    "numDownlinkInputMissingLines",
    "downlinkMissingLinesSignificantFlag",
    "numInstrumentInputDataGaps",
    "instrumentGapsInInputDataSignificantFlag",
    "numInstrumentInputMissingLines",
    "instrumentMissingLinesSignificantFlag",
    "numSsbErrorInputDataGaps",
    "ssbErrorGapsInInputDataSignificantFlag",
    "numSsbErrorInputMissingLines",
    "ssbErrorMissingLinesSignificantFlag",
    "chirpSourceUsed",
    "pgSourceUsed",
    "rrfSpectrumUsed",
    "replicaReconstructionFailedFlag",
    "meanPgProductAmplitude",
    "stdDevPgProductAmplitude",
    "meanPgProductPhase",
    "stdDevPgProductPhase",
    "pgProductDerivationFailedFlag",
    "invalidDownlinkParamsFlag"
})
@XmlRootElement(name = "downlinkQuality")
public class DownlinkQuality {

    protected double iInputDataMean;
    protected double qInputDataMean;
    protected boolean inputDataMeanOutsideNominalRangeFlag;
    protected double iInputDataStdDev;
    protected double qInputDataStdDev;
    protected boolean inputDataStDevOutsideNominalRangeFlag;
    @XmlElement(required = true)
    protected BigInteger numDownlinkInputDataGaps;
    protected boolean downlinkGapsInInputDataSignificantFlag;
    @XmlElement(required = true)
    protected BigInteger numDownlinkInputMissingLines;
    protected boolean downlinkMissingLinesSignificantFlag;
    @XmlElement(required = true)
    protected BigInteger numInstrumentInputDataGaps;
    protected boolean instrumentGapsInInputDataSignificantFlag;
    @XmlElement(required = true)
    protected BigInteger numInstrumentInputMissingLines;
    protected boolean instrumentMissingLinesSignificantFlag;
    @XmlElement(required = true)
    protected BigInteger numSsbErrorInputDataGaps;
    protected boolean ssbErrorGapsInInputDataSignificantFlag;
    @XmlElement(required = true)
    protected BigInteger numSsbErrorInputMissingLines;
    protected boolean ssbErrorMissingLinesSignificantFlag;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String chirpSourceUsed;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String pgSourceUsed;
    @XmlElement(required = true)
    protected String rrfSpectrumUsed;
    protected boolean replicaReconstructionFailedFlag;
    protected double meanPgProductAmplitude;
    protected double stdDevPgProductAmplitude;
    protected double meanPgProductPhase;
    protected double stdDevPgProductPhase;
    protected boolean pgProductDerivationFailedFlag;
    protected boolean invalidDownlinkParamsFlag;

    /**
     * Gets the value of the iInputDataMean property.
     * 
     */
    public double getIInputDataMean() {
        return iInputDataMean;
    }

    /**
     * Sets the value of the iInputDataMean property.
     * 
     */
    public void setIInputDataMean(double value) {
        this.iInputDataMean = value;
    }

    /**
     * Gets the value of the qInputDataMean property.
     * 
     */
    public double getQInputDataMean() {
        return qInputDataMean;
    }

    /**
     * Sets the value of the qInputDataMean property.
     * 
     */
    public void setQInputDataMean(double value) {
        this.qInputDataMean = value;
    }

    /**
     * Gets the value of the inputDataMeanOutsideNominalRangeFlag property.
     * 
     */
    public boolean isInputDataMeanOutsideNominalRangeFlag() {
        return inputDataMeanOutsideNominalRangeFlag;
    }

    /**
     * Sets the value of the inputDataMeanOutsideNominalRangeFlag property.
     * 
     */
    public void setInputDataMeanOutsideNominalRangeFlag(boolean value) {
        this.inputDataMeanOutsideNominalRangeFlag = value;
    }

    /**
     * Gets the value of the iInputDataStdDev property.
     * 
     */
    public double getIInputDataStdDev() {
        return iInputDataStdDev;
    }

    /**
     * Sets the value of the iInputDataStdDev property.
     * 
     */
    public void setIInputDataStdDev(double value) {
        this.iInputDataStdDev = value;
    }

    /**
     * Gets the value of the qInputDataStdDev property.
     * 
     */
    public double getQInputDataStdDev() {
        return qInputDataStdDev;
    }

    /**
     * Sets the value of the qInputDataStdDev property.
     * 
     */
    public void setQInputDataStdDev(double value) {
        this.qInputDataStdDev = value;
    }

    /**
     * Gets the value of the inputDataStDevOutsideNominalRangeFlag property.
     * 
     */
    public boolean isInputDataStDevOutsideNominalRangeFlag() {
        return inputDataStDevOutsideNominalRangeFlag;
    }

    /**
     * Sets the value of the inputDataStDevOutsideNominalRangeFlag property.
     * 
     */
    public void setInputDataStDevOutsideNominalRangeFlag(boolean value) {
        this.inputDataStDevOutsideNominalRangeFlag = value;
    }

    /**
     * Gets the value of the numDownlinkInputDataGaps property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumDownlinkInputDataGaps() {
        return numDownlinkInputDataGaps;
    }

    /**
     * Sets the value of the numDownlinkInputDataGaps property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumDownlinkInputDataGaps(BigInteger value) {
        this.numDownlinkInputDataGaps = value;
    }

    /**
     * Gets the value of the downlinkGapsInInputDataSignificantFlag property.
     * 
     */
    public boolean isDownlinkGapsInInputDataSignificantFlag() {
        return downlinkGapsInInputDataSignificantFlag;
    }

    /**
     * Sets the value of the downlinkGapsInInputDataSignificantFlag property.
     * 
     */
    public void setDownlinkGapsInInputDataSignificantFlag(boolean value) {
        this.downlinkGapsInInputDataSignificantFlag = value;
    }

    /**
     * Gets the value of the numDownlinkInputMissingLines property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumDownlinkInputMissingLines() {
        return numDownlinkInputMissingLines;
    }

    /**
     * Sets the value of the numDownlinkInputMissingLines property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumDownlinkInputMissingLines(BigInteger value) {
        this.numDownlinkInputMissingLines = value;
    }

    /**
     * Gets the value of the downlinkMissingLinesSignificantFlag property.
     * 
     */
    public boolean isDownlinkMissingLinesSignificantFlag() {
        return downlinkMissingLinesSignificantFlag;
    }

    /**
     * Sets the value of the downlinkMissingLinesSignificantFlag property.
     * 
     */
    public void setDownlinkMissingLinesSignificantFlag(boolean value) {
        this.downlinkMissingLinesSignificantFlag = value;
    }

    /**
     * Gets the value of the numInstrumentInputDataGaps property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumInstrumentInputDataGaps() {
        return numInstrumentInputDataGaps;
    }

    /**
     * Sets the value of the numInstrumentInputDataGaps property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumInstrumentInputDataGaps(BigInteger value) {
        this.numInstrumentInputDataGaps = value;
    }

    /**
     * Gets the value of the instrumentGapsInInputDataSignificantFlag property.
     * 
     */
    public boolean isInstrumentGapsInInputDataSignificantFlag() {
        return instrumentGapsInInputDataSignificantFlag;
    }

    /**
     * Sets the value of the instrumentGapsInInputDataSignificantFlag property.
     * 
     */
    public void setInstrumentGapsInInputDataSignificantFlag(boolean value) {
        this.instrumentGapsInInputDataSignificantFlag = value;
    }

    /**
     * Gets the value of the numInstrumentInputMissingLines property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumInstrumentInputMissingLines() {
        return numInstrumentInputMissingLines;
    }

    /**
     * Sets the value of the numInstrumentInputMissingLines property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumInstrumentInputMissingLines(BigInteger value) {
        this.numInstrumentInputMissingLines = value;
    }

    /**
     * Gets the value of the instrumentMissingLinesSignificantFlag property.
     * 
     */
    public boolean isInstrumentMissingLinesSignificantFlag() {
        return instrumentMissingLinesSignificantFlag;
    }

    /**
     * Sets the value of the instrumentMissingLinesSignificantFlag property.
     * 
     */
    public void setInstrumentMissingLinesSignificantFlag(boolean value) {
        this.instrumentMissingLinesSignificantFlag = value;
    }

    /**
     * Gets the value of the numSsbErrorInputDataGaps property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumSsbErrorInputDataGaps() {
        return numSsbErrorInputDataGaps;
    }

    /**
     * Sets the value of the numSsbErrorInputDataGaps property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumSsbErrorInputDataGaps(BigInteger value) {
        this.numSsbErrorInputDataGaps = value;
    }

    /**
     * Gets the value of the ssbErrorGapsInInputDataSignificantFlag property.
     * 
     */
    public boolean isSsbErrorGapsInInputDataSignificantFlag() {
        return ssbErrorGapsInInputDataSignificantFlag;
    }

    /**
     * Sets the value of the ssbErrorGapsInInputDataSignificantFlag property.
     * 
     */
    public void setSsbErrorGapsInInputDataSignificantFlag(boolean value) {
        this.ssbErrorGapsInInputDataSignificantFlag = value;
    }

    /**
     * Gets the value of the numSsbErrorInputMissingLines property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumSsbErrorInputMissingLines() {
        return numSsbErrorInputMissingLines;
    }

    /**
     * Sets the value of the numSsbErrorInputMissingLines property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumSsbErrorInputMissingLines(BigInteger value) {
        this.numSsbErrorInputMissingLines = value;
    }

    /**
     * Gets the value of the ssbErrorMissingLinesSignificantFlag property.
     * 
     */
    public boolean isSsbErrorMissingLinesSignificantFlag() {
        return ssbErrorMissingLinesSignificantFlag;
    }

    /**
     * Sets the value of the ssbErrorMissingLinesSignificantFlag property.
     * 
     */
    public void setSsbErrorMissingLinesSignificantFlag(boolean value) {
        this.ssbErrorMissingLinesSignificantFlag = value;
    }

    /**
     * Gets the value of the chirpSourceUsed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChirpSourceUsed() {
        return chirpSourceUsed;
    }

    /**
     * Sets the value of the chirpSourceUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChirpSourceUsed(String value) {
        this.chirpSourceUsed = value;
    }

    /**
     * Gets the value of the pgSourceUsed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPgSourceUsed() {
        return pgSourceUsed;
    }

    /**
     * Sets the value of the pgSourceUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPgSourceUsed(String value) {
        this.pgSourceUsed = value;
    }

    /**
     * Gets the value of the rrfSpectrumUsed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRrfSpectrumUsed() {
        return rrfSpectrumUsed;
    }

    /**
     * Sets the value of the rrfSpectrumUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRrfSpectrumUsed(String value) {
        this.rrfSpectrumUsed = value;
    }

    /**
     * Gets the value of the replicaReconstructionFailedFlag property.
     * 
     */
    public boolean isReplicaReconstructionFailedFlag() {
        return replicaReconstructionFailedFlag;
    }

    /**
     * Sets the value of the replicaReconstructionFailedFlag property.
     * 
     */
    public void setReplicaReconstructionFailedFlag(boolean value) {
        this.replicaReconstructionFailedFlag = value;
    }

    /**
     * Gets the value of the meanPgProductAmplitude property.
     * 
     */
    public double getMeanPgProductAmplitude() {
        return meanPgProductAmplitude;
    }

    /**
     * Sets the value of the meanPgProductAmplitude property.
     * 
     */
    public void setMeanPgProductAmplitude(double value) {
        this.meanPgProductAmplitude = value;
    }

    /**
     * Gets the value of the stdDevPgProductAmplitude property.
     * 
     */
    public double getStdDevPgProductAmplitude() {
        return stdDevPgProductAmplitude;
    }

    /**
     * Sets the value of the stdDevPgProductAmplitude property.
     * 
     */
    public void setStdDevPgProductAmplitude(double value) {
        this.stdDevPgProductAmplitude = value;
    }

    /**
     * Gets the value of the meanPgProductPhase property.
     * 
     */
    public double getMeanPgProductPhase() {
        return meanPgProductPhase;
    }

    /**
     * Sets the value of the meanPgProductPhase property.
     * 
     */
    public void setMeanPgProductPhase(double value) {
        this.meanPgProductPhase = value;
    }

    /**
     * Gets the value of the stdDevPgProductPhase property.
     * 
     */
    public double getStdDevPgProductPhase() {
        return stdDevPgProductPhase;
    }

    /**
     * Sets the value of the stdDevPgProductPhase property.
     * 
     */
    public void setStdDevPgProductPhase(double value) {
        this.stdDevPgProductPhase = value;
    }

    /**
     * Gets the value of the pgProductDerivationFailedFlag property.
     * 
     */
    public boolean isPgProductDerivationFailedFlag() {
        return pgProductDerivationFailedFlag;
    }

    /**
     * Sets the value of the pgProductDerivationFailedFlag property.
     * 
     */
    public void setPgProductDerivationFailedFlag(boolean value) {
        this.pgProductDerivationFailedFlag = value;
    }

    /**
     * Gets the value of the invalidDownlinkParamsFlag property.
     * 
     */
    public boolean isInvalidDownlinkParamsFlag() {
        return invalidDownlinkParamsFlag;
    }

    /**
     * Sets the value of the invalidDownlinkParamsFlag property.
     * 
     */
    public void setInvalidDownlinkParamsFlag(boolean value) {
        this.invalidDownlinkParamsFlag = value;
    }

}
