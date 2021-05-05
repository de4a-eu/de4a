package eu.de4a.connector.service.spring;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import eu.de4a.connector.api.manager.ApplicationContextProvider;

public class MessageUtils {

    private String key;
    private Object[] args;

    private MessageSource messageSource;
    {
        messageSource = ApplicationContextProvider.getApplicationContext().getBean(MessageSource.class);
    }

    public MessageUtils(String key) {
        super();
        this.key = key;
    }
    
    public MessageUtils(String key, Object[] args) {
        super();
        this.key = key;
        this.args = args;
    }

    public String value() {
        Locale locale = LocaleContextHolder.getLocale();
        if(args != null) {
            return messageSource.getMessage(key, this.args, locale);
        }
        return messageSource.getMessage(key, new Object[0], locale);
    }

    @Override
    public String toString() {
        return value();
    }
}
