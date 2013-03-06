#! /usr/bin/groovy

import java.util.jar.JarFile
import groovy.xml.StreamingMarkupBuilder

String version = null

def checkServerHome(String serverHome) {

  if (serverHome == null || serverHome.trim().isEmpty()) {
    println "Missing parameter 'server home'"
    System.exit(1)
  } else if (!new File(serverHome).exists()) {
    println "The server home $serverHome doesn't exist"
    System.exit(1)
  }

  if (! Const.MODIFY_FILES)
  println "WARNING: This is a dry run. No files modified!"

}

def checkVersion(String serverHome) {

  JarFile runjar = new JarFile("$serverHome/../../bin/run.jar")
  java.util.jar.Manifest m = runjar.getManifest()

  java.util.jar.Attributes a = m.getMainAttributes()
  version = a.getValue("Specification-Version").trim()

  println "JBoss AS Version: $version"

  if (!version.equals("6.1.0.Final") && !version.equals("6.0.0.Final")) {
    println "Not supported JBoss AS version ($version)"
    System.exit(2)
  }
}

def configureHornetQ(String serverHome) {
  println "Configure HornetQ..."
  setUpInetAddress(serverHome)
  autoSessionReattachment(serverHome)
  println "... configured."
}

def setUpInetAddress(String serverHome) {
  String hornetqJMSConfigPath = serverHome + "/deploy/hornetq/hornetq-configuration.xml"
  def slurper = new XmlSlurper()
  slurper.setKeepWhitespace(true)
  def configuration = slurper.parse(new File(hornetqJMSConfigPath))
  def hostParams = configuration.'**'.grep { it.@value.text() == '${jboss.bind.address:localhost}' }
  if (!hostParams.empty) {
    println "setting up the listening addesss at localhost for HornetQ..."
    hostParams.each { it.@value = 'localhost' }

    new StreamingMarkupBuilder(useDoubleQuotes: true).bind {
      namespaces << ["": "urn:hornetq"]
      out << configuration
    }.writeTo(new File(hornetqJMSConfigPath).newWriter())
    println 'done'
  }
}

def autoSessionReattachment(String serverHome) {
  println "setting up the auto session reattachment in HornetQ..."
  String hornetqJMSConfigPath = serverHome + "/deploy/hornetq/hornetq-jms.xml"
  def slurper = new XmlSlurper()
  slurper.setKeepWhitespace(true)
  def configuration = slurper.parse(new File(hornetqJMSConfigPath))
  def connectionFactories = configuration.'connection-factory'
  connectionFactories.appendNode {
    'confirmation-window-size'(10000000)
  }

  new StreamingMarkupBuilder(useDoubleQuotes: true).bind {
    namespaces << ["": "urn:hornetq"]
    out << configuration
  }.writeTo(new File(hornetqJMSConfigPath).newWriter())
  println 'done'
}

if (INSTALL_CONTEXT == 'install') {
  checkServerHome(JBOSS_SERVER)
  checkVersion(JBOSS_SERVER)
  configureHornetQ(JBOSS_SERVER)
} else {
  setUpInetAddress(JBOSS_SERVER)
}
