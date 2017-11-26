/**
 *
 */
package com.gojek.guice.jpa;

import java.util.Optional;
import java.util.function.Function;

import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.guice.util.GuiceUtil;
import com.google.inject.persist.UnitOfWork;

/**
 * @author ganeshs
 *
 */
public class ActiveJpaGuiceExecutionWrapper {
    
    private static final Logger logger = LoggerFactory.getLogger(ActiveJpaGuiceExecutionWrapper.class);
    
    /**
     * Executes within a activeJpa-Guice wrapper
     *
     * @param function
     * @return
     */
    public <T> T execute(Function<Optional<Void>, T> function) {
        UnitOfWork unitOfWork = getUnitOfWork();
        JPAContext context = getContext();
        context.getEntityManager();
        try {
            return function.apply(Optional.empty());
        } catch(RuntimeException e) {
            logger.error("Failed while running the job", e);
            throw e;
        } finally {
            try {
                unitOfWork.end();
            } finally {
                if (context.isTxnOpen()) {
                    context.closeTxn(true);
                }
                context.close();
            }
        }
    }
    
    /**
     * @return
     */
    protected JPAContext getContext() {
        return JPA.instance.getDefaultConfig().getContext();
    }
    
    /**
     * @return
     */
    protected UnitOfWork getUnitOfWork() {
        return GuiceUtil.getInstance(UnitOfWork.class);
    }
}
