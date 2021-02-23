package eu.de4a.scsp.preview.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.toop.req.model.EvaluatorRequestData;
import eu.toop.req.repository.RequestorRequestRepository;

@Component
public class PreviewManager {

	@Autowired
	private RequestorRequestRepository requestorRequestRepository;
	
	public ResponseTransferEvidence gimmePreview(String id) {
		RequestorRequest request=new RequestorRequest();
		request.setSenderId(id);
		 Example<RequestorRequest> example = Example.of(request);
		 RequestorRequest requestorRequest=requestorRequestRepository.findAll(example).findFirst().orElse(null);
		 //wait a ratito hasta timeout para establecer que tenemos guardada una request de evaluator y que tiene datos a previsualizar
		 //TODO esto cambiará para cuando tengamos parametros de entrada en el preview.
		 if(requestorRequest.isDone()) {
			 //Mediante el requestor obtendríamos los datos de la respuesta. A priori N evidenceTransferDAta asociado a 1 requestorRequest. En base a esas N evidencias 
			 //pintamos los datos de la preview.
			 return response;
		 }
		 return null;
	}
}
