@Library ('JenkinsLibsGIT') _
  
        env.MAVENNAME = 'MAVEN'
        env.MAVENREPO = '/data/jenkins/.m2/repository/'
        env.JDK11 = 'jdk11'
        env.dirCodigo = ''
	node 
	{
		def checklistDefinition = ["PACKAGE", "TESTS", "TASKS",  "PUBLISH", "EMAILQA", "EMAILDEVELOPERS", "OTHERS"]
		def checksActivos = checklistGIT.crearChecklistDE4A(checklistDefinition)
		println (checksActivos)
		def docker=0
		try{
			for (item in checksActivos)
			{
				stage (item)
				{
					switch (item)
					{
						case checklistDefinition[0]: package.compilarPortfolio(mavenName, mavenRepo, JDK11, dirCodigo)
						break;
						case checklistDefinition[1]: tests.runTestsPortfolio(mavenName, mavenRepo, JDK11, JDK11, dirCodigo)
						break;
						case checklistDefinition[2]: tasks.publishTasksPorfolio(dirCodigo)
						break;
						case checklistDefinition[3]: publish.publishTestsPortfolio(dirCodigo)
						break;
						case checklistDefinition[4]: sendEmail.emailQA()
						break;
						case checklistDefinition[5]: sendEmail.emailDevelopersPortfolio()
						break;
						case checklistDefinition[6]: sendEmail.emailOthers(item)
						break;
					}
				}
			}
	
		}
		catch(err)
		{
			
		}
	
	}
	