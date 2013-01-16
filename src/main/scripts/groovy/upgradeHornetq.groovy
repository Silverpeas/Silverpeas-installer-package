#!/usr/bin/groovy

import java.security.MessageDigest
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FileExistsException

/**
 * Short name of the product to upgrade.
 */
ProductName = 'hornetq'

/**
 * Path of the directory in which the libraries of the new product version are available.
 */
SourceLibsPath = "${SILVERPEAS_HOME}/bin/jar/hornetq"

/**
 * Libraries of tier-party product embedded in the JBoss AS are distributed into several
 * locations in a JBoss installation (JBOSS_HOME): common/libs, client/, and server/<profile>/deploy
 * for RAs.
 * Below are defined the descriptors on the libraries to upgrade.
 */
def JBossCommonLibs = [
  path: "${JBOSS_HOME}/common/lib",
  libs: ['hornetq-core.jar', 'hornetq-bootstrap.jar', 'hornetq-jboss-as-integration.jar', 
      'hornetq-jms.jar', 'hornetq-logging.jar', 'netty.jar'],
  copied: []
]

def JBossClientLibs = [
  path: "${JBOSS_HOME}/client",
  libs: ['hornetq-core-client.jar', 'hornetq-jms-client.jar', 'netty.jar'],
  copied: []
]

def JBossRAs = [
  path: "${JBOSS_HOME}/server/${JBOSS_SERVER_PROFILE}/deploy",
  libs: ['jms-ra.rar'],
  copied: []
]

/**
 * The descriptors carrying the information about the libraries implied in the upgrade.
 */
JBossLibsInUpdate = [JBossCommonLibs, JBossClientLibs, JBossRAs]

void die(String msg) {
  throw new RuntimeException(msg)
}

String md5sum(String f) {
  MessageDigest digest = MessageDigest.getInstance("MD5")
  new File(f).withInputStream() { input ->
    byte[] buffer = new byte[8192]
    int read = 0
    while( (read = input.read(buffer)) > 0) {
      digest.update(buffer, 0, read);
    }
  }
  byte[] md5sum = digest.digest()
  return new BigInteger(1, md5sum).toString(16).padLeft(32, '0')
}

def setUpBackup() {
  BACKUP_DIR = new File("${FileUtils.getTempDirectoryPath()}/${ProductName}")
  RESTORE_ERROR = false
  if (BACKUP_DIR.exists())
    try {
      FileUtils.forceDelete(BACKUP_DIR)
    } catch(Exception ex) {
      die "ERROR: cannot remove a previous temporary directory: ${ex.message}. Stop the upgrade"
    }
  if (!BACKUP_DIR.mkdirs())
    die 'ERROR: cannot create temporary directory! Stop the upgrade'
}

def tearDownBackup() {
  if (!RESTORE_ERROR)
    try {
      FileUtils.forceDelete(BACKUP_DIR)
    } catch(Exception ex) {
      println "WARNING: ${ex.message}"
    }
}

def copyLibs(descriptor) {
  descriptor.libs.each { lib ->
    try {
      FileUtils.moveToDirectory(new File("${descriptor.path}/${lib}"), BACKUP_DIR, false)
    } catch(FileExistsException ex) {
    }
    descriptor.copied << lib
    File source = new File("${SourceLibsPath}/${lib}")
    if (source.isFile())
      FileUtils.copyFileToDirectory(source, new File("${descriptor.path}"))
    else if (source.isDirectory())
      FileUtils.copyDirectoryToDirectory(source, new File("${descriptor.path}"))
  }
}

def restoreLibs(descriptor) {
  descriptor.copied.each { lib ->
    try {
      File source = new File("${BACKUP_DIR.path}/${lib}")
      if (source.isFile())
        FileUtils.copyFileToDirectory(source, new File("${descriptor.path}"))
      else (source.isDirectory)
        FileUtils.copyDirectoryToDirectory(source, new File("${descriptor.path}"))
    } catch(Exception ex) {
      println "WARNING: cannot restore ${lib}. ${ex.message}"
      RESTORE_ERROR = true
    }
  }
}

String libToCheck = JBossLibsInUpdate[0].libs[0]
String currentMD5 = md5sum "${SourceLibsPath}/${libToCheck}"
String newerMD5   = md5sum "${JBossLibsInUpdate[0].path}/${libToCheck}"
if (currentMD5 != newerMD5) {
  try {
    println 'Old HornetQ version detected: upgrade it...'
    setUpBackup()
    try {
      JBossLibsInUpdate.each { descriptor ->
        copyLibs descriptor
      }
    } catch(Exception ex) {
      println "ERROR: ${ex.message}. Restores the original ${ProductName}..."
      JBossLibsInUpdate.each { descriptor ->
        restoreLibs descriptor
      }
      if (RESTORE_ERROR)
        println "INFO: Some error has occured while restoring the original ${ProductName} in JBoss.\n" +
          "Theses libraries are in ${BACKUP_DIR.path}, please restore them by hand"
      die "An error occured while upgrading ${ProductName}. Stop the upgrade."
    } finally {
      tearDownBackup()
    }
  } catch(Exception ex) {
    println ex.message
    return 1
  }
}
return 0
