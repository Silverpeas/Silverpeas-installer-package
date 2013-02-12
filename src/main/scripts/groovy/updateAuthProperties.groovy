#!/usr/bin/env groovy

final String AuthPropertiesPath = "${SILVERPEAS_HOME}/properties/org/silverpeas/authentication/"
final String TempSuffix         = '.tmp'
final String TemplateToReplace  = 'com.stratelia.'

new File(AuthPropertiesPath).eachFile { properties ->
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
