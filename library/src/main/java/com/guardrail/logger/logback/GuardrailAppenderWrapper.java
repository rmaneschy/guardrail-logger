package com.guardrail.logger.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.guardrail.logger.engine.SanitizationEngine;
import org.slf4j.Marker;
import org.slf4j.event.KeyValuePair;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Appender wrapper que intercepta eventos de log e aplica sanitização.
 * 
 * <p>Este appender pode envolver qualquer outro appender existente,
 * aplicando mascaramento de dados sensíveis antes de delegar ao
 * appender original.</p>
 * 
 * <p>É especialmente útil para integração com appenders GELF/Graylog
 * sem necessidade de modificar a configuração existente.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class GuardrailAppenderWrapper extends AppenderBase<ILoggingEvent> 
        implements AppenderAttachable<ILoggingEvent> {

    private final AppenderAttachableImpl<ILoggingEvent> appenderAttachable = 
        new AppenderAttachableImpl<>();
    private boolean enabled = true;

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!enabled) {
            appenderAttachable.appendLoopOnAppenders(eventObject);
            return;
        }

        SanitizationEngine engine = SanitizationEngine.getInstance();
        if (!engine.isInitialized()) {
            appenderAttachable.appendLoopOnAppenders(eventObject);
            return;
        }

        // Cria um evento modificado com a mensagem sanitizada
        ILoggingEvent sanitizedEvent = createSanitizedEvent(eventObject, engine);
        appenderAttachable.appendLoopOnAppenders(sanitizedEvent);
    }

    /**
     * Cria um evento de log com a mensagem sanitizada.
     */
    private ILoggingEvent createSanitizedEvent(ILoggingEvent original, SanitizationEngine engine) {
        String originalMessage = original.getFormattedMessage();
        String sanitizedMessage = engine.sanitize(originalMessage);

        // Se a mensagem não mudou, retorna o evento original
        if (originalMessage.equals(sanitizedMessage)) {
            return original;
        }

        // Cria um novo evento com a mensagem sanitizada
        return new SanitizedLoggingEvent(original, sanitizedMessage);
    }

    @Override
    public void addAppender(Appender<ILoggingEvent> newAppender) {
        appenderAttachable.addAppender(newAppender);
    }

    @Override
    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        return appenderAttachable.iteratorForAppenders();
    }

    @Override
    public Appender<ILoggingEvent> getAppender(String name) {
        return appenderAttachable.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<ILoggingEvent> appender) {
        return appenderAttachable.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        appenderAttachable.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<ILoggingEvent> appender) {
        return appenderAttachable.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        return appenderAttachable.detachAppender(name);
    }

    /**
     * Habilita ou desabilita o mascaramento.
     *
     * @param enabled true para habilitar
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Verifica se o mascaramento está habilitado.
     *
     * @return true se habilitado
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Evento de log wrapper com mensagem sanitizada.
     */
    private static class SanitizedLoggingEvent implements ILoggingEvent {
        private final ILoggingEvent delegate;
        private final String sanitizedMessage;

        SanitizedLoggingEvent(ILoggingEvent delegate, String sanitizedMessage) {
            this.delegate = delegate;
            this.sanitizedMessage = sanitizedMessage;
        }

        @Override
        public String getFormattedMessage() {
            return sanitizedMessage;
        }

        @Override
        public String getMessage() {
            return sanitizedMessage;
        }

        @Override
        public Object[] getArgumentArray() {
            return new Object[0]; // Argumentos já foram aplicados na mensagem sanitizada
        }

        @Override
        public String getThreadName() { 
            return delegate.getThreadName(); 
        }
        
        @Override
        public Level getLevel() { 
            return delegate.getLevel(); 
        }
        
        @Override
        public String getLoggerName() { 
            return delegate.getLoggerName(); 
        }
        
        @Override
        public LoggerContextVO getLoggerContextVO() { 
            return delegate.getLoggerContextVO(); 
        }
        
        @Override
        public IThrowableProxy getThrowableProxy() { 
            return delegate.getThrowableProxy(); 
        }
        
        @Override
        public StackTraceElement[] getCallerData() { 
            return delegate.getCallerData(); 
        }
        
        @Override
        public boolean hasCallerData() { 
            return delegate.hasCallerData(); 
        }
        
        @Override
        public Marker getMarker() {
            final var markers = delegate.getMarkerList();
            return CollectionUtils.firstElement(markers);
        }
        
        @Override
        public List<Marker> getMarkerList() { 
            return delegate.getMarkerList(); 
        }
        
        @Override
        public Map<String, String> getMDCPropertyMap() { 
            return delegate.getMDCPropertyMap(); 
        }
        
        @Override
        public Map<String, String> getMdc() { 
            return delegate.getMDCPropertyMap();
        }
        
        @Override
        public long getTimeStamp() { 
            return delegate.getTimeStamp(); 
        }
        
        @Override
        public int getNanoseconds() { 
            return delegate.getNanoseconds(); 
        }
        
        @Override
        public long getSequenceNumber() { 
            return delegate.getSequenceNumber(); 
        }
        
        @Override
        public List<KeyValuePair> getKeyValuePairs() { 
            return delegate.getKeyValuePairs(); 
        }
        
        @Override
        public void prepareForDeferredProcessing() { 
            delegate.prepareForDeferredProcessing(); 
        }
    }
}
