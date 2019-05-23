package cjc.tracebuilder.execution;

import cjc.tracebuilder.input.UserParams;

/**
 *  An executor singleton that maintains current status of the various executors,
 *  as well as the initial user input parameters.
 */
public class ExecutionStatusManager {
    private static ExecutionStatusManager ourInstance = new ExecutionStatusManager();
    private boolean _inputFinished;
    private boolean _isProcessingFinished;
    private boolean _isOutputFinished;
    private boolean _isTraceHandlingFinished;
    private boolean _isStdInReadingFinished;
    private UserParams _userParams;

    /**
     * Gets our instance of the execution status manager.
     * @return our instance of the execution status manager.
     */
    public static ExecutionStatusManager getInstance() {
        return ourInstance;
    }

    private ExecutionStatusManager() { }

    /**
     * Flag to indicate if the input reader is finished working.
     * @return flag for if the input reader is finished
     */
    public synchronized boolean isInputFinished() {
            return _inputFinished;
    }

    /**
     * Flag to indicate if the input reader is finished working.
     * @param _inputFinished flag for if the input reader is finished
     */
    public synchronized void setInputFinished(boolean _inputFinished) {
            this._inputFinished = _inputFinished;
    }

    /**
     * Flag to indicate if the trace parser is finished working.
     * @return flag for if the trace parser is finished
     */
    public synchronized boolean isProcessingFinished() {
            return _isProcessingFinished;
    }

    /**
     * Flag to indicate if the trace parser is finished working.
     * @param _isProcessingFinished flag for if the trace parser is finished
     */
    public synchronized void setProcessingFinished(boolean _isProcessingFinished) {
            this._isProcessingFinished = _isProcessingFinished;
    }

    /**
     * Flag to indicate if the finished trace handler is finished working.
     * @return flag for if the finishe trace handler is finished
     */
    public synchronized boolean isTraceHandlingFinished() {
            return _isTraceHandlingFinished;

    }

    /**
     * Flag to indicate if the finished trace handler is finished working.
     * @param _isTraceHandlingFinished flag for if the trace handler is finished
     */
    public synchronized void setTraceHandlingFinished(boolean _isTraceHandlingFinished) {
            this._isTraceHandlingFinished = _isTraceHandlingFinished;
    }

    /**
     * Flag to indicate if the output writer is finished working.
     * @return flag for if the output writer is finished
     */
    public synchronized boolean isOutputFinished() {
            return _isOutputFinished;
    }

    /**
     * Flag to indicate if the output writer is finished working.
     * @param _isOutputFinished flag for if the output writer is finished
     */
    public synchronized void setOutputFinished(boolean _isOutputFinished) {
            this._isOutputFinished = _isOutputFinished;
    }

    /**
     * The collection of execution parameters provided by the user (or default values if not provided).
     * @return The collection of execution parameters provided by the user (or default values if not provided).
     */
    public UserParams getUserParams() {
        return _userParams;
    }

    /**
     * Sets the collection of execution parameters provided by the user (defaults any param values not provided).
     * @param _userParams The collection of execution parameters provided by the user (or default values if not provided).
     */
    public void setUserParams(UserParams _userParams) {
        this._userParams = _userParams;
    }

    /**
     * Flag to indicate that reading from STDIN is finished.
     * @return true if we are finished reading from STDIN
     */
    public boolean isStdInReadingFinished() {
        return _isStdInReadingFinished;
    }

    /**
     * Sets flag to indicate whether or not we are finished reading from STDIN.
     * @param _isStdInReadingFinished flag to indicate whether or not we are finished reading from STDIN.
     */
    public void setStdInReadingFinished(boolean _isStdInReadingFinished) {
        this._isStdInReadingFinished = _isStdInReadingFinished;
    }
}
