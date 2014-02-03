catechis
========

  Natural language learning with spaced repetition.  
  
  The jar is used in the Indoct Struts web application for learning foreign langugaes using a spaced repetition algorithm.  This package was created with Java 1.4.2.  Therefore, mostly there are no generics or other features introduced after this release.

### Installation Instructions ###

Requirements:

Most of the libraries required are in the lib folder.
Catechis also requires Junit to be on your classpath when you run the build.xml script.

To create the jar, run the dist ant target like this:

<code>bash$ ant dist</code>

The jar will be created in the dist directory specified in the build file.

```xml
<property name="dist.dir" location="dist"/>
```

If the folder doesn't exists, it will be created in the project directory with the default setting.
