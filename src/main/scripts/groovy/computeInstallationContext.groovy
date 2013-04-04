#! /usr/bin/groovy

/**
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of the GPL, you may
 * redistribute this Program in connection with Free/Libre Open Source Software ("FLOSS")
 * applications as described in Silverpeas's FLOSS exception. You should have received a copy of the
 * text describing the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

import groovy.xml.StreamingMarkupBuilder ;

/**
 * Determine if JBoss has already been configured for Silverpeas or not.
 */
def String jbossConfigFile =  JBOSS_CONF + '/bootstrap/profile.xml';

//System.out.println(jbossConfigFile);

def root = new XmlSlurper().parse(jbossConfigFile);
def propertyTag = root.bean.find { it.@name == 'UserProfileFactory' }.property.find {it.@name == 'applicationURIs' };
def listTag = propertyTag.list.find {it.@elementClass == 'java.net.URI'};
def children = listTag.'value'.findAll();
// System.out.println('children.size ' + children.size());
if(children.size() == 1) {
 // System.out.println('INSTALL');
  gestionVariables.addVariable('INSTALL_CONTEXT', 'install');
  gestionVariables.addVariable('KEY_APPURIS', 'applicationURIs');
} else {
 // System.out.println('UPDATE');
  gestionVariables.addVariable('INSTALL_CONTEXT', 'update');
  gestionVariables.addVariable('KEY_APPURIS', '');
}

