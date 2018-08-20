#!/bin/bash

#calling this script with input_dir and output file in XML

date=$(date +%s)
outfile=/tmp/bash_scan_outfile-$date

shellcheck ./target.sh -f gcc > "$outfile"
echo $outfile

while read line; do
	filename=$(echo "$line" | cut -d ' ' -f 1)
	type=$(echo "$line" | cut -d ' ' -f 2 | tr -d ':')
	ruleName=$(echo "$line" | cut -d ' ' -f 3-100)
	echo "<rule>"
	echo "<key>$filename</file>" 
	echo "<severity>$type</type>" 
	echo "<description>$ruleName</rule>" 
	echo "<key>KEY</key>"
	echo "<name>NAME</name>"
	echo "<debt>5min</debt>"
	echo "<tags></tags>"
	echo "<debt2></debt2>"
	echo "debtCalcType>DEBTCALCTYPE</debtCalcType>"
        echo "<status>BETA</status>"
	echo "<debtType>ARCHITECTURE_RELIABILITY</debtType>"
	echo "<url></url>"
	echo "</rule>"
	
done < "$outfile"

rm "$outfile"


