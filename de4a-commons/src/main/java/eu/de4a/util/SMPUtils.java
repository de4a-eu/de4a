package eu.de4a.util;

import java.util.Arrays;
import java.util.List;

public class SMPUtils {

	private static final String DOUBLE_SEPARATOR = "::";
	private static final String SEPARATOR = ":";
	private static final String SERVICE = ":service::";
	private static final String SERVICES_PATH = "/services/";
	
	SMPUtils() {
		//empty constructor
	}

	/**
	 * 
	 * Retrieve SMP uri for requestor, from transferor service It will replace
	 * transferor by requestor id to obtain the counterpart service on SMP
	 * 
	 * @param smpEndpoint
	 * @param service
	 * @param requestor
	 * @return smpUri uri to retrieve processId returnService of requestor from SMP
	 */
	public static String getSmpUri(String smpEndpoint, String service, String requestor) {
		List<String> serviceParams = Arrays.asList(service.split(SERVICE));
		String scheme = serviceParams.get(0);
		List<String> docParams = Arrays.asList(serviceParams.get(1).split(SEPARATOR));
		StringBuilder docId = new StringBuilder("");
		for (int i = 2; i < docParams.size(); i++) {
			docId.append((i != 2 ? SEPARATOR : ""));
			docId.append(docParams.get(i));
		}
		StringBuilder uri = new StringBuilder(smpEndpoint);
		uri.append(scheme).append(DOUBLE_SEPARATOR).append(requestor).append(SERVICES_PATH).append(scheme)
				.append(DOUBLE_SEPARATOR).append(docId);
		return uri.toString();
	}

	/**
	 * Retrieve SMP uri for passed service
	 * 
	 * @param smpEndpoint
	 * @param service
	 * @return mpUri uri to retrieve processId service of transferor from SMP
	 */
	public static String getSmpUri(String smpEndpoint, String service) {
		List<String> serviceParams = Arrays.asList(service.split(SERVICE));
		String scheme = serviceParams.get(0);
		List<String> docParams = Arrays.asList(serviceParams.get(1).split(SEPARATOR));
		String participantId = docParams.get(0) + SEPARATOR + docParams.get(1);
		String docId = docParams.get(2) + (docParams.size() > 3 ? SEPARATOR + docParams.get(3) : "");

		StringBuilder uri = new StringBuilder(smpEndpoint);
		uri.append(scheme).append(DOUBLE_SEPARATOR).append(participantId).append(SERVICES_PATH).append(scheme)
				.append(DOUBLE_SEPARATOR).append(docId);
		return uri.toString();
	}

	/**
	 * 
	 * Retrieve SMP service for requestor from transferor service It will replace
	 * transferor by requestor participant id to obtain the counterpart service on SMP
	 * 
	 * @param service
	 * 		transferor service
	 * @param requestor
	 * 		requestor participantId
	 * @return smpService
	 * 		Service to retrieve processId returnService of requestor from SMP
	 */
	public static String getRequestorReturnService(String service, String requestor) {
		List<String> serviceParams = Arrays.asList(service.split(SERVICE));
		String scheme = serviceParams.get(0);
		List<String> docParams = Arrays.asList(serviceParams.get(1).split(SEPARATOR));
		String docId = docParams.get(2) + (docParams.size() > 3 ? SEPARATOR + docParams.get(3) : "");

		StringBuilder uri = new StringBuilder("");
		uri.append(scheme).append(SERVICE).append(requestor).append(SEPARATOR).append(docId);
		return uri.toString();
	}
}
