catechis
========

Natural language learning with spaced repetition.

### Installation Instructions ###

Requirements:

Most of the libraries required are in the lib folder.
Catechis also requires Junit to be on your classpath when you run the build.xml script.

To create the jar, run the dist ant target like this:

bash$ ant dist

The jar will be created in the dist directory specified in the build file.

<property name="dist.dir" location="dist"/>

If the folder doesn't exists, it will be created in the directory containing the project.
