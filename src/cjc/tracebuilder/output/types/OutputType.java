package cjc.tracebuilder.output.types;

/**
 *  Types of output Types for this application
 *   - TEXTFILE: user specifies a text file to write parsed JSON traces to.
 *   - STDOUT: parsed JSON traces are written to STDOUT.
 */
public enum OutputType {
    STDOUT, TEXTFILE
}
