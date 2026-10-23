package sgcf_back.camilaronzzani.com.github.sgcf_back.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;

public class DatabaseLogAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        if (!event.getLevel().isGreaterOrEqual(Level.WARN)) {
            return;
        }

        IThrowableProxy throwable = event.getThrowableProxy();
        String exceptionType = throwable == null ? null : throwable.getClassName();
        String stackTrace = throwable == null ? null : ThrowableProxyUtil.asString(throwable);

        sgcf_back.camilaronzzani.com.github.sgcf_back.config.SystemLogStore.record(
                event.getLevel().toString(),
                event.getLoggerName(),
                event.getFormattedMessage(),
                exceptionType,
                stackTrace
        );
    }
}
