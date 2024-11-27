#!/bin/bash

if [ "$#" -ne 1 ]; then
  echo "Please provide exactly one argument - path to the model file."
  exit 1;
else
  echo "The provided file is: $1"
fi

file="$1"
dmnxml=$(cat $file)
dmnjson=$(jq -Rs '.' < $file)

curl -H "Content-Type: application/xml" -X POST http://localhost:8080/jitdmn/schema/form/ -d "${dmnxml}" | jq .