package org.deadlit.rdf.util;

import com.hp.hpl.jena.rdf.model.Model;

public interface ModelOperations {
    
    public static interface ModelExecutionCallback<T> {
        T execute(Model model);
    }
    public static abstract class ModelExecutionCallbackWithoutResult implements ModelExecutionCallback<Object> {
        @Override
        final public Object execute(Model model) {
            executeWithoutResult(model);
            return null;
        }
        protected abstract void executeWithoutResult(Model model);
    }

    <T> T withModel(ModelExecutionCallback<T> callback);

}