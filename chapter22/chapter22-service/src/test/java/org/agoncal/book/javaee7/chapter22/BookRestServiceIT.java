package org.agoncal.book.javaee7.chapter22;

import com.sun.net.httpserver.HttpServer;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Antonio Goncalves
 *         APress Book - Beginning Java EE 7 with Glassfish 4
 *         http://www.apress.com/
 *         http://www.antoniogoncalves.org
 *         --
 */
public class BookRestServiceIT {

  // ======================================
  // =             Attributes             =
  // ======================================

  private static HttpServer server;
  private static URI uri = UriBuilder.fromUri("http://localhost/chapter22-service-1.0/rs").port(8080).build();
  private static Client client = ClientFactory.newClient();

  private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><book><description>Science fiction comedy book</description><illustrations>false</illustrations><isbn>1-84023-742-2</isbn><nbOfPage>354</nbOfPage><price>12.5</price><title>The Hitchhiker's Guide to the Galaxy</title></book>";

  // ======================================
  // =              Unit tests            =
  // ======================================


  @Test
  public void shouldMarshallABook() throws JAXBException {
    // given
    Book book = new Book("The Hitchhiker's Guide to the Galaxy", 12.5F, "Science fiction comedy book", "1-84023-742-2", 354, false);
    StringWriter writer = new StringWriter();
    JAXBContext context = JAXBContext.newInstance(Book.class);
    Marshaller m = context.createMarshaller();
    m.marshal(book, writer);

    // then
    assertEquals(XML, writer.toString());
  }

  @Test
  public void shouldMarshallAListOfBooks() throws JAXBException {
    Books books = new Books();
    books.add(new Book("The Hitchhiker's Guide to the Galaxy", 12.5F, "Science fiction comedy book", "1-84023-742-2", 354, false));
    books.add(new Book("The Hitchhiker's Guide to the Galaxy", 12.5F, "Science fiction comedy book", "1-84023-742-2", 354, false));
    StringWriter writer = new StringWriter();
    JAXBContext context = JAXBContext.newInstance(Books.class);
    Marshaller m = context.createMarshaller();
    m.marshal(books, writer);
  }


  @Test @Ignore
  public void shouldCreateABook() throws JAXBException {
    // given
    Book book = new Book("The Hitchhiker's Guide to the Galaxy", 12.5F, "Science fiction comedy book", "1-84023-742-2", 354, false);
    StringWriter writer = new StringWriter();
    JAXBContext context = JAXBContext.newInstance(Book.class);
    Marshaller m = context.createMarshaller();
    m.marshal(book, writer);

    // when
    Response response = client.target(uri).path("//books").request().post(Entity.entity(book, "application/xml"));

    // then
    assertEquals(201, response.getStatus());
    assertTrue(response.getLocation().toString().startsWith("http://localhost:8080/chapter22-service-1.0/rs//books"));

    // when
    response = client.target(response.getLocation()).request(MediaType.APPLICATION_XML).get();

    // then
    assertEquals(200, response.getStatus());
    assertTrue(response.hasEntity());

    System.out.println("########## " + response.getEntity());

    book = response.readEntity(Book.class);
    assertEquals("The Hitchhiker's Guide to the Galaxy", book.getTitle());
    assertEquals("Science fiction comedy book", book.getDescription());
  }
}