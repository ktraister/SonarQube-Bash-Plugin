#!/bin/bash

#calling this script with input_dir and output file in XML

date=$(date +%s)
outfile=/tmp/bash_scan_outfile-$date

if [[ -f $2 ]]; then 
	rm -f "$2"
fi

touch $2

if [[ ! -f $2 ]]; then
	echo "we failed to create $2" > /tmp/lifeboatfile
else 
	echo "we created $2" > /tmp/gadeemfile
	echo "we're reading from $1" >> /tmp/gadeemfile
fi

find "$1" -type f \( -name "*.sh" -o -name "*.bash" -o -name "*.ksh" -o -name "*.zsh" \) -exec shellcheck {} -f gcc \; > "$outfile"

echo "<Objects>" >> "$2"

while read -r line; do
	filename=$(echo "$line" | cut -d ' ' -f 1)
	type=$(echo "$line" | cut -d ' ' -f 2 | tr -d ':')
	ruleName=$(echo "$line" | cut -d ' ' -f 3-100)
	echo "<Object>" >> "$2"
	echo "<File>$filename</File>" >> "$2"
	echo "<Message>$type</Message>" >> "$2"
	echo "<Rule>MYRULES</Rule>" >> "$2"
	echo "<RuleName>$ruleName</RuleName>" >> "$2"
	echo "<Line>LINE_NUMBER</Line>" >> "$2"
	echo "</Object>" >> "$2"
	
done < "$outfile"

echo "</Objects>" >> "$2"

	
rm "$outfile"


