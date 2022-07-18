package eu.de4a.connector.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.helger.commons.collection.ArrayHelper;
import eu.de4a.connector.StaticContextAccessor;

public final class MessageUtils {

    private MessageUtils (){}

    public static String format(final String key, final Object[] args) {
        final MessageSource messageSource = StaticContextAccessor.getBean(MessageSource.class);
        final Locale locale = LocaleContextHolder.getLocale();
        if(args != null && args.length > 0) {
            return messageSource.getMessage(key, args, locale);
        }
        return messageSource.getMessage(key, ArrayHelper.EMPTY_OBJECT_ARRAY, locale);
    }
    
    public static String format(final String key, String code, Object[] args) {
        ArrayList<Object> newObj = new ArrayList<Object>(Arrays.asList(args));
        newObj.add(0, code);
        final MessageSource messageSource = StaticContextAccessor.getBean(MessageSource.class);
        final Locale locale = LocaleContextHolder.getLocale();
        if(args != null && args.length > 0) {
            return messageSource.getMessage(key, newObj.toArray(), locale);
        }
        return messageSource.getMessage(key, ArrayHelper.EMPTY_OBJECT_ARRAY, locale);
    }
}
