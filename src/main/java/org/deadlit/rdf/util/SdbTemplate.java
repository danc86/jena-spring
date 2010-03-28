package org.deadlit.rdf.util;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.graph.PrefixMappingSDB;
import com.hp.hpl.jena.sdb.sql.SDBConnection;

@ManagedResource
public class SdbTemplate {
    
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
    
    private final DataSource dataSource;
    private final StoreDesc storeDesc;
    
    public SdbTemplate(DataSource dataSource, StoreDesc storeDesc) {
        this.dataSource = dataSource;
        this.storeDesc = storeDesc;
    }
    
    private Store create() {
        try {
            return SDBFactory.connectStore(new SDBConnection(dataSource), storeDesc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public <T> T withModel(ModelExecutionCallback<T> callback) {
        Store store = create();
        store.getLoader().setUseThreading(false);
        try {
            Model model = SDBFactory.connectDefaultModel(store);
            try {
                return callback.execute(model);
            } finally {
                model.close();
            }
        } finally {
            store.close();
        }
    }
    
    @ManagedOperation(description = "Creates the tables and indices necessary for SDB")
    public void format() {
        Store store = create();
        store.getLoader().setUseThreading(false);
        try {
            store.getTableFormatter().create();
        } finally {
            store.close();
        }
    }
    
    @ManagedOperation
    public Map<String, String> getPrefixMappings() throws SQLException {
        PrefixMappingSDB mapping = new PrefixMappingSDB(null, new SDBConnection(dataSource));
        return mapping.getNsPrefixMap();
    }
    
    @ManagedOperation
    public void addPrefixMapping(String prefix, String uri) throws SQLException {
        PrefixMappingSDB mapping = new PrefixMappingSDB(null, new SDBConnection(dataSource));
        mapping.setNsPrefix(prefix, uri);
    }

}