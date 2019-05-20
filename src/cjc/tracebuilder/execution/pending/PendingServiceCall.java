package cjc.tracebuilder.execution.pending;

import java.util.Date;

/**
 *  Represents a parsed individual line in our trace log file.  Stored on a parent pending output trace,
 *  collected together with other service calls with the same trace ID.
 */
public class PendingServiceCall {

    private String _origin;
    private String _destination;
    private String _serviceName;
    private Date _startDate;
    private Date _endDate;

    /**
     * Provides the origin service span (from format origin->destination).
     * @return origin service span
     */
    public String getOrigin() {
        return _origin;
    }

    /**
     * Provides the destination service span (from format origin->destination).
     * @return destination service span
     */
    public String getDestination() {
        return _destination;
    }

    /**
     * Provides the parsed service name.
     * @return service name
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * Sets the service name parsed from the log line
     * @param svcName service name
     */
    public void setServiceName(String svcName){
        this._serviceName = svcName;
    }

    /**
     * Provides the parsed start date of the logged service call.
     * @return service call start date
     */
    public Date getStartDate() {
        return _startDate;
    }

    /**
     * Sets the parsed start date of the logged service call.
     * @param _startDate service call start date
     */
    public void setStartDate(Date _startDate) {
        this._startDate = _startDate;
    }

    /**
     * Provides the parsed end date of the logged service call.
     * @return service call end date
     */
    public Date getEndDate() {
        return _endDate;
    }

    /**
     * Sets the parsed end dtae of the logged service call.
     * @param _endDate service call end date
     */
    public void setEndDate(Date _endDate) {
        this._endDate = _endDate;
    }

    /**
     * Sets the origin and destination spans from the provided array of strings.  The original format from the log line is:
     * "origin->destination".  After split, str[0] = origin, str[1] = destination.
     * @param str the split string array containing origin (str[0]) and destination (str[1])
     */
    public void setOriginAndDestinationFromSplitString(String[] str){
        if(str.length != 2){ return; }
        _origin = str[0];
        _destination = str[1];
    }

}
