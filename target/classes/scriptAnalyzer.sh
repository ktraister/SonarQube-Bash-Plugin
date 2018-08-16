#!/bin/bash

#calling this script with input_dir and output file in XML

date=$(date +%s)
outfile=/tmp/bash_scan_outfile-$date

rm -f "$2"

find "$1" -type f \( -name "*.sh" -o -name "*.bash" -o -name "*.ksh" -o -name "*.zsh" \) -exec shellcheck {} -f gcc \; > "$outfile"

while read -r line; do
	filename=$(echo "$line" | cut -d ' ' -f 1)
	type=$(echo "$line" | cut -d ' ' -f 2 | tr -d ':')
	ruleName=$(echo "$line" | cut -d ' ' -f 3-100)
	echo "<file>$filename</file>" >> "$2"
	echo "<type>$type</type>" >> "$2"
	echo "<rule>$ruleName</rule>" >> "$2"
	echo "" >> "$2"
	
done < "$outfile"

rm "$outfile"


