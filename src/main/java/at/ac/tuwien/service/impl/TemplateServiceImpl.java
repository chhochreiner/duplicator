package at.ac.tuwien.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import at.ac.tuwien.service.TemplateService;

public class TemplateServiceImpl implements TemplateService {

    public TemplateServiceImpl() {
        if (!(new File("appdata/templates")).exists()) {
            new File("appdata/templates").mkdirs();
        }
    }

    @Override
    public File generateTest(String filename, String uuid) {

        VelocityEngine ve = new VelocityEngine();
        ve.init();

        Template t = ve.getTemplate("appdata/templates/studivz.xml");

        VelocityContext context = new VelocityContext();
        context.put("prename", "John");

        File file = new File("appdata/temp/" + filename + uuid);
        try {
            FileWriter writer = new FileWriter(file);

            t.merge(context, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // checkforcomplete(file);

        return file;

    }

    private String checkforcomplete(File file) throws FileNotFoundException, IOException {
        FileReader reader = new FileReader(file);

        BufferedReader bufRead = new BufferedReader(reader);
        String line;
        StringBuffer text = new StringBuffer();

        line = bufRead.readLine();
        while (line != null) {
            text.append(line);
            line = bufRead.readLine();
        }

        Pattern p = Pattern.compile("[$][a-zA-Z_0-9]+");
        Matcher m = p.matcher(text.toString());
        m.lookingAt();

        return ("File could not be processed, there were " + m.groupCount() + " attributes missing: " + m.group());
    }
}
