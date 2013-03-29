#!/usr/bin/groovy

import java.security.MessageDigest
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FileExistsException

/**
 * Short name of the product to upgrade.
 */
ProductName = 'Hibernate'

/**
 * Path of the directory in which the libraries of the new product version are available.
 */
SourceLibsPath = "${SILVERPEAS_HOME}/bin/jar/hibernate"

/**
 * The jarfile in the common/lib that will serv as reference jar to compare the version
 * between the product carried by the installer and the actual one in JBoss.
 */
ReferenceLib = "hibernate-core.jar"

/**
 * Libraries of tier-party product embedded in the JBoss AS are distributed into several
 * locations in a JBoss installation (JBOSS_HOME): common/libs, client/, and server/<profile>/deploy
 * for RAs.
 * Below are defined the descriptors on the libraries to upgrade.
 */
def JBossLibs = [
  path: "lib",
  copied: []
]

def JBossCommonLibs = [
  path: "common/lib",
  copied: []
]

def JBossClientLibs = [
  path: "client",
  copied: []
]

/**
 * The descriptors carrying the information about the libraries implied in the upgrade.
 */
JBossLibsInUpdate = [JBossLibs, JBossCommonLibs, JBossClientLibs]

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
  File source = new File("${SourceLibsPath}/${descriptor.path}")
  File destination = new File("${JBOSS_HOME}/${descriptor.path}")
  source.listFiles().each { lib ->
    try {
      File actualLib = new File("${destination.path}/${lib.name}")
      if (actualLib.exists()) {
        FileUtils.moveToDirectory(actualLib, BACKUP_DIR, false)
        descriptor.copied << "${descriptor.path}/${lib.name}"
      }
    } catch(FileExistsException ex) {
    }
    if (lib.isFile())
      FileUtils.copyFileToDirectory(lib, destination)
    else if (lib.isDirectory())
      FileUtils.copyDirectoryToDirectory(lib, destination)
  }
}

def restoreLibs(descriptor) {
  File dist = new File(SourceLibsPath + '/' + descriptor.path)
  File destination = new File(JBOSS_HOME + '/' + descriptor.path)
  dist.listFiles().each { lib ->
    try {
      File source = new File(BACKUP_DIR.path + '/' + lib.name)
      if (source.isFile())
        FileUtils.copyFileToDirectory(source, destination)
      else (source.isDirectory)
        FileUtils.copyDirectoryToDirectory(source, destination)
    } catch(Exception ex) {
      println "WARNING: cannot restore ${lib}. ${ex.message}"
      RESTORE_ERROR = true
    }
  }
}

String currentMD5 = md5sum "${SourceLibsPath}/${JBossCommonLibs.path}/${ReferenceLib}"
String newerMD5   = md5sum "${JBOSS_HOME}/${JBossCommonLibs.path}/${ReferenceLib}"
if (currentMD5 != newerMD5) {
  try {
    println "Old ${ProductName} version detected: upgrade it..."
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
