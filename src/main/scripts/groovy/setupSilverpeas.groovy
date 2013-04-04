/**
 * This script is for preparing the setting of the Silverpeas installation.
 */

import org.apache.commons.io.FileUtils

/**
 * create the hidden silverpeas directory.
 */
File silverpeasDir = new File(HIDDEN_SILVERPEAS_DIR)
if (!silverpeasDir.exists()) {
  FileUtils.forceMkdir(silverpeasDir)
  silverpeasDir.setWritable(true)
  silverpeasDir.setReadable(true)
  silverpeasDir.setExecutable(true)

  if (System.properties['os.name'].toLowerCase().contains('windows'))
    Runtime.getRuntime().exec("attrib +H ${HIDDEN_SILVERPEAS_DIR}")
}
