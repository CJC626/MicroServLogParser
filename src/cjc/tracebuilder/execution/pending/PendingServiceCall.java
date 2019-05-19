package cjc.tracebuilder.execution.pending;

import cjc.tracebuilder.output.ServiceCall;

import java.util.Date;

public class PendingServiceCall {

    private String _origin;
    private String _destination;
    private String _serviceName;
    private Date _startDate;
    private Date _endDate;
    private boolean _finished;

    public String getOrigin() {
        return _origin;
    }

    public void setOrigin(String _origin) {
        this._origin = _origin;
    }

    public String getDestination() {
        return _destination;
    }

    public void setDestination(String _destination) {
        this._destination = _destination;
    }

    public String getServiceName() {
        return _serviceName;
    }

    public void setServiceName(String svcName){
        this._serviceName = svcName;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public void setStartDate(Date _startDate) {
        this._startDate = _startDate;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public void setEndDate(Date _endDate) {
        this._endDate = _endDate;
    }

    public void setOriginAndDestinationFromSplitString(String[] str){
        //TODO validate width
        _origin = str[0];
        _destination = str[1];
    }


    public boolean getFinished(){
        return _finished;
    }

    public void setFinished(boolean _finished){
        this._finished = _finished;
    }


}
