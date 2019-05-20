package cjc.tracebuilder.output;

import cjc.tracebuilder.execution.pending.PendingServiceCall;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  A service call (to be formatted into a JSON string), from one point/span (origin) to another (destination).
 *  Contains the following attributes:
 *
 *  - Service - the name of the service called
 *  - Start - the timestamp of when the service call started
 *  - End - the timestamp of when the service call ended
 *  - Span - the "span" identifier (origin) of the service "caller"
 *  - Calls - a List of child service calls made from within this service call
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceCall {

    private String _service;
    private Date _start;
    private Date _end;
    private List<ServiceCall> _calls;
    private String _span;

    /**
     * Constructor that takes a pending/collected service call and maps it to the final ServiceCall object to be
     * written to a JSON string.
     * @param call the pending service call from the pending output trace queue to be mapped to the final ServiceCall
     */
    public ServiceCall(PendingServiceCall call){
        _service = call.getServiceName();
        _start = call.getStartDate();
        _end = call.getEndDate();
        _span = call.getDestination();
        _calls = new ArrayList<ServiceCall>();
    }

    /**
     * Obtains the Service name.
     * @return the Service name.
     */
    public String getService() {
        return _service;
    }

    /**
     * Sets the Service name.
     * @param _service the Service name
     */
    public void setService(String _service) {
        this._service = _service;
    }

    /**
     * Obtains the Start timestamp of the service call.
     * @return the Start timestamp of the service call
     */
    public Date getStart() {
        return _start;
    }

    /**
     * Sets the Start timestamp of the service call.
     * @param _start the Start timestamp of the service call
     */
    public void setStart(Date _start) {
        this._start = _start;
    }

    /**
     * Obtains the End timestamp of the service call.
     * @return the End timestamp of the service call
     */
    public Date getEnd() {
        return _end;
    }

    /**
     * Sets the End timestamp of the service call.
     * @param _end the Start timestamp of the service call
     */
    public void setEnd(Date _end) {
        this._end = _end;
    }

    /**
     * Obtains the list of child ServiceCalls on this parent.
     * @return the list of child ServiceCalls on this parent.
     */
    public List<ServiceCall> getCalls() {
        return _calls;
    }

    /**
     * Sets the list of child ServiceCalls on this parent.
     * @param calls the list of child ServiceCalls on this parent.
     */
    public void setCalls(List<ServiceCall> calls) {
        this._calls = calls;
    }

    /**
     * Adds a child ServiceCall to the list.
     * @param call the ServiceCall to add to the parent's list.
     * @return true if the ServiceCall was added successfully.
     */
    public boolean addToCalls(ServiceCall call){
        if(_calls==null){
            _calls = new ArrayList<ServiceCall>();
        }
        return this._calls.add(call);
    }

    /**
     * Obtains the Span identifier (origin) of this ServiceCall
     * @return the Span identifier (origin) of this ServiceCall
     */
    public String getSpan(){
        return _span;
    }

    /**
     * Sets the Span identifier (origin) of this ServiceCall
     * @param _span the Span identifier (origin) of this ServiceCall
     */
    public void setSpan(String _span){
        this._span = _span;
    }


}
