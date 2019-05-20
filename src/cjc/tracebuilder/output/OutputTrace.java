package cjc.tracebuilder.output;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 *  An output trace tree, to be written as a JSON string.  Contains the following attributes:
 *
 *   - Id - the identifier for a collection of end-to-end service calls.  Also known as a "trace".
 *   - Root - the initial (root) service call.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutputTrace {

    private String _id;
    private ServiceCall _root;

    /**
     * Obtains the trace ID.
     * @return the trace ID.
     */
    public String getId() {
        return _id;
    }

    /**
     * Sets the trace ID.
     * @param _id the trace ID.
     */
    public void setId(String _id) {
        this._id = _id;
    }

    /**
     * Obtains the root service call (span/destination == "null").
     * @return the root service call
     */
    public ServiceCall getRoot() {
        return _root;
    }

    /**
     * Sets the root service call (span/destination == "null").
     * @param _root the root service call
     */
    public void setRoot(ServiceCall _root) {
        this._root = _root;
    }


}
