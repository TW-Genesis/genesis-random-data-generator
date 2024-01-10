# Data Generator

This is a utility used to generate fake yet realistic data for genesis. It uses different ontologies to generate this data. Output is stored in TTL file format.



### To generate the jar
`./gradlew shadowJar`

This will generate fat jar with all dependencies. You can find the jar inside `build/libs` folder.

### To run the jar
`java -jar data-generator-1.0-SNAPSHOT-all.jar -ec 10`

here -ec is command argument to denote experiment count

You can also provide -dir for directory path and -N for number of data storage server nodes. Default is `output` and 1 node respectively. 


## Future work
in this version of data-generator, there is no mass spec related data
