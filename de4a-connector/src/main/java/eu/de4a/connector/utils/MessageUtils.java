package eu.de4a.connector.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.helger.commons.collection.ArrayHelper;

import eu.de4a.connector.StaticContextAccessor;
import eu.de4a.iem.core.jaxb.common.EventNotificationItemType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractEvidenceItemType;

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
    
    public static <T> String getRequestMetadata(List<T> items) {

	    List<String> requestItems = new ArrayList<String>();
	    items.forEach(item -> {
	    	RequestEvidenceItemType request = (RequestEvidenceItemType) item;
	    	requestItems.add(request.getRequestItemId() + ":" + request.getCanonicalEvidenceTypeId());
	    });
	    
	    return format(requestItems);
    }
    
    public static String getEvidenceResponseMetadata(List<ResponseExtractEvidenceItemType> items) {

	    List<String> requestItems = new ArrayList<String>();
	    items.forEach(item -> {
	    	if(!item.getError().isEmpty()) requestItems.add(item.getRequestItemId() + ":" + item.getErrorAtIndex(0).getCode());
	    	else requestItems.add(item.getRequestItemId() + ":" + item.getCanonicalEvidenceTypeId());
	    });
	    
	    return format(requestItems);
    }
    
    public static String getRedirectResponseMetadata(RedirectUserType redirectResponse) {
    	return redirectResponse.getError().isEmpty() ?
    			redirectResponse.getRequestId() + ":" + redirectResponse.getCanonicalEvidenceTypeId() :
    				redirectResponse.getRequestId() + ":" + redirectResponse.getErrorAtIndex(0).getCode(); 
    }
    
    public static String getEventNotificationMetadata(List<EventNotificationItemType> items) {
    	List<String> requestItems = new ArrayList<String>();
	    items.forEach(item -> {
	    	requestItems.add(item.getNotificationItemId() + ":" + item.getCanonicalEventCatalogUri());
	    });
	    
	    return format(requestItems);
	    
    }
    
    //TODO The ResponseEventSubscriptionItemType does not have ErrorType
    /*
    public static String getEventSubscriptionResponseMetadata(List<ResponseEventSubscriptionItemType> items) {

	    List<String> requestItems = new ArrayList<String>();
	    items.forEach(item -> {
	    	if(!item.getError().isEmpty()) requestItems.add(item.getRequestItemId() + ":" + item..getCode());
	    	else requestItems.add(item.getRequestItemId() + ":" + item.getCanonicalEvidenceTypeId());
	    });
	    
	    return format(requestItems);
    }*/
    
    private static String format(List<String> items) {
    	return "(" + String.join(", ",  items) + ")";
    }
}
