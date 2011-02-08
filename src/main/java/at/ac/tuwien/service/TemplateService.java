package at.ac.tuwien.service;

import java.io.File;
import java.util.List;

public interface TemplateService {

    public File generateTest(String filename, String uuid);

    public String checkGeneratedTest(File file);

    public File createTestSuiteZip(List<File> files);

}
