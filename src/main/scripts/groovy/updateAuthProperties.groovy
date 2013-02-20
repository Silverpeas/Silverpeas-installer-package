#!/usr/bin/env groovy

final String OldAuthPropertiesPath = "${SILVERPEAS_HOME}/properties/com/stratelia/silverpeas/authentication/"
final String AuthPropertiesPath    = "${SILVERPEAS_HOME}/properties/org/silverpeas/authentication/"

def updateAllAuthPropertiesIn(String directory) {
  final String TempSuffix            = '.tmp'
  final String TemplateToReplace     = 'com.stratelia.'
  File authDir = new File(directory)
  if (authDir.exists() && authDir.isDirectory()) {
    authDir.eachFile { properties ->
      if (properties.isFile()) {
        FileWriter updatedProperties = new FileWriter(properties.path + TempSuffix)
        new FileReader(properties).transformLine(updatedProperties) { line ->
          if (line.contains(TemplateToReplace))
            line = line.replaceAll(TemplateToReplace, 'org.')
          if (!line.contains('SQLPasswordEncryption'))
            line
        }
        properties.delete()
        new File(properties.path + TempSuffix).renameTo(properties)
      }
    } 
  } 
}

updateAllAuthPropertiesIn OldAuthPropertiesPath
updateAllAuthPropertiesIn AuthPropertiesPath

