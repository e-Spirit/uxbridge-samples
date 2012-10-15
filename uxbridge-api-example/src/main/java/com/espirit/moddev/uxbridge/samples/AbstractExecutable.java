package com.espirit.moddev.uxbridge.samples;

import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.access.script.ExecutionException;
import de.espirit.firstspirit.access.store.templatestore.WorkflowScriptContext;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;


public abstract class AbstractExecutable implements Executable {
    private static final Class<?> LOGGER = AbstractExecutable.class;
    protected WorkflowScriptContext context;
    protected Writer out;
    protected Writer err;
    protected Map<String, Object> params;


    public Object getContext() {
        return context;
    }


    public Writer getOut() {
        return out;
    }


    public Writer getErrorOut() {
        return err;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public Object execute(final Map<String, Object> params) throws ExecutionException {
        context = (WorkflowScriptContext) params.get("context");
        return execute(params, new PrintWriter(System.out, true), new PrintWriter(System.err, true));
    }

//    public abstract Object execute(final Map<String, Object> params, final Writer out, final Writer err) throws ExecutionException;

}

