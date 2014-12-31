# ixa-pipe-wikifydomain

*ixa-pipe-wikifydomain* is a module to adapt the output of
 *[ixa-pipe-wikify](https://github.com/ixa-ehu/ixa-pipe-wikify)* to a
 domain. Given a list of categories for a specific domain,
 *ixa-pipe-wikifydomain* module takes a [NAF
 document](http://wordpress.let.vupr.nl/naf/) containing *wf*, *term*
 and *mark* elements as input, recognizes wikified terms (references
 to DBpedia) for a domain, and outputs a NAF document with those
 references on *markables* element, as `<markables
 source="in-domain-wikiterms">` or `<markables
 source="out-of-domain-wikiterms">`.

*ixa-pipe-wikifydomain* is part of IXA pipes, a [multilingual NLP
 pipeline](http://ixa2.si.ehu.es/ixa-pipes) developed by the [IXA NLP
 Group](http://ixa.si.ehu.es/Ixa).


### Module contents

The contents of the module are the following:

    + src/   	    java source code of the module
    + pom.xml 	    maven pom file wich deals with everything related to compilation and execution of the module
    + README.md	    this README file
    + Furthermore, the installation process, as described in the README.md, will generate another directory:
    target/	    it contains binary executable and other directories


## INSTALLATION

Installing the *ixa-pipe-wikifydomain* requires the following steps:

*If you already have installed in your machine the Java 1.7+ and MAVEN
3, please go to [step
3](#3-download-and-install-statistical-backend---dbpedia-spotlight)
directly. Otherwise, follow the detailed steps*

### 1. Install JDK 1.7

If you do not install JDK 1.7 in a default location, you will probably
need to configure the PATH in .bashrc or .bash_profile:
```
export JAVA_HOME=/yourpath/local/java17
export PATH=${JAVA_HOME}/bin:${PATH}
```


If you use tcsh you will need to specify it in your .login as follows:
```
setenv JAVA_HOME /usr/java/java17
setenv PATH ${JAVA_HOME}/bin:${PATH}
```


If you re-login into your shell and run the command
```
java -version
```


you should now see that your JDK is 1.7.


### 2. Install MAVEN 3

Download MAVEN 3 from
```
wget http://ftp.udc.es/apache/maven/maven-3/3.0.4/binaries/apache-maven-3.0.4-bin.tar.gz
```

Now you need to configure the PATH. For Bash Shell:
```
export MAVEN_HOME=/yourpath/local/apache-maven-3.0.4
export PATH=${MAVEN_HOME}/bin:${PATH}
```

For tcsh shell:
```
setenv MAVEN3_HOME ~/local/apache-maven-3.0.4
setenv PATH ${MAVEN3}/bin:{PATH}
```

If you re-login into your shell and run the command
```
mvn -version
```

You should see reference to the MAVEN version you have just installed plus the JDK 7 that is using.


### 3. Get module source code
```
git clone https://github.com/ixa-ehu/ixa-pipe-wikifydomain.git
```


### 4. Compile
```
cd ixa-pipe-wikifydomain
mvn clean package
```

This step will create a directory called 'target' which contains
various directories and files. Most importantly, there you will find
the module executable:
```
ixa-pipe-wikifydomain-1.0.0.jar
```

This executable contains every dependency the module needs, so it is
completely portable as long as you have a JVM 1.7 installed.


### 5. Download required DBpedia categories information

This module requires external DBpedia information (i.e. information about categories). Download the required files and place them where the executable jar is located, under 'resources' folder, following these steps:
```
cd target
mkdir resources
cd resources
wget http://ixa2.si.ehu.es/ixa-pipes/models/wikipedia-db.tar.gz
wget http://ixa2.si.ehu.es/ixa-pipes/models/yago2-categories-db.tar.gz
wget http://ixa2.si.ehu.es/ixa-pipes/models/dbpedia-sfCounts-db.tar.gz
tar xzvf wikipedia-db.tar.gz
tar xzvf yago2-categories-db.tar.gz
tar xzvf dbpedia-sfCounts-db.tar.gz
```


## USAGE

The *ixa-pipe-wikifydomain* requires a NAF document (with *wf*, *term*
and *mark* elements) as standard input and outputs NAF through
standard output. You can get the necessary input for
*ixa-pipe-wikifydomain* by piping
*[ixa-pipe-tok](https://github.com/ixa-ehu/ixa-pipe-tok)*,
*[ixa-pipe-pos](https://github.com/ixa-ehu/ixa-pipe-pos)* and
*[ixa-pipe-wikify](https://github.com/ixa-ehu/ixa-pipe-wikify)* as
shown in the example below.


It also requires a file containing a list of YAGO2 categories (one
category per line).
    

There are several parameters:
+ **-c** (required): specify the categories file as parameter.
+ **-t** (optional): use this parameter to set a threshold to filter out-of-domain terms (default value is 1.0).

You can call to *ixa-pipe-wikifydomain* module as follows:
```
cat text.txt | ixa-pipe-tok | ixa-pipe-pos | ixa-pipe-wikify | java -jar ixa-pipe-wikifydomain-1.0.0.jar -c $CATEGORIES_FILE
```
or
```
cat text.txt | ixa-pipe-tok | ixa-pipe-pos | ixa-pipe-wikify | java -jar ixa-pipe-wikifydomain-1.0.0.jar -c $CATEGORIES_FILE -t 0.5
```

For more options running *ixa-pipe-wikifydomain*:

    java -jar ixa-pipe-wikifydomain-1.0.0.jar -h


#### Contact information

    Arantxa Otegi
    arantza.otegi@ehu.es
    IXA NLP Group
    University of the Basque Country (UPV/EHU)
    E-20018 Donostia-San Sebastián


