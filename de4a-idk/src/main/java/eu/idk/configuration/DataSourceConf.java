package eu.idk.configuration;


public class DataSourceConf {
	private String url;
	private String driverClassName;

	private String username;
	private String password;
	private String initializationMode;

	private JpaHibernate jpaHibernate = new JpaHibernate();

	@SuppressWarnings("unused")
	public static class JpaHibernate {
		private String dialectPlatform;
		private String ddlAuto;
		private String generateDdl;
		private String namingStrategy;
		private String showSql;
		private String formatSql;

		public String getDialectPlatform() {
			return dialectPlatform;
		}
		public void setDialectPlatform(String dialectPlatform) {
			this.dialectPlatform = dialectPlatform;
		}
		public String getDdlAuto() {
			return ddlAuto;
		}
		public void setDdlAuto(String ddlAuto) {
			this.ddlAuto = ddlAuto;
		}
		public String getGenerateDdl() {
			return generateDdl;
		}
		public void setGenerateDdl(String generateDdl) {
			this.generateDdl = generateDdl;
		}
		public String getNamingStrategy() {
			return namingStrategy;
		}
		public void setNamingStrategy(String namingStrategy) {
			this.namingStrategy = namingStrategy;
		}
		public String getShowSql() {
			return showSql;
		}
		public void setShowSql(String showSql) {
			this.showSql = showSql;
		}
		public String getFormatSql() {
			return formatSql;
		}
		public void setFormatSql(String formatSql) {
			this.formatSql = formatSql;
		}

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInitializationMode() {
		return initializationMode;
	}

	public void setInitializationMode(String initializationMode) {
		this.initializationMode = initializationMode;
	}

	public JpaHibernate getJpaHibernate() {
		return jpaHibernate;
	}

	public void setJpaHibernate(JpaHibernate jpaHibernate) {
		this.jpaHibernate = jpaHibernate;
	}
}
