#!/bin/bash
lines=$(grep -E "^v[[:space:]]" $1.out);
echo "SAT" > $1
b=0;
while read -r line; do
   if [[ $b -eq 0 ]]
   then
      echo -n "${line:2}" >> $1
      b=1
   else
      echo -n "${line:1}" >> $1
   fi
done <<< "$lines"
