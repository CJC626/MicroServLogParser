package cjc.tracebuilder.execution;

import cjc.tracebuilder.input.UserParams;

public class ExecutionStatusManager {
    private static ExecutionStatusManager ourInstance = new ExecutionStatusManager();
    private boolean _inputFinished;
    private boolean _isProcessingFinished;
    private boolean _isOutputFinished;
    private boolean _isTraceHandlingFinished;
    private UserParams _userParams;

    public static ExecutionStatusManager getInstance() {
        return ourInstance;
    }

    private ExecutionStatusManager() {

    }

    public boolean isInputFinished() {
        synchronized (this) {
            return _inputFinished;
        }
    }

    public void setInputFinished(boolean _inputFinished) {
        synchronized (this) {
            this._inputFinished = _inputFinished;
        }
    }

    public boolean isProcessingFinished() {
        synchronized (this) {
            return _isProcessingFinished;
        }
    }

    public void setProcessingFinished(boolean _isProcessingFinished) {
        synchronized (this) {
            this._isProcessingFinished = _isProcessingFinished;
        }
    }

    public boolean isTraceHandlingFinished() {
        synchronized (this) {
            return _isTraceHandlingFinished;
        }
    }

    public void setTraceHandlingFinished(boolean _isTraceHandlingFinished) {
        synchronized (this) {
            this._isTraceHandlingFinished = _isTraceHandlingFinished;
        }
    }

    public boolean isOutputFinished() {
        synchronized (this) {
            return _isOutputFinished;
        }
    }

    public void setOutputFinished(boolean _isOutputFinished) {
        synchronized (this) {
            this._isOutputFinished = _isOutputFinished;
        }
    }

    public UserParams getUserParams() {
        return _userParams;
    }

    public void setUserParams(UserParams _userParams) {
        this._userParams = _userParams;
    }
}
