package eu.de4a.scsp.mock.dba;

import java.util.ArrayList;
import java.util.List; 

import org.springframework.stereotype.Component;

@Component
public class DbaRepository {
	private List<Entity> entities;
	 
	public DbaRepository() {
		entities=new ArrayList<Entity>();
		Entity a=makeEntity("NLNHR.90000471","7500", "NL", "1508 WN", "Zaandam", "nepemailadres@kvk.nl", "379", "Regional Tris-ice Coöperatie", "1980-09-18",
				"economically active", "+31 0209999999", "coöp", "Leverkruidweg");
		Entity b=makeEntity("NLOSS.70000777","23500", "NL", "1243 ZS", "Oss", "fakeenterprise@kvk.nl", "777", "Fake Coöperatie", "1961-11-12",
				"economically active", "+31 0209999944", "coöp", "Ravenstein");
		Entity c=makeEntity("NLXXX.10000111","200", "NL", "666 FR", "Delft", "delferprise@kvk.nl", "111", "Delft United Tecnologies", "2021-01-12",
				"economically active", "+31 0209999555", "society", "Van Der Bosch");
		entities.add(a);
		entities.add(b);
		entities.add(c);
	}
	public Entity selectEntity(String id) {
		return entities.stream().filter(e->e.getId().toLowerCase().equals(id.toLowerCase())).findFirst().orElse(null);
	}
	private Entity makeEntity(String id,String activity,String country,String cp,String cpname,String email,String locationDesignator,String name,String registrationDate,
			String status,String tlf,String type,String via) {
		Entity e=new Entity();
		e.setId(id);
		e.setActivity(activity);
		e.setCountry(country);
		e.setCp(cp);
		e.setCpname(cpname);
		e.setEmail(email);
		e.setLocationDesignator(locationDesignator);
		e.setName(name);
		e.setRegistrationDate(registrationDate);
		e.setStatus(status);
		e.setTlf(tlf);
		e.setType(type);
		e.setVia(via);
		return e;
	}
}
