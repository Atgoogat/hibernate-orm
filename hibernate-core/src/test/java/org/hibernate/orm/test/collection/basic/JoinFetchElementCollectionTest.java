/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.collection.basic;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.FailureExpected;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DomainModel(
		annotatedClasses = {
				Contact.class, EmailAddress.class, User.class
		}
)
@SessionFactory
public class JoinFetchElementCollectionTest {

	@Test
	@TestForIssue(jiraKey = "HHH-8206")
	@FailureExpected(jiraKey = "HHH-8206", reason = "This is not explicitly supported, however should arguably throw an exception")
	public void testJoinFetchesByPath(SessionFactoryScope scope) {
		Set<EmailAddress> emailAddresses = new HashSet<EmailAddress>();
		emailAddresses.add( new EmailAddress( "test1@test.com" ) );
		emailAddresses.add( new EmailAddress( "test2@test.com" ) );
		emailAddresses.add( new EmailAddress( "test3@test.com" ) );

		// Session 1: Insert a user with email addresses but no emailAddresses2
		scope.inTransaction(
				session -> {
					User user = new User();
					user.setName( "john" );
					Contact contact = new Contact();
					contact.setName( "John Doe" );
					contact.setEmailAddresses( emailAddresses );
					contact = (Contact) session.merge( contact );
					user.setContact( contact );
					user = (User) session.merge( user );
				}
		);
		// Session 2: Retrieve the user object and check if the sets have the expected values
		scope.inTransaction(
				session -> {
					final String qry = "SELECT user "
							+ "FROM User user "
							+ "LEFT OUTER JOIN FETCH user.contact "
							+ "LEFT OUTER JOIN FETCH user.contact.emailAddresses2 "
							+ "LEFT OUTER JOIN FETCH user.contact.emailAddresses";
					User user = (User) session.createQuery( qry ).uniqueResult();
					assertEquals( emailAddresses, user.getContact().getEmailAddresses() );
					assertTrue( user.getContact().getEmailAddresses2().isEmpty() );
				}
		);
	}

	@Test
	@TestForIssue(jiraKey = "HHH-5465")
	public void testJoinFetchElementCollection(SessionFactoryScope scope) {
		Set<EmailAddress> emailAddresses = new HashSet<EmailAddress>();
		emailAddresses.add( new EmailAddress( "test1@test.com" ) );
		emailAddresses.add( new EmailAddress( "test2@test.com" ) );
		emailAddresses.add( new EmailAddress( "test3@test.com" ) );

		// Session 1: Insert a user with email addresses but no emailAddresses2
		scope.inTransaction(
				session -> {
					User user = new User();
					user.setName( "john" );
					Contact contact = new Contact();
					contact.setName( "John Doe" );
					contact.setEmailAddresses( emailAddresses );
					contact = (Contact) session.merge( contact );
					user.setContact( contact );
					user = (User) session.merge( user );
				}
		);
		// Session 2: Retrieve the user object and check if the sets have the expected values
		scope.inTransaction(
				session -> {
					final String qry = "SELECT user "
							+ "FROM User user "
							+ "LEFT OUTER JOIN FETCH user.contact c "
							+ "LEFT OUTER JOIN FETCH c.emailAddresses2 "
							+ "LEFT OUTER JOIN FETCH c.emailAddresses";
					User user = (User) session.createQuery( qry ).uniqueResult();
					assertEquals( emailAddresses, user.getContact().getEmailAddresses() );
					assertTrue( user.getContact().getEmailAddresses2().isEmpty() );
				}
		);
	}

}
