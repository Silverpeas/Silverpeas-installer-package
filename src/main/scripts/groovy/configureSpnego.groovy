#!/usr/bin/groovy
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.PrefixFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter

import java.security.MessageDigest

/**
 * Short name of the product to configure.
 */
ProductName = 'Silverpeas-Spnego';

/**
 * Path of the directory in which the libraries of the new product version are available.
 */
SourceLibsPath = "${SILVERPEAS_HOME}/repository/java";

/**
 * The jarfile in the common/lib that will serve as reference jar to compare the version
 * between the product carried by the installer and the actual one in JBoss.
 */

ReferenceLibDescriptors = [[
    mavenArtifactIds: "spnego-r8",
    webSpnegoHttpFilter: "net.sourceforge.spnego.SpnegoHttpFilter"
], [
    mavenArtifactIds: "silverpeas-spnego",
    webSpnegoHttpFilter: "org.silverpeas.spnego.SpnegoHttpFilter"
]];

/**
 * Below are defined the descriptor of the common library to configure.
 */

JBossCommonLibs = [
    path: "common/lib"
]

def checkServerHome(String serverHome) {

  if (serverHome == null || serverHome.trim().isEmpty()) {
    println "Missing parameter 'server home'"
    System.exit(1)
  } else if (!new File(serverHome).exists()) {
    println "The server home $serverHome doesn't exist"
    System.exit(1)
  }

  if (!Const.MODIFY_FILES)
    println "WARNING: This is a dry run. No files modified!"

}

def printSpnegoConfigurationDetection() {
  println "SSO with Spnego/Kerberos configuration has been detected"
  println "Verifying version of ${ProductName} library ..."
}

void die(String msg) {
  throw new RuntimeException(msg)
}

def getLibFrom(String path) {
  File givenPath = new File(path);
  def libs = [];
  ReferenceLibDescriptors.each { descriptor ->
    FileUtils.listFiles(
        givenPath,
        new PrefixFileFilter(descriptor.mavenArtifactIds),
        TrueFileFilter.TRUE).each { lib ->
      libs.add([
          file: lib,
          descriptor: descriptor
      ])
    };
  }
  if (libs.empty) {
    return null;
  }
  if (libs.size() > 1) {
    def message = "ERROR: several Silverpeas Spnego librairies are detected in same environment:\n";
    libs.each { lib ->
      message += "- " + lib.file.getPath() + "\n";
    }
    message += "Please choose one of the above listed libraries in order to delete manually the other(s)";
    die message;
  }
  return libs[0];
}

String md5sum(File file) {
  MessageDigest digest = MessageDigest.getInstance("MD5")
  file.withInputStream() { input ->
    byte[] buffer = new byte[8192]
    int read;
    while ((read = input.read(buffer)) > 0) {
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
    } catch (Exception ex) {
      die "ERROR: cannot remove a previous temporary directory: ${ex.message}"
    }
  if (!BACKUP_DIR.mkdirs())
    die 'ERROR: cannot create temporary directory!'
}

def tearDownBackup() {
  try {
    FileUtils.forceDelete(BACKUP_DIR)
  } catch (Exception ex) {
    println "WARNING: ${ex.message}"
  }
}

def setupSpnegoHttpFilter(String serverHome, def oldLibDescriptor, def newLibDescriptor) {
  if (oldLibDescriptor.descriptor.webSpnegoHttpFilter != newLibDescriptor.descriptor.webSpnegoHttpFilter) {
    File webFile = FileUtils.getFile(serverHome, "deploy/jbossweb.sar/web.xml");
    String content = FileUtils.readFileToString(webFile);
    if (content.contains((String) oldLibDescriptor.descriptor.webSpnegoHttpFilter)) {
      FileUtils.write(webFile, content.replace(
          (String) oldLibDescriptor.descriptor.webSpnegoHttpFilter,
          (String) newLibDescriptor.descriptor.webSpnegoHttpFilter));
      return true;
    }
  }
  return false;
}

def configure() {
  def sourceLibDesc;
  def oldDestinationLibDesc;
  try {
    sourceLibDesc = getLibFrom SourceLibsPath;
    oldDestinationLibDesc = getLibFrom "${JBOSS_HOME}/${JBossCommonLibs.path}";
  } catch (Exception ex) {
    printSpnegoConfigurationDetection();
    println ex.message;
    return 1;
  }

  if (oldDestinationLibDesc == null || !oldDestinationLibDesc.file.isFile()) {
    // No SSO Spnego configuration detected
    return 0;
  }

// SSO Spnego / Kerberos configuration has been detected
  printSpnegoConfigurationDetection();

  if (sourceLibDesc == null || !sourceLibDesc.file.isFile()) {
    println "... error during the verification because the source library is missing or is not a file!";
    println "(${sourceLibDesc.file.getName})";
    return 1;
  }

  String currentMD5 = md5sum sourceLibDesc.file;
  String newerMD5 = md5sum oldDestinationLibDesc.file;
  if (currentMD5 != newerMD5) {
    setUpBackup();

    println "--- Backup of old library ${oldDestinationLibDesc.file.getPath()}";
    File oldDestinationLibBackup = FileUtils.getFile(BACKUP_DIR, oldDestinationLibDesc.file.getName());
    try {
      FileUtils.moveFile(oldDestinationLibDesc.file, oldDestinationLibBackup);
    } catch (Exception ex) {
      println "... error during the backup!";
      println ex.message;
      return 1;
    }

    File destinationLib = FileUtils.getFile(oldDestinationLibDesc.file.getParentFile(), sourceLibDesc.file.getName());
    println "--- Copying ${sourceLibDesc.file.getPath()} into ${destinationLib.getParentFile().getPath()}";
    try {
      FileUtils.copyFile(sourceLibDesc.file, destinationLib);
    } catch (Exception ex) {
      println "... error during the copy!";
      println ex.message;
      println "Restoring the library backup";
      FileUtils.copyFile(oldDestinationLibBackup, oldDestinationLibDesc.file);
      return 1;
    }

    println "--- Applying right settings on SpnegoHttpFilter";
    try {
      if (setupSpnegoHttpFilter(JBOSS_SERVER, oldDestinationLibDesc, sourceLibDesc)) {
        println "settings have been configured";
      } else {
        println "settings were already configured";
      }
    } catch (Exception ex) {
      println "... error during the setup of SpnegoHttpFilter!";
      println ex.message;
      println "Deleting the new library";
      FileUtils.forceDelete(destinationLib);
      println "Restoring the old library from the backup";
      FileUtils.copyFile(oldDestinationLibBackup, oldDestinationLibDesc.file);
      return 1;
    }
    println "--- Deleting the backup directory";
    tearDownBackup();
    println "SSO with Spnego/Kerberos configuration done\n";
  } else {
    println "... the configuration is up to date.";
  }
  return 0;
}

if (INSTALL_CONTEXT == 'install') {
  checkServerHome(JBOSS_SERVER);
}

def returnExecutionCode = configure();
if (returnExecutionCode != 0) {
  println "Stop the Silverpeas installation process";
  println "Please checking carefully the Silverpeas installer settings and retry a full Silverpeas install";
  System.exit(returnExecutionCode);
}