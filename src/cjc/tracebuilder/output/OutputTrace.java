package cjc.tracebuilder.output;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutputTrace {

    private String _id;
    private ServiceCall _root;

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public ServiceCall getRoot() {
        return _root;
    }

    public void setRoot(ServiceCall _root) {
        this._root = _root;
    }


}
