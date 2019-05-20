package cjc.tracebuilder.execution.pending;

import cjc.tracebuilder.output.OutputTrace;
import cjc.tracebuilder.output.ServiceCall;

import java.util.*;

/**
 *  A "Pending" output trace, temporarily stored to maintain service calls collected
 *  on this trace ID, to eventually parse into the trace JSON once (we think) all service
 *  calls have been collected.
 */
public class PendingOutputTrace {

    private OutputTrace _outputTrace;
    private Map<String, List<PendingServiceCall>> _collectedServiceCalls;
    private Date _creationTime;
    private boolean _rootNodeFound;

    /**
     * Default constructor.  Initializes the collected service calls array.
     */
    public PendingOutputTrace(){
        _collectedServiceCalls = new HashMap<String, List<PendingServiceCall>>();
    }

    /**
     * Getter for the output trace that will be converted into JSON when ready.
     * @return OutputTrace object tree.
     */
    public OutputTrace getOutputTrace() {
        return _outputTrace;
    }

    /**
     * Setter for the output trace that will be converted into JSON when ready.
     * @param _outputTrace OutputTrace object tree to be initialized on this pending trace.
     */
    public void setOutputTrace(OutputTrace _outputTrace) {
        this._outputTrace = _outputTrace;
    }

    /**
     * Adds a parsed service call collected from a log line to this pending trace.
     * @param serviceCall pending service call to add to the list of collected service
     * @return whether or not the service call was successfully added
     */
    public boolean addToCollectedServiceCalls(PendingServiceCall serviceCall){
        boolean addition = false;
        List<PendingServiceCall> serviceCallsOnCurrentDestination;
        //TODO possibly synchronize on _collectedServiceCalls
        synchronized (this) {
            if (_collectedServiceCalls.containsKey(serviceCall.getOrigin())) {
                serviceCallsOnCurrentDestination = _collectedServiceCalls.get(serviceCall.getOrigin());
            } else {
                serviceCallsOnCurrentDestination = new ArrayList<PendingServiceCall>();
                _collectedServiceCalls.put(serviceCall.getOrigin(), serviceCallsOnCurrentDestination);
            }
        }
        addition = serviceCallsOnCurrentDestination.add(serviceCall);
        return addition;
    }

    /**
     * Flag that indicates if the root node "origin=null" service call for this trace has already been found.
     * @return flag that root node has been found.
     */
    public boolean isRootNodeFound(){
        return _rootNodeFound;
    }

    /**
     * Sets flag indicates if the root node "origin=null" service call for this trace has been found.
     * @param _rootNodeFound flag that root node has been found.
     */
    public void setRootNodeFound(boolean _rootNodeFound){
        this._rootNodeFound = _rootNodeFound;
    }

    /**
     * Time that this pending output trace was created.  Used to determine readyness of building final OutputTrace.
     * @return creation time of this pending output trace.
     */
    public Date getCreationTime(){
        return _creationTime;
    }

    /**
     * Sets the creation time of this output trace.
     * @param date creation time of this pending output trace.
     */
    public void setCreationTime(Date date){
        _creationTime = date;
    }

    /**
     * Uses the data from the collected service calls to build the final OutputTrace tree for this trace node.
     * This method is executed when a time threshold (based on creation time) has passed, and the system assumes
     * that this trace has no more service calls to wait for.  Uses recursion to build each child node, while
     * returning the OutputTrace in its current state if the origin span key is not in the list of collected service calls
     * (meaning it is an end destination).
     *
     * @param origin the origin span of this service call
     * @param node the ServiceCall node to add our child service calls to.
     * @return The OutputTrace tree in its current state.
     */
    public OutputTrace buildFinishedOutputTrace(String origin, ServiceCall node){
        if(!_collectedServiceCalls.containsKey(origin)){
            return _outputTrace;
        }
        List<PendingServiceCall> currentPendingCalls = _collectedServiceCalls.get(origin);
        //We will build the root node first.
        if("null".equals(origin)){
            PendingServiceCall currentPendingCall;
            //TODO - CHECK FOR POSSIBLE SYNC ISSUES
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
