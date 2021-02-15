@Library ('JenkinsLibsGIT') _
  
        env.MAVENNAME = 'MAVEN'
        env.MAVENREPO = '/data/jenkins/.m2/repository/'
        env.JDK11 = 'jdk11'
        env.dirCodigo = ''
	node 
	{
		def checklistDefinition = ["PACKAGE", "TESTS", "TASKS",  "PUBLISH", "EMAILQA", "EMAILDEVELOPERS", "OTHERS"]
		def checksActivos = checklistGIT.crearChecklistDE4A(checklistDefinition, checklistParamTextDefinition,checklistParamTextDocker)
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
						case checklistDefinition[3]: tests.runTestsPortfolio(mavenName, mavenRepo, JDK11, JDK11, dirCodigo)
						break;
						case checklistDefinition[9]: tasks.publishTasksPorfolio(dirCodigo)
						break;
						case checklistDefinition[10]: publish.publishTestsPortfolio(dirCodigo)
						break;
						case checklistDefinition[13]: sendEmail.emailQA()
						break;
						case checklistDefinition[14]: sendEmail.emailDevelopersPortfolio()
						break;
						case checklistDefinition[15]: sendEmail.emailOthers(item)
						break;
					}
				}
			}
	
		}
		catch(err)
		{
			
		}
	
	}
	