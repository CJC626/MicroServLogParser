package cjc.tracebuilder.output;

import cjc.tracebuilder.execution.pending.PendingServiceCall;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceCall {

    private String _service;
    private Date _start;
    private Date _end;
    private List<ServiceCall> _calls;
    private String _span;

    public ServiceCall(PendingServiceCall call){
        _service = call.getServiceName();
        _start = call.getStartDate();
        _end = call.getEndDate();
        _span = call.getDestination();
        _calls = new ArrayList<ServiceCall>();
    }

    public String getService() {
        return _service;
    }

    public void setService(String _service) {
        this._service = _service;
    }

    public Date getStart() {
        return _start;
    }

    public void setStart(Date _start) {
        this._start = _start;
    }

    public Date getEnd() {
        return _end;
    }

    public void setEnd(Date _end) {
        this._end = _end;
    }

    public List<ServiceCall> getCalls() {
        return _calls;
    }

    public void setCalls(List<ServiceCall> calls) {
        this._calls = calls;
    }

    public boolean addToCalls(ServiceCall call){
        if(_calls==null){
            _calls = new ArrayList<ServiceCall>();
        }
        return this._calls.add(call);
    }

    public String getSpan(){
        return _span;
    }

    public void setSpan(String _span){
        this._span = _span;
    }


}
