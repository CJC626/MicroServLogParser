package cjc.tracebuilder.execution.pending;

import cjc.tracebuilder.execution.OutputTraceWriter;
import cjc.tracebuilder.output.OutputTrace;
import cjc.tracebuilder.output.ServiceCall;

import java.util.*;

public class PendingOutputTrace {

    private OutputTrace _outputTrace;
    private Map<String, List<PendingServiceCall>> _collectedServiceCalls;
    private boolean _finished;
    private Date _creationTime;
    private Date _dateLastNodeAdded;
    private boolean _rootNodeFound;

    public PendingOutputTrace(){
        _collectedServiceCalls = new HashMap<String, List<PendingServiceCall>>();
    }

    public OutputTrace getOutputTrace() {
        return _outputTrace;
    }

    public void setOutputTrace(OutputTrace _outputTrace) {
        this._outputTrace = _outputTrace;
    }

    public Map<String, List<PendingServiceCall>> getCollectedServiceCalls() {
        return _collectedServiceCalls;
    }

    public void setCollectedServiceCalls(Map<String, List<PendingServiceCall>> _collectedServiceCalls) {
        this._collectedServiceCalls = _collectedServiceCalls;
    }

    public boolean addToCollectedServiceCalls(PendingServiceCall serviceCall){
        boolean addition = false;
        List<PendingServiceCall> serviceCallsOnCurrentDestination;
        synchronized (this) {
            if (_collectedServiceCalls.containsKey(serviceCall.getOrigin())) {
                serviceCallsOnCurrentDestination = _collectedServiceCalls.get(serviceCall.getOrigin());
            } else {
                serviceCallsOnCurrentDestination = new ArrayList<PendingServiceCall>();
                _collectedServiceCalls.put(serviceCall.getOrigin(), serviceCallsOnCurrentDestination);
            }
        }
        addition = serviceCallsOnCurrentDestination.add(serviceCall);
        //LOOK HERE FOR SYNC ISSUES
        return addition;
    }

    public boolean isFinished(){
        return _finished;
    }

    public void setFinished(boolean _finished){
        this._finished = _finished;
    }

    public boolean isRootNodeFound(){
        return _rootNodeFound;
    }

    public void setRootNodeFound(boolean _rootNodeFound){
        this._rootNodeFound = _rootNodeFound;
    }

    public Date getCreationTime(){
        return _creationTime;
    }

    public void setCreationTime(Date date){
        _creationTime = date;
    }

    public Date getDateLastNodeAdded(){
        return _dateLastNodeAdded;
    }

    public void setDateLastNodeAdded(Date date){
        _dateLastNodeAdded = date;
    }


    //TODO Make this more readable.  Maybe do some GC?
    public OutputTrace buildFinishedOutputTrace(String origin, ServiceCall node){
        if(!_collectedServiceCalls.containsKey(origin)){
            _finished = true;
            return _outputTrace;
        }
        List<PendingServiceCall> currentPendingCalls = _collectedServiceCalls.get(origin);
        if(origin.equals("null")){
            PendingServiceCall currentPendingCall;
            //TODO - CHECK FOR SYNC ISSUES
            synchronized (this) {
                currentPendingCall = currentPendingCalls.get(0);
            }
            ServiceCall newCall = new ServiceCall(currentPendingCall);
            _outputTrace.setRoot(newCall);
            return buildFinishedOutputTrace(currentPendingCall.getDestination(), newCall);
        }
        for(PendingServiceCall pendingCall: currentPendingCalls){
            ServiceCall newCall = new ServiceCall(pendingCall);
            node.addToCalls(newCall);
           _outputTrace = buildFinishedOutputTrace(pendingCall.getDestination(), newCall);
        }
        return _outputTrace;
    }

}
