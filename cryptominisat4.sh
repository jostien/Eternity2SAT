#!/bin/bash
cd /home/jostie/tools/cryptominisat/build

if [ "$#" -eq 2 ];
then
   ./cryptominisat4 -t 4 $1 | tee $2.out
fi

if [ "$#" -eq 3 ];
then
   ./cryptominisat4 --random $3 -t 4 $1 | tee $2.out
fi

lines=$(grep -E "^v[[:space:]]" $2.out);
echo "SAT" > $2
b=0;
while read -r line; do
   if [[ $b -eq 0 ]]
   then
      echo -n "${line:2}" >> $2
      b=1
   else
      echo -n "${line:1}" >> $2
   fi
done <<< "$lines"
