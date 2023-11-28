#!/bin/bash

out_dir=""
bin_dir=""
version=""
main_class_name=""
classp=""
rgx=""
name="pclib"
package="lu/pcy113/pclib"

# Loop through the arguments
while [ $# -gt 0 ]; do
	case "$1" in
		-version:*)
			version="${1#-version:}"
			;;
		-main:*)
			main_class_name="${1#-main:}"
			echo "Adding Main-Class to Manifest: ${main_class_name}"
			;;
		-cp:*)
			classp="${1#-cp:}"
			echo "Compiling with classpath: ${classp}"
			;;
		-regex:*)
			rgx="${1#-rgx:}"
			echo "File Selector set to: ${rgx}"
			;;
		*)
			# Unknown argument
			echo "Unknown argument: $1; use -version:X, -main:X, -cp:X, -regex:X; default regex:*.java"
			exit 1
			;;
	esac
	shift
done

# Check if version is provided
if [ -z ${version} ];
then
  version=$(sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//' < ../src/${package}/version.txt)
  echo "No argument provided; using current version: ${version}"
else
  #version=$1
  echo "Using provided version: ${version}"
fi

if [ -z ${rgx} ];
then
	rgx="^((?!PrivateMain|txt$).)*$"
	echo "Using default file selector for compilation: ${rgx}"
else
	echo "Using provided file selector for compilation: ${rgx}"
fi

function check {
	if [ $1 -eq 0 ];
	then
		echo $2
	else
		echo $3
		exit 1
	fi
}

src_dir="../src/"
new_version=$(echo "${version}" | tr '.' '_')
out_dir="./${new_version}"
bin_dir="${out_dir}/bin"
manifest="${out_dir}/MANIFEST.MF"

# Create the bin directory
mkdir -p "${bin_dir}"
check $? "Created output binary directory: ${bin_dir}" "Failed to create output binary directory. Aborted."

# Creating and writing the MANIFEST.MF
echo "Manifest-Version: 1.0" > ${manifest}
check $? "Created JAR manifest to: ${manifest}" "Failed to create JAR manifest. Aborted."

# Check if main class is provided
if ! [ -z ${main_class_name} ];
then
  echo "Main-Class: ${main_class_name}" >> ${manifest}
  check $? "Added Main Class to JAR manifest: ${main_class_name}" "Failed to add Main Class to JAR manifest. Aborted."
fi

# Compile sources
# Find .java files
find ${src_dir} -type f | grep -P ${rgx} > "${out_dir}/sources.txt"
check $? "Sources collected from ${src_dir} to ${out_dir}" "Couldn't find ${rgx} files in ${src_dir}"

# Compile Java files
if [ -z $classp ]
then
	javac -nowarn -d "${bin_dir}" "@${out_dir}/sources.txt" 2>&1
	check $? "Compilation done to ${bin_dir}/" "Compilation failed. Aborted."
else
	javac -nowarn -d "${bin_dir}" -cp "${classp}" "@${out_dir}/sources.txt" 2>&1
	check $? "Compilation done to ${bin_dir}/" "Compilation failed. Aborted."
fi

# Add to JAR file
jar cvfm "${out_dir}/${name}-${version}.jar" "${manifest}" -C "${bin_dir}" . > "/dev/null" 2>&1
check $? "JAR file compressed to: ${out_dir}/${name}-${version}.jar" "JAR file compression failed. Aborted."
