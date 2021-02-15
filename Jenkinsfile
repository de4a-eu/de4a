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
						case checklistDefinition[0]: package.compilarDE4A(mavenName, mavenRepo, JDK11, dirCodigo)
						break;
						case checklistDefinition[1]: tests.runTestsDE4A(mavenName, mavenRepo, JDK11, JDK11, dirCodigo)
						break;
						case checklistDefinition[2]: tasks.publishTasksDE4A(dirCodigo)
						break;
						case checklistDefinition[3]: publish.publishTestsDE4A(dirCodigo)
						break;
						case checklistDefinition[4]: sendEmail.emailQADE4A()
						break;
						case checklistDefinition[5]: sendEmail.emailDevelopersDE4A()
						break;
						case checklistDefinition[6]: sendEmail.emailOthersDE4A(item)
						break;
					}
				}
			}
	
		}
		catch(err)
		{
			
		}
	
	}
	