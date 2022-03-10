package eu.de4a.connector.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import eu.de4a.connector.StaticContextAccessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtils {
    
    public static String valueOf(final String key, final Object[] args) {
        MessageSource messageSource = StaticContextAccessor.getBean(MessageSource.class);
        Locale locale = LocaleContextHolder.getLocale();
        if(args != null) {
            return messageSource.getMessage(key, args, locale);
        }
        return messageSource.getMessage(key, new Object[0], locale);
    }
}
