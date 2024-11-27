#!/bin/bash

if [ "$#" -ne 2 ]; then
  echo "Please provide exactly two arguments:"
  echo "#1 - path the model file: ./my/project/model.dmn"
  echo "#2 - data for the model in a json format: '{\"n\" : 1, \"m\" : 2}'"
  exit 1;
else
  echo "The provided file is: $1"
fi

file="$1"
dmnjson=$(jq -Rs '.' < $file)
echo $2
curl -H "Content-Type: application/json" -X POST http://localhost:8080/jitdmn -d '{"context": '"${2}"', "model": '"${dmnjson}"'}' | jq