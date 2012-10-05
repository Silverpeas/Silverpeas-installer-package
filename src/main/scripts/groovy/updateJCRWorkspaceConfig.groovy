import groovy.xml.*

String workspaceConfPath = SILVERPEAS_DATA_HOME + "/jackrabbit/workspaces/jackrabbit/workspace.xml"
def workspaceConf = null
def slurper = new XmlSlurper()
slurper.setKeepWhitespace(true)
try {
  workspaceConf = slurper.parse(new File(workspaceConfPath))
} catch (Exception ex) {
  return 1;
}
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

