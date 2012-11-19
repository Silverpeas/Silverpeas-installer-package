import groovy.xml.*

String workspaceConfPath = SILVERPEAS_DATA_HOME + "/jackrabbit/workspaces/jackrabbit/workspace.xml"
def jcrConfigFile = new File(workspaceConfPath)
println()
if(jcrConfigFile.exists() && jcrConfigFile.isFile()) {
  def slurper = new XmlSlurper()
  slurper.setKeepWhitespace(true)
  def workspaceConf = slurper.parse(jcrConfigFile)
  def jdbcDriverByJNDI = workspaceConf.'**'.grep { it.@value.text() == 'javax.naming.InitialContext' }
  if (jdbcDriverByJNDI.isEmpty()) {
    println "Old JCR workspace configuration detected! => Update it..."
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
    workspaceConf.PersistenceManager.replaceNode {
        PersistenceManager(class: persistenceManager) {
            param(name: "driver", value: "javax.naming.InitialContext")
            param(name: "url", value: "java:/datasources/DocumentStoreDS")
            param(name: "schema", value: JACKRABBIT_SCHEMA)
            param(name: "schemaObjectPrefix", value: "JCR_")
        }
    }
    XmlUtil.serialize(workspaceConf, new File(workspaceConfPath).newWriter())
    println "JCR workspace configuration update done"
  } else {
    println "The JCR workspace configuration is up to date"
  } 
} else {
  println "No JCR workspace configuration"
}
