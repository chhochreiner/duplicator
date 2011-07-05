package at.ac.tuwien.view.profiledata;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import at.ac.tuwien.BasePage;
import at.ac.tuwien.ErrorPage;
import at.ac.tuwien.GeneralConstants;
import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.service.DBService;

public class ProfileDetail extends BasePage {

	private static final long serialVersionUID = 7734710518718389158L;

	@SpringBean(name = "DBService")
	private DBService dbService;

	public ProfileDetail(PageParameters parameters) {
		body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

		StringValue uuid = parameters.get("id");

		Map<String, String> data = dbService.fetchProfileData(uuid.toString());



		List<KeyValueEntry> additionalvalues = new ArrayList<KeyValueEntry>();

		List<String> alreadyListet = GeneralConstants.getRequiredKeys();

		if (data == null) {
			PageParameters parameter = new PageParameters();

			parameter.add("error", "Could not find a profile with UUID " + uuid.toString());
			throw new RestartResponseException(ErrorPage.class, parameter);
		}

		final String name = "appdata/images/" + uuid.toString() + ".jpg";

		if (!new File(name).exists()) {
			body.add(new Image("profile-image", new Model<String>("dummy.png")));
		} else {
			body.add(new Image("profile-image", new DynamicImageResource() {
				private static final long serialVersionUID = 199083709778570992L;

				@Override
				protected byte[] getImageData(Attributes arg0) {
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					try {
						InputStream inStream = new FileInputStream(new File(name));
						copy(inStream, outStream);
						inStream.close();
						outStream.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return outStream.toByteArray();
				}

			}));
		}

		body.add(new Label("name", data.get("prename") + " " + data.get("surname")));
		body.add(new Label("emailValue", data.get("email")));
		body.add(new Label("passwordValue", data.get("password")));
		body.add(new Label("birthdayValue", data.get("birthday")));

		for (String key : data.keySet()) {
			if (alreadyListet.contains(key)) {
				continue;
			}
			additionalvalues.add(new KeyValueEntry(key.toString(), data.get(key).toString()));
		}

		body.add(new ListView<KeyValueEntry>("additionalData", additionalvalues) {
			private static final long serialVersionUID = 7734710518718389159L;

			@Override
			protected void populateItem(ListItem<KeyValueEntry> item) {
				KeyValueEntry entry = item.getModelObject();
				item.add(new Label("key", entry.getKey()));
				item.add(new Label("value", entry.getValue()));
			}
		});

		PageParameters parameter = new PageParameters();

		parameter.add("id", data.get("UUID").toString());

		body.add(new BookmarkablePageLink<String>("edit", EditProfile.class, parameter));
		body.add(new BookmarkablePageLink<String>("editFriends", EditFriends.class, parameter));
		body.add(new BookmarkablePageLink<String>("uploadImage", UploadImage.class, parameter));
	}

	private void copy(InputStream source, OutputStream destination)
			throws IOException
			{
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = source.read(buf)) > 0) {
				destination.write(buf, 0, len);
			}
			source.close();
			destination.close();
		} catch (IOException ioe) {
			throw ioe;
		}
			}
}
