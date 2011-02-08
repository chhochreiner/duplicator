package at.ac.tuwien.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.wicket.spring.injection.annot.SpringBean;

import at.ac.tuwien.service.DBService;
import at.ac.tuwien.service.TemplateService;

public class TemplateServiceImpl implements TemplateService {

    @SpringBean(name = "DBService")
    public DBService dbService;

    public TemplateServiceImpl() {
        if (!(new File("appdata/templates")).exists()) {
            new File("appdata/templates").mkdirs();
        }
        if (!(new File("appdata/temp")).exists()) {
            new File("appdata/temp").mkdirs();
        }
    }

    @Override
    public File generateTest(String filename, String uuid) {

        VelocityEngine ve = new VelocityEngine();
        ve.init();

        Template t = ve.getTemplate("appdata/templates/" + filename);
        VelocityContext context = new VelocityContext();

        Map<String, String> data = dbService.fetchProfileData(uuid);

        for (String key : data.keySet()) {
            context.put(key, data.get(key));
        }

        fileRemover("appdata/temp/" + filename);

        File file = new File("appdata/temp/" + filename);

        try {
            FileWriter writer = new FileWriter(file);
            t.merge(context, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    @Override
    public String checkGeneratedTest(File file) {
        try {
            FileReader reader = new FileReader(file);

            BufferedReader bufRead = new BufferedReader(reader);
            StringBuffer text = new StringBuffer();

            String line = bufRead.readLine();
            while (line != null) {
                text.append(line);
                line = bufRead.readLine();
            }

            Pattern p = Pattern.compile("[$][a-zA-Z_0-9]+");
            Matcher m = p.matcher(text.toString());
            m.lookingAt();

            String result = "";
            Integer counter = 0;

            while (m.find() || (counter > 10)) {
                if (counter != 0) {
                    result += ", ";
                }
                result += m.group();
                counter++;
            }

            if (counter > 0) {
                return ("The template could no be generated completly, there were " + counter + " attributes missing: " + result);
            } else {
                return ("The test was generated.");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ("The test was generated.");
    }

    public void setDbService(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public File createTestSuiteZip(List<File> files) {

        files.add(generateTestsuite(files));

        byte[] buf = new byte[1024];
        fileRemover("appdata/temp/templates.zip");
        String outFilename = "appdata/temp/templates.zip";

        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

            for (File file : files) {
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (new File(outFilename));
    }

    private File generateTestsuite(List<File> files) {

        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "\n"
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""
                + "\n"
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                + "\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">"
                + "\n"
                + "<head>"
                + "\n"
                + "<meta content=\"text/html; charset=UTF-8\" http-equiv=\"content-type\" />"
                + "\n"
                + "<title>Test Suite</title>"
                + "\n"
                + "</head>"
                + "\n"
                + "<body>"
                + "\n"
                + "<table id=\"suiteTable\" cellpadding=\"1\" cellspacing=\"1\" border=\"1\" class=\"selenium\"><tbody>"
                + "\n" + "<tr><td><b>Test Suite</b></td></tr>";

        for (File file : files) {
            result += "<tr><td><a href=\"" + file.getName() + "\">" + file.getName() + "</a></td></tr>";
        }

        result += "</tbody></table>" + "\n" + "</body>" + "\n" + "</html>";

        try {
            FileWriter fstream = new FileWriter("appdata/temp/suite.xml");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(result);
            out.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (new File("appdata/temp/suite.xml"));
    }

    private void fileRemover(String name) {
        File f = new File(name);
        if (f.exists()) {
            f.delete();
        }
    }
}
