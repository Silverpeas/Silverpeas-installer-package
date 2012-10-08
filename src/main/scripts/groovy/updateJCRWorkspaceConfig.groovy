import groovy.xml.*

String workspaceConfPath = SILVERPEAS_DATA_HOME + "/jackrabbit/workspaces/jackrabbit/workspace.xml"
def jcrConfigFile = new File(workspaceConfPath)
if(jcrConfigFile.exists() && jcrConfigFile.isFile()) {
  def slurper = new XmlSlurper()
  slurper.setKeepWhitespace(true)
  def workspaceConf = slurper.parse(jcrConfigFile)
  def jdbcDriverByJNDI = workspaceConf.'**'.grep { it.@value.text() == 'javax.naming.InitialContext' }
  println()
  if (jdbcDriverByJNDI.isEmpty()) {
    println "Old JCR workspace configuration detected! => Update it..."
    workspaceConf.PersistenceManager.replaceNode {
        PersistenceManager(class: "org.apache.jackrabbit.core.persistence.bundle.PostgreSQLPersistenceManager") {
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
  println "No JCR workspace configuration to configure"
}
