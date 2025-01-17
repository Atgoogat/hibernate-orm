/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */

//$Id: Group.java 7175 2005-06-17 05:23:15Z oneovthafew $
package org.hibernate.orm.test.query.joinfetch;
import java.util.HashMap;
import java.util.Map;

public class Group {
	private String name;
	private Map users = new HashMap();
	
	public Group(String name) {
		this.name = name;
	}

	Group() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map getUsers() {
		return users;
	}

	public void setUsers(Map users) {
		this.users = users;
	}

}
