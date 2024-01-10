#!/bin/zsh

databasePath="$FUSEKI_BASE/databases/genesis100"
eval "tdb2.tdbloader --loc $databasePath output/ccp.ttl"
eval "tdb2.tdbloader --loc $databasePath output/massSpec.ttl"
eval "tdb2.tdbloader --loc $databasePath output/transcriptomics.ttl"
