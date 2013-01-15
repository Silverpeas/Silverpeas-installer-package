import groovy.xml.*

String repositoryConfPath = SILVERPEAS_HOME + "/setup/jackrabbit/repository.xml"
def repositoryConfigFile = new File(repositoryConfPath)
println()
if(repositoryConfigFile.exists() && repositoryConfigFile.isFile()) {
  def slurper = new XmlSlurper()
  slurper.setKeepWhitespace(true)
  def repositoryConf = slurper.parse(repositoryConfigFile)
  println "JCR is being configured for migration"
  def persistenceManager
    switch(DB_SERVERTYPE) {
      case "POSTGRES":
        persistenceManager = "org.apache.jackrabbit.core.persistence.bundle.PostgreSQLPersistenceManager"
        break;
      case "MSSQL":
        persistenceManager = "org.apache.jackrabbit.core.persistence.bundle.MSSqlPersistenceManager"
        break;
      case "ORACLE":
        persistenceManager = "org.apache.jackrabbit.core.persistence.bundle.OraclePersistenceManager"
        break;
      case "H2":
        persistenceManager = "org.apache.jackrabbit.core.persistence.bundle.H2PersistenceManager"
        break;
      default:
        println("Error: the following DB type isn't supported: " + DB_SERVERTYPE)
        return 1
    }
    repositoryConf.Workspace.PersistenceManager.replaceNode {
        PersistenceManager(class: persistenceManager) {
            param(name: "driver", value: "javax.naming.InitialContext")
            param(name: "url", value: "java:/datasources/DocumentStoreDS")
            param(name: "schema", value: JACKRABBIT_SCHEMA)
            param(name: "schemaObjectPrefix", value: "JCR_")            
            param(name: "consistencyCheck", value: "true")
            param(name: "consistencyFix", value: "true")
        }
    }
    repositoryConf.Versioning.PersistenceManager.replaceNode {
        PersistenceManager(class: persistenceManager) {
             param(name: "driver", value: "javax.naming.InitialContext")
            param(name: "url", value: "java:/datasources/DocumentStoreDS")
            param(name: "schema", value: JACKRABBIT_SCHEMA)
            param(name: "schemaObjectPrefix", value: "version_")            
            param(name: "consistencyCheck", value: "true")
            param(name: "consistencyFix", value: "true")
        }
    }
    XmlUtil.serialize(repositoryConf, new File(repositoryConfPath).newWriter())
    println "JCR migration configuration update done"
} else {
  println "No JCR workspace configuration"
}
