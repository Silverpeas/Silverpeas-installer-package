import org.apache.commons.io.FileUtils

String tickerHomePath = SILVERPEAS_DATA_HOME + '/web/weblib.war/ticker'
File tickerHome = new File(tickerHomePath)
if (tickerHome.exists() && tickerHome.isDirectory()) {
  println()
  tickerHome.eachFileMatch(~/sample_.*/) {
    File aTicker = new File(tickerHomePath + '/' + it.name.replaceAll('sample_', ''))
    if (!aTicker.exists()) {
      println "Generate the ticker ${aTicker.name}"
      FileUtils.copyFile(it, aTicker)
    }
  }
}
