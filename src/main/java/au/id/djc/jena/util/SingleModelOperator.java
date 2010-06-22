package au.id.djc.jena.util;

import com.hp.hpl.jena.rdf.model.Model;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * {@link ModelOperations} implementation which always operates on a single
 * shared in-memory {@link Model} instance.
 */
public class SingleModelOperator implements ModelOperations, InitializingBean {
    
    private Model model;
    
    public SingleModelOperator() {
    }
    
    public SingleModelOperator(Model model) {
        this.model = model;
    }
    
    public void setModel(Model model) {
        this.model = model;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(model, "The 'model' property must not be null");
    }
    
    @Override
    public <T> T withModel(ModelExecutionCallback<T> callback) {
        return callback.execute(model);
    }

}
