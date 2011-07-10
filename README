# Setup

* Adjust API token in src/main/ressources/config.properties
* Compile the application with mvn clean install
* start the application with mvn jetty:run


# Usage

## Import Data

### Import profiles manually
* open http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.view.profiledata.AddProfile
* insert all desired data
* click on the submit button

### Import profiles via csv
* open http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.view.profiledata.BatchImport
* select the file and upload it with a click on the upload button

### Import profiles via API
* there is an API call that is described on the front page
* there is also an example in src/test/java/at/ac/tuwien/ExternalImporter.java taht uses this API

## Import templates
* open http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.view.templateduplication.ImportTemplates
* Upload the desired templates; some sample templates can be found in the templates folder; the according sample pages can be found in the folder testpage

## Generate templates
* open http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.view.templateduplication.TemplateGenerator
* select one user
* select at least one template (the order of the selected templates matters)
* generate the templates with a click on the submit button

## Execute templates
* copy the downloaded file (Generator.java) into the tests folder of the selenium runner and execute the run.sh

## Gather information about people
* there is a rudimentary profile search implemented under http://localhost:8080/wicket/bookmarkable/at.ac.tuwien.ProfileFinder
* in order to use it, you have to insert a facebook access token, that can be found at the end of URL for a sample call of the Graph API
* you can enter either names of stored users or individuals;
* the functionality of the profile finder changes, as the major social networks change or restrict their api




