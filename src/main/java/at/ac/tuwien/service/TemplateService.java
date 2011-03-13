package at.ac.tuwien.service;

import java.io.File;
import java.util.List;

import at.ac.tuwien.domain.Profile;

public interface TemplateService {

    public File generateTest(String filename, String uuid);

    public String checkGeneratedTest(File file);

    public File generateVcardExport(List<Profile> profiles);

    public File generateTestsuite(List<File> files);

    public File createTestSuiteZip(List<File> files);

}
