package idk;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.idk.model.Provision;

public class TestIDKModel {

	@Test
	public void testProvision() {
		//Instance
		Provision provision = new Provision();
		provision.setId(Long.MIN_VALUE);
		provision.setProvisionType("provisionType");

		//Clone
		Provision provisionCopy = new Provision(provision);
		assertEquals(provision.getId(), provisionCopy.getId());
	}

}
