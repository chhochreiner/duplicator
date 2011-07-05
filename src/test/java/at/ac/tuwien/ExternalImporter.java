package at.ac.tuwien;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ExternalImporter {

	public static void main(String[] args) {

		String url1 = "http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.service.impl.ExternalProfile?birthday=01.01.2011&prename=External&surname=Import1&email=mm@gmail.com&password=asdf&avatar=http://fbcdn-photos-a.akamaihd.net/hphotos-ak-ash2/59997_1507274515897_1055330621_1392027_724889_a.jpg";
		String url2 = "http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.service.impl.ExternalProfile?birthday=01.01.2011&prename=External&surname=Import2&email=mm@gmail.com&password=asdf&avatar=http://fbcdn-photos-a.akamaihd.net/hphotos-ak-snc1/5495_1181303406823_1055330621_552048_4691767_a.jpg";
		String url3 = "http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.service.impl.ExternalProfile?birthday=01.01.2011&prename=External&surname=Import3&email=mm@gmail.com&password=asdf&avatar=http://fbcdn-photos-a.akamaihd.net/hphotos-ak-snc6/230512_1040052155630_1055330621_130871_9814_a.jpg";
		String url4 = "http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.service.impl.ExternalProfile?birthday=01.01.2011&prename=External&surname=Import4&email=mm@gmail.com&password=asdf&avatar=http://fbcdn-photos-a.akamaihd.net/hphotos-ak-ash1/18352_1278648360386_1055330621_828551_7767052_a.jpg";
		String url5 = "http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.service.impl.ExternalProfile?birthday=01.01.2011&prename=External&surname=Import5&email=mm@gmail.com&password=asdf&avatar=http://fbcdn-photos-a.akamaihd.net/hphotos-ak-ash4/222367_1023491301619_1055330621_83291_2198_a.jpg";


		try {
			importContact(url1);
			importContact(url2);
			importContact(url3);
			importContact(url4);
			importContact(url5);

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private static void importContact(String url2) throws IOException,
	MalformedURLException {
		URLConnection connection;
		connection = new URL(url2).openConnection();
		connection.connect();
		connection.getContent();
		System.out.println("imported contact");
	}

}
