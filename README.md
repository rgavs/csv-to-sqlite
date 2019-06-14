# csvsqlite

Java application to consume a CSV file, parse the data and insert to a SQLite In-Memory database.

**Key Tools**
- Maven
- org.xerial / sqlite-jdbc
- org.hsqldb / hsqldb

Install steps:
```
git clone git@github.com:rgavs/csv-to-sqlite.git
OR
git clone https://github.com/rgavs/csv-to-sqlite.git
mvn install
```

I have been unable to complete the challenge. Nonetheless I believe I have all of
the main functions written. I was able to insert about 500 rows a minute ago, which stopped at
a single quote literal `'`, and I attempted to fix. However then failed to run properly again.

I have been using Intellij Idea as my IDE, with it's automated tools. Unfortunately, I have been
unable to recreate the runtime on the commandline. I hope this effort is still acceptable.
Command should be:

``` javac -cp "libs/*" src/main/java/csvtosqlite.java```

but it is not running properly
